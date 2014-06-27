import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/* Dachuan Huang, June 2014 */
/* hdc1112@gmail.com */
/* This program is one MapReduce implementation of Apriori algorithm */
/* It implements the algorithm from the link below */
/* http://www.ijric.org/volumes/Vo12/Vol12No7.pdf */

/* This program is tested in Hadoop 2.2.0 */
/* Input files are put in the input folder */
/* Each input file will be treated as one FileSplit */
/* Each input file's format is a boolean matrix */
/* Row means transaction, column means item */
/* For example */
/* 1 0 1 1 1 1 0 */
/* 0 0 1 1 0 0 0 */
/* 1 1 1 0 1 0 1 */
/* 1 1 0 1 0 1 1 */

/* 1st Phase's output records the global candidates */
/* 2nd Phase's output is the frequent itemset */

// MR2PhaseApriori.java.BASE strictly follows the paper
// this program did some optimization, code: OPT1
// optimization includes: 
// 1st, phase 1's mapper will write the local candidate file into HDFS
// 2nd, phase 2's mapper will use that file as cache
// optimization's goal:
// to reduce the "counting" in phase 2's mapper

public class MR2PhaseApriori {

	private static String inputpath;
	private static String outputpath1stphase;
	private static String outputpath;

	private static int items;
	// private static int minsupport;
	private static double minsupport;

	// OPT2
	private static double tolerate = 1;

	private static String ITEMS_CONFIG = "MR2PhaseApriori.items.value";
	private static String MINSUPPORT_CONFIG = "MR2PhaseApriori.minsupport.value";

	private static String FIRSTPHASEOUTDIR_CONFIG = "MR2PhaseApriori.1stphase.outdir";

	private static String OPT2TOLERATE_CONFIG = "MR2PhaseApriori.opt2tolerate.value";

	private static String SPLIT_NUM_ROWS = "split_num_rows.key";

	// OPT1's cache file suffix
	private static String cachecountfile = "-cachecount.file";

	private static enum TOTALROWCOUNTER {
		TOTALROW
	}

	private static String TOTAL_ROW_CONFIG = "MR2PhaseApriori.totalrow.value";
	private static long totalrows;

	/* for phase 1 */

	public static void run_on_hadoop_phase1() throws IllegalArgumentException,
			IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();

		conf.set(ITEMS_CONFIG, Integer.toString(items));
		// conf.set(MINSUPPORT_CONFIG, Integer.toString(minsupport));
		conf.set(MINSUPPORT_CONFIG, Double.toString(minsupport));
		conf.set(FIRSTPHASEOUTDIR_CONFIG, outputpath1stphase);
		conf.set(OPT2TOLERATE_CONFIG, Double.toString(tolerate));

		String jobname = "MapReduce 2Phase Apriori, Phase 1";
		Job job = new Job(conf, jobname);
		job.setJarByClass(MR2PhaseApriori.class);
		job.setInputFormatClass(WholeFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapperClass(FirstPhaseMapper.class);
		// job.setCombinerClass(FirstPhaseReducer.class);
		job.setReducerClass(FirstPhaseReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(inputpath));
		FileOutputFormat.setOutputPath(job, new Path(outputpath1stphase));

		int retval = job.waitForCompletion(true) ? 0 : 1;
		if (retval != 0) {
			System.exit(retval);
		}

		Counters counters = job.getCounters();
		Counter c = counters.findCounter(TOTALROWCOUNTER.TOTALROW);
		totalrows = c.getValue();

		System.err.println(Commons.PREFIX + "Total rows: " + totalrows);
	}

