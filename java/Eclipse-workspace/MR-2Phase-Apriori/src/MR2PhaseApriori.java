import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

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
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/* This program implements the algorithm from the link below */
/* http://www.ijric.org/volumes/Vo12/Vol12No7.pdf */

public class MR2PhaseApriori {

	/* for phase 1 */

	private static String inputpath;
	private static String outputpath1stphase;
	private static String outputpath;

	private static int items;
	private static int minsupport;

	private static String ITEMS_CONFIG = "MR2PhaseApriori.items.value";
	private static String MINSUPPORT_CONFIG = "MR2PhaseApriori.minsupport.value";

	private static String SPLIT_NUM_ROWS = "split_num_rows";
	private static String TOTAL_NUM_ROWS = "_totalrows.txt";

	public static void run_on_hadoop_phase1() throws IllegalArgumentException,
			IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();

		conf.set(ITEMS_CONFIG, Integer.toString(items));
		conf.set(MINSUPPORT_CONFIG, Integer.toString(minsupport));

		String jobname = "MapReduce 2Phase Apriori, Phase 1";
		Job job = new Job(conf, jobname);
		job.setJarByClass(MR2PhaseApriori.class);
		job.setInputFormatClass(WholeFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapperClass(FirstPhaseMapper.class);
		job.setCombinerClass(FirstPhaseReducer.class);
		job.setReducerClass(FirstPhaseReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(inputpath));
		FileOutputFormat.setOutputPath(job, new Path(outputpath1stphase));

		int retval = job.waitForCompletion(true) ? 0 : 1;
		if (retval == 1) {
			System.exit(1);
		}
	}

	public static class FirstPhaseMapper extends
			Mapper<NullWritable, BytesWritable, Text, IntWritable> {

		private int transactions;
		private int items;
		private int minsupport;

		@Override
		public void setup(Context context) {
			items = context.getConfiguration().getInt(ITEMS_CONFIG, 0);
			minsupport = context.getConfiguration()
					.getInt(MINSUPPORT_CONFIG, 0);

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

			LocalApriori localalg = new LocalApriori(transactions, items,
					minsupport, dataset);
			localalg.apriori();
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
					context.write(new Text(it2.next()), new IntWritable(
							thisfreq.get(loopc2)));
				}
			}

			// write this InputSplit's rows, such that the whole
			// number of transactions can be computed.
			// this is based on the assumption that this map()
			// will only be called once.
			// context.write(new Text(SPLIT_NUM_ROWS), new IntWritable(
			// transactions));
		}
	}

	public static class FirstPhaseReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		// private IntWritable result = new IntWritable();
		private IntWritable one = new IntWritable(1);

		@Override
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {

			if (key.toString().equalsIgnoreCase(SPLIT_NUM_ROWS)) {
				int totalrows = 0;
				for (IntWritable val : values) {
					totalrows += val.get();
				}

				// FileSystem fs = FileSystem.get(context.getConfiguration());
				// Path filepath = new Path("./" + TOTAL_NUM_ROWS);
				// fs.delete(filepath, true);
				// FSDataOutputStream out = fs.create(new Path("./"
				// + TOTAL_NUM_ROWS), true);
				// out.writeUTF(Integer.toString(totalrows));
				// out.close();
			} else {
				// the following is for debugging purpose
				int sum = 0;
				for (IntWritable val : values) {
					sum += val.get();
				}
				context.write(key, new IntWritable(sum));
				// the following is from paper's description
				// context.write(key, one);
			}
		}
	}

	/* for phase 2 */

	public static void run_on_hadoop_phase2() throws IOException,
			ClassNotFoundException, InterruptedException, URISyntaxException {
		Configuration conf = new Configuration();

		conf.set(ITEMS_CONFIG, Integer.toString(items));
		conf.set(MINSUPPORT_CONFIG, Integer.toString(minsupport));

		String jobname = "MapReduce 2Phase Apriori, Phase 2";
		Job job = new Job(conf, jobname);
		job.setJarByClass(MR2PhaseApriori.class);
		job.setInputFormatClass(WholeFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapperClass(SecondPhaseMapper.class);
		job.setCombinerClass(SecondPhaseReducer.class);
		job.setReducerClass(SecondPhaseReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(inputpath));
		FileOutputFormat.setOutputPath(job, new Path(outputpath));

		FileSystem fs = FileSystem.get(conf);
		FileStatus[] fss = fs.listStatus(new Path(outputpath1stphase));
		for (FileStatus status : fss) {
			Path path = status.getPath();
			if (path.getName().startsWith("_"))
				continue;
			job.addCacheFile(new URI(path.toString()));
			// DistributedCache.addCacheFile(new URI(path.toString()), conf);
		}

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	public static class SecondPhaseMapper extends
			Mapper<NullWritable, BytesWritable, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);

		private int transactions;
		private int items;
		private int minsupport;

		private String[] localFileNames;
		private File[] localFiles;

		@Override
		public void setup(Context context) throws IOException {
			items = context.getConfiguration().getInt(ITEMS_CONFIG, 0);
			minsupport = context.getConfiguration()
					.getInt(MINSUPPORT_CONFIG, 0);

			Path[] cacheFiles = context.getLocalCacheFiles();
			if (cacheFiles != null) {
				localFileNames = new String[cacheFiles.length];
				localFiles = new File[cacheFiles.length];
				for (int i = 0; i < cacheFiles.length; i++) {
					localFileNames[i] = cacheFiles[i].toString();
					localFiles[i] = new File(localFileNames[i]);
				}
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
				int count = 0;
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
					LocalApriori.DATASETSEPARATOR);
			for (int i = 0; i < items; i++) {
				trans[i] = st.nextToken().equalsIgnoreCase(
						LocalApriori.PURCHASED);
			}
			StringTokenizer st2 = new StringTokenizer(itemset,
					LocalApriori.SEPARATOR);
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

		private String totalrows;

		private int minsupport;

		@Override
		public void setup(Context context) throws IOException {
			// FileSystem fs = FileSystem.get(context.getConfiguration());
			// Path filepath = new Path("./" + TOTAL_NUM_ROWS);
			// FSDataInputStream in = fs.open(filepath);
			// totalrows = in.readUTF();

			minsupport = context.getConfiguration()
					.getInt(MINSUPPORT_CONFIG, 0);

		}

		@Override
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			// context.write(new Text(Integer.toString(sum)), new
			// IntWritable(sum));
			context.write(
					new Text("zzz--" + key.toString() + "---"
							+ Integer.toString(sum)), new IntWritable(555));
			// if ((sum * 1.0 / 8.0) >= (minsupport * 1.0 / 100.0)) {
			// context.write(key, new IntWritable(sum));
			// context.write(key, new IntWritable(1000));
			// context.write(key, new IntWritable(loopc));
			// } else {
			// context.write(key, new IntWritable(1000));
			// }
			// context.write(new Text("zzz--" + key.toString() + "---" +
			// Integer.toString(sum)),
			// new IntWritable(999));
			// context.write(new Text("hhh--" + Integer.toString(minsupport)),
			// new IntWritable(100));
		}
	}

	/* main function */

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException, URISyntaxException {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 4) {
			System.err.print("Usage: MR2PhaseApriori <in> <out>");
			System.err.print(" <columns/items>");
			System.err.println(" <minimum support>");
			System.exit(2);
		}

		inputpath = otherArgs[0];
		outputpath = otherArgs[1];
		outputpath1stphase = outputpath + "-1stphase";

		items = Integer.parseInt(otherArgs[2]);
		minsupport = Integer.parseInt(otherArgs[3]);

		run_on_hadoop_phase1();

		run_on_hadoop_phase2();
	}
}