	public static class FirstPhaseMapper extends
			Mapper<NullWritable, BytesWritable, Text, IntWritable> {

		public static Log LOG = LogFactory.getLog(FirstPhaseMapper.class);

		private int transactions;
		private int items;
		// private int minsupport;
		private double minsupport;
		private String output1stphasedir;

		// OPT2
		private double tolerate;

		private long starttime;
		private long endtime;

		private BufferedWriter bw;

		@Override
		public void setup(Context context) throws IOException {
			items = context.getConfiguration().getInt(ITEMS_CONFIG, 0);
			minsupport = context.getConfiguration().getDouble(
					MINSUPPORT_CONFIG, 0);
			output1stphasedir = context.getConfiguration().get(
					FIRSTPHASEOUTDIR_CONFIG);

			// OPT2
			tolerate = context.getConfiguration().getDouble(
					OPT2TOLERATE_CONFIG, 1);

			starttime = System.currentTimeMillis();

			System.err.println(Commons.PREFIX + "minsupport = " + minsupport);

			// OPT2
			System.err.println(Commons.PREFIX + "tolerate = " + tolerate);

			System.err.println(Commons.PREFIX + "(1/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Map Task start time: " + (starttime));

			if (Commons.enabledOPT1()) {
				// prepare to write this counting statistics to hdfs
				// int mapid = context.getTaskAttemptID().getTaskID().getId();
				FileSystem fs = FileSystem.get(context.getConfiguration());
				// the path is output1stphasedir / _${splitname}-cachecount.file
				// this path's name should be changed.
				String splitname = ((FileSplit) context.getInputSplit())
						.getPath().getName();
				FSDataOutputStream out = fs.create(new Path(output1stphasedir
						+ "/_" + splitname + cachecountfile));
				bw = new BufferedWriter(new OutputStreamWriter(out));
			}

		}

		@Override
		public void map(NullWritable key, BytesWritable value, Context context)
				throws IOException, InterruptedException {

			// transform the bytes to strings
			String realstr = new String(value.getBytes());
			String[] rows = realstr.split("\\n");
			ArrayList<String> dataset = new ArrayList<String>();
			for (int i = 0; i < rows.length; i++) {
				String thisrow = rows[i].trim();
				if (thisrow.length() != 0) {
					dataset.add(thisrow);
				}
			}
			// get the number of rows from dataset itself
			transactions = dataset.size();

			// from now on, the input split is a ArrayList<String>
			// now call the local apriori algorithm

			LocalApriori localalg = null;

			if (Commons.enabledOPT2()) {
				localalg = new LocalApriori(transactions, items, minsupport,
						dataset, tolerate);
			} else {
				localalg = new LocalApriori(transactions, items, minsupport,
						dataset);
			}

			localalg.apriori();

			// get the frequent itemset, i.e. >= minsup
			ArrayList<ArrayList<String>> frequent = localalg.frequentItemset();
			ArrayList<ArrayList<Integer>> occurrences = localalg.frequencies();

			int loopc = 0, loopc2 = 0;
			for (Iterator<ArrayList<String>> it = frequent.iterator(); it
					.hasNext(); loopc++) {
				ArrayList<String> thisitemset = it.next();
				ArrayList<Integer> thisfreq = occurrences.get(loopc);
				loopc2 = 0;
				for (Iterator<String> it2 = thisitemset.iterator(); it2
						.hasNext(); loopc2++) {
					String itemset = it2.next();
					context.write(new Text(itemset),
							new IntWritable(thisfreq.get(loopc2)));

					// freqs will be passed to reduce phase no matter what,
					// but whether they need to be written to HDFS
					// depends on this switch
					if (Commons.enabledOPT1()) {
						// write to hdfs
						bw.write(itemset + " " + thisfreq.get(loopc2) + "\n");
					}
				}
			}

			// get the tolerating itemset, i.e. >= ratio * minsup
			// and < minsup, 0 <= ratio < 1
			if (Commons.enabledOPT2()) {
				loopc = 0;
				loopc2 = 0;
				ArrayList<ArrayList<String>> tol_itemset = localalg
						.tolerateItemset();
				ArrayList<ArrayList<Integer>> tol_occurs = localalg
						.tolerateFrequencies();

				// for bookkeeping
				long tolerateitemsets = 0;

				// if there's no tolerating candidate, then it's
				// the same with OPT2 disabled.
				for (Iterator<ArrayList<String>> it = tol_itemset.iterator(); it
						.hasNext(); loopc++) {
					ArrayList<String> thisitemset = it.next();
					ArrayList<Integer> thisfreq = tol_occurs.get(loopc);
					loopc2 = 0;
					for (Iterator<String> it2 = thisitemset.iterator(); it2
							.hasNext(); loopc2++) {
						String itemset = it2.next();
						// I don't need to check enabledOPT1 here anymore
						// these candidates don't need to be written to reduce
						// phase
						bw.write(itemset + " " + thisfreq.get(loopc2) + "\n");

						// for bookkeeping
						tolerateitemsets++;
					}
				}

				// this log is only available in enabledOPT2
				System.err.println(Commons.PREFIX + "(1/2) "
						+ context.getTaskAttemptID().getTaskID().getId()
						+ " Tolerate itemsets: " + tolerateitemsets);
			}

			context.write(new Text(SPLIT_NUM_ROWS), new IntWritable(
					transactions));

			System.err.println(Commons.PREFIX + "(1/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Total loops: " + localalg.getTotalLoops());
		}

		@Override
		public void cleanup(Context context) throws IOException {
			endtime = System.currentTimeMillis();
			System.err.println(Commons.PREFIX + "(1/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Map Task end time: " + (endtime));
			System.err.println(Commons.PREFIX + "(1/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Map Task execution time: " + (endtime - starttime));

			if (Commons.enabledOPT1()) {
				// close hdfs file handler
				bw.close();
			}
		}
	}

	public static class FirstPhaseReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable one = new IntWritable(1);

		private long reducestart, reduceend;

		private double minsupport;

		@Override
		public void setup(Context context) {
			reducestart = System.currentTimeMillis();
			System.err.println(Commons.PREFIX + "(1/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Reduce task start time: " + reducestart);

			minsupport = context.getConfiguration().getDouble(
					MINSUPPORT_CONFIG, 0.0);
		}

		@Override
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {

			if (key.toString().equalsIgnoreCase(SPLIT_NUM_ROWS)) {
				int totalrows = 0;
				for (IntWritable val : values) {
					totalrows += val.get();
				}
				context.getCounter(TOTALROWCOUNTER.TOTALROW).increment(
						totalrows);
			} else {
				// the following is for debugging purpose
				// int sum = 0;
				// for (IntWritable val : values) {
				// sum += val.get();
				// }
				// context.write(key, new IntWritable(sum));
				// the following is from paper's description
				context.write(key, one);
			}
		}

		@Override
		public void cleanup(Context context) {
			reduceend = System.currentTimeMillis();
			System.err.println(Commons.PREFIX + "(1/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Reduce task end time: " + reduceend);
			System.err.println(Commons.PREFIX + "(1/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Reduce execution time: " + (reduceend - reducestart));
		}
	}

	/* for phase 2 */

	public static void run_on_hadoop_phase2() throws IOException,
			ClassNotFoundException, InterruptedException, URISyntaxException {
		Configuration conf = new Configuration();

		conf.set(ITEMS_CONFIG, Integer.toString(items));
		conf.set(MINSUPPORT_CONFIG, Double.toString(minsupport));

		conf.set(TOTAL_ROW_CONFIG, Long.toString(totalrows));
		conf.set(FIRSTPHASEOUTDIR_CONFIG, outputpath1stphase);

		String jobname = "MapReduce 2Phase Apriori, Phase 2";
		Job job = new Job(conf, jobname);
		job.setJarByClass(MR2PhaseApriori.class);
		job.setInputFormatClass(WholeFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapperClass(SecondPhaseMapper.class);
		job.setReducerClass(SecondPhaseReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(inputpath));
		FileOutputFormat.setOutputPath(job, new Path(outputpath));

		FileSystem fs = FileSystem.get(conf);
		FileStatus[] fss = fs.listStatus(new Path(outputpath1stphase));
		int filenum = 0;
		for (FileStatus status : fss) {
			Path path = status.getPath();
			if (path.getName().startsWith("_"))
				continue;
			filenum++;
			job.addCacheFile(new URI(path.toString()));
		}

		System.err.println(Commons.PREFIX + "Just added " + filenum
				+ " files into distributed cache.");
		int retval = job.waitForCompletion(true) ? 0 : 1;

		// if (retval != 0) {
		// System.exit(retval);
		// }
	}

	public static class SecondPhaseMapper extends
			Mapper<NullWritable, BytesWritable, Text, IntWritable> {

		private int items;

		private String[] localFileNames;
		private File[] localFiles;

		private long mapstart, mapend;

		private String output1stphasedir;

		private BufferedReader br;

		private HashMap<String, Integer> cache = new HashMap<String, Integer>();
		private long total, hit;

		@Override
		public void setup(Context context) throws IOException {
			items = context.getConfiguration().getInt(ITEMS_CONFIG, 0);
			// minsupport = context.getConfiguration()
			// .getInt(MINSUPPORT_CONFIG, 0);

			output1stphasedir = context.getConfiguration().get(
					FIRSTPHASEOUTDIR_CONFIG);

			Path[] cacheFiles = context.getLocalCacheFiles();
			if (cacheFiles != null) {
				localFileNames = new String[cacheFiles.length];
				localFiles = new File[cacheFiles.length];
				for (int i = 0; i < cacheFiles.length; i++) {
					localFileNames[i] = cacheFiles[i].toString();
					localFiles[i] = new File(localFileNames[i]);
				}
			}

			mapstart = System.currentTimeMillis();
			System.err.println(Commons.PREFIX + "(2/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Map task start time: " + mapstart);

			if (Commons.enabledOPT1()) {
				// load the hdfs file into cache
				// if this is skipped, then cache is empty
				// OPT2: this mapper doesn't know whether this
				// cache contains tolerating candidates or not
				// so OPT2 is transparent to Phase 2
				FileSystem fs = FileSystem.get(context.getConfiguration());
				String splitname = ((FileSplit) context.getInputSplit())
						.getPath().getName();
				FSDataInputStream out = fs.open(new Path(output1stphasedir
						+ "/_" + splitname + cachecountfile));
				br = new BufferedReader(new InputStreamReader(out));

				String line = null;
				while ((line = br.readLine()) != null) {
					String[] two = line.split(" ");
					cache.put(two[0], Integer.parseInt(two[1]));
				}
				br.close();
			}
		}

		@Override
		public void cleanup(Context context) throws IOException {
			mapend = System.currentTimeMillis();
			System.err.println(Commons.PREFIX + "(2/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Map task end time: " + mapend);
			System.err.println(Commons.PREFIX + "(2/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Map task execution time: " + (mapend - mapstart));

			System.err.println(Commons.PREFIX + "(2/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Cache hit: " + hit + " / " + total + " = "
					+ (total == 0 ? "N/A" : (hit / (double) total)));

			// br.close();
		}

		@Override
		public void map(NullWritable key, BytesWritable value, Context context)
				throws IOException, InterruptedException {

			// transform the bytes to strings
			String realstr = new String(value.getBytes());
			String[] rows = realstr.split("\\n");
			ArrayList<String> dataset = new ArrayList<String>();
			for (int i = 0; i < rows.length; i++) {
				String thisrow = rows[i].trim();
				if (thisrow.length() != 0) {
					dataset.add(thisrow);
				}
			}

			// OPT1: here I should open the countcache file
			// OPT1: load it into cache map
			// OPT1: close the count cache file

			if (localFiles != null) {
				for (int i = 0; i < localFiles.length; i++) {
					localCount(localFiles[i], dataset, context);
				}
			}
		}

		private void localCount(File file, ArrayList<String> dataset,
				Context context) throws IOException, InterruptedException {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String itemset = line.split("\\t")[0];
				// OPT1: ask the cache map whether it has my count
				total++;
				if (cache.containsKey(itemset)) {
					hit++;
					int num = cache.get(itemset);
					context.write(new Text(itemset), new IntWritable(num));
					continue;
				}

				int count = 0;
				// the following op is expensive, because
				// it iterates the whole dataset for the count
				for (Iterator<String> it = dataset.iterator(); it.hasNext();) {
					String transaction = it.next();
					if (included(itemset, transaction)) {
						count++;
					}
				}
				context.write(new Text(itemset), new IntWritable(count));
			}
			br.close();
		}

		private boolean included(String itemset, String transaction) {
			boolean[] trans = new boolean[items];
			StringTokenizer st = new StringTokenizer(transaction,
					Commons.DATASETSEPARATOR);
			for (int i = 0; i < items; i++) {
				trans[i] = st.nextToken().equalsIgnoreCase(Commons.PURCHASED);
			}
			StringTokenizer st2 = new StringTokenizer(itemset,
					Commons.SEPARATOR);
			boolean match = false;
			while (st2.hasMoreTokens()) {
				match = trans[Integer.parseInt(st2.nextToken()) - 1];
				if (!match) {
					return false;
				}
			}
			return true;
		}
	}

	public static class SecondPhaseReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {

		private long totalrows;
		private double minsupport;

		private long reducestart, reduceend;

		@Override
		public void setup(Context context) throws IOException {
			minsupport = context.getConfiguration().getDouble(
					MINSUPPORT_CONFIG, 0);
			totalrows = context.getConfiguration().getLong(TOTAL_ROW_CONFIG, 0);

			System.err.println(Commons.PREFIX + "minsupport = " + minsupport);

			reducestart = System.currentTimeMillis();
			System.err.println(Commons.PREFIX + "(2/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Reduce task start time: " + reducestart);
		}

		@Override
		public void cleanup(Context context) {
			reduceend = System.currentTimeMillis();
			System.err.println(Commons.PREFIX + "(2/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Reduce task end time: " + reduceend);
			System.err.println(Commons.PREFIX + "(2/2) "
					+ context.getTaskAttemptID().getTaskID().getId()
					+ " Reduce task execution time: "
					+ (reduceend - reducestart));
		}

		@Override
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			if ((sum * 100) >= (minsupport * totalrows)) {
				context.write(key, new IntWritable(sum));
			}
		}
	}

	/* main function */

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException, URISyntaxException {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length < 4) {
			// first kind of input: 4 params
			System.err.print(Commons.PREFIX
					+ "Usage: MR2PhaseApriori <in> <out>");
			System.err.print(" <columns/items>");
			System.err.print(" <minimum support>");
			// second kind of input: 5 params
			System.err.println(" [<tolerate ratio>]");
			// be careful when params need to be >= 6
			System.exit(2);
		}

		inputpath = otherArgs[0];
		outputpath = otherArgs[1];
		items = Integer.parseInt(otherArgs[2]);
		minsupport = Double.parseDouble(otherArgs[3]);

		if (otherArgs.length == 5) {
			tolerate = Double.parseDouble(otherArgs[4]);
			System.err.println(Commons.PREFIX + "tolerate set by user");
		}

		System.err.println(Commons.PREFIX + "items = " + items);
		System.err.println(Commons.PREFIX + "minsupport = " + minsupport);
		System.err.println(Commons.PREFIX + "enabledOPT1 = "
				+ Commons.enabledOPT1());
		System.err.println(Commons.PREFIX + "enabledOPT2 = "
				+ Commons.enabledOPT2());
		System.err.println(Commons.PREFIX + "tolerate = " + tolerate);

		outputpath1stphase = outputpath + "-1stphase";

		long starttime = System.currentTimeMillis();

		long phase1starttime = System.currentTimeMillis();

		run_on_hadoop_phase1();

		// System.exit(1);

		long phase1endtime = System.currentTimeMillis();

		long phase2starttime = System.currentTimeMillis();

		run_on_hadoop_phase2();

		long phase2endtime = System.currentTimeMillis();

		long endtime = System.currentTimeMillis();

		System.err.println(Commons.PREFIX + "Total execution time: "
				+ (endtime - starttime));
		System.err.println(Commons.PREFIX + "Phase 1 execution time: "
				+ (phase1endtime - phase1starttime));
		System.err.println(Commons.PREFIX + "Phase 2 execution time: "
				+ (phase2endtime - phase2starttime));
	}
}
