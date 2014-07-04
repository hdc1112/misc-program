import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
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
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class MR2PhaseFPGrowth {

	// hadoop params
	private static final String PHASE1OUTDIR_CONFIG = Commons.PROGNAME
			+ ".phase1.outdir";
	private static final String PHASE1MINSUP_CONFIG = Commons.PROGNAME
			+ ".phase1.minsup";
	private static long totalrows;

	private static enum RowCounter {
		TOTALROW
	}

	public static void run_on_hadoop_phase1() throws IOException,
			ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		conf.set(PHASE1OUTDIR_CONFIG, inputpath);
		conf.set(PHASE1MINSUP_CONFIG, Double.toString(phase1minsup));

		String jobname = Commons.PROGNAME + " Phase 1";
		Job job = new Job(conf, jobname);
		job.setJarByClass(MR2PhaseFPGrowth.class);
		job.setInputFormatClass(WholeFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapperClass(Phase1Mapper.class);
		job.setReducerClass(Phase1Reducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(inputpath));
		FileOutputFormat.setOutputPath(job, new Path(outputpath1stphase));

		int retval = job.waitForCompletion(true) ? 0 : 1;
		if (retval != 0) {
			System.err.println(Commons.PREFIX + "Phase 1 Error, exit");
			System.exit(retval);
		}

		Counters counters = job.getCounters();
		Counter counter = counters.findCounter(RowCounter.TOTALROW);
		totalrows = counter.getValue();

		System.err.println(Commons.PREFIX + "Total rows: " + totalrows);
	}

	private static final String SPLIT_NUM_ROWS = "split_num_rows.key";

	public static class Phase1Mapper extends
			Mapper<NullWritable, BytesWritable, Text, IntWritable> {
		@Override
		public void map(NullWritable key, BytesWritable value, Context context)
				throws IOException, InterruptedException {
			// byteswritable -> ArrayList<String>
			String realstr = new String(value.getBytes());
			String[] rows = realstr.split("\\n");
			ArrayList<String> dataset = new ArrayList<String>();
			String thisrow = null;
			for (int i = 0; i < rows.length; i++) {
				thisrow = rows[i].trim();
				if (thisrow.length() != 0) {
					dataset.add(thisrow);
				}
			}
			int numrows = dataset.size();

			// use fp-growth alg

			// output intermediate data

			context.write(new Text(SPLIT_NUM_ROWS), new IntWritable(numrows));
		}

		// download param
		private double minsup;
		private String output1stphasedir;

		// bookkeeping
		private long starttime, endtime;
		private int taskid;

		@Override
		public void setup(Context context) {
			// my id
			taskid = context.getTaskAttemptID().getTaskID().getId();

			// download
			minsup = context.getConfiguration().getDouble(PHASE1MINSUP_CONFIG,
					100);
			output1stphasedir = context.getConfiguration().get(
					PHASE1OUTDIR_CONFIG);

			// show params
			System.err.println(Commons.PREFIX + "minsupport: " + minsup);
			System.err.println(Commons.PREFIX + "output1stphasedir: "
					+ output1stphasedir);

			// bookkeeping
			starttime = System.currentTimeMillis();
			System.err.println(Commons.PREFIX + "(1/2) " + taskid
					+ " Map Task start time: " + starttime);
		}

		@Override
		public void cleanup(Context context) {
			// bookkeeping
			endtime = System.currentTimeMillis();
			System.err.println(Commons.PREFIX + "(1/2) " + taskid
					+ " Map Task end time: " + endtime);
			System.err.println(Commons.PREFIX + "(1/2) " + taskid
					+ " Map Task execution time: " + (endtime - starttime));
		}
	}

	public static class Phase1Reducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		@Override
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			if (key.toString().equals(SPLIT_NUM_ROWS)) {
				// compute input total rows
				int totalrows = 0;
				for (IntWritable val : values) {
					totalrows += val.get();
				}
				context.getCounter(RowCounter.TOTALROW).increment(totalrows);
			} else {
				context.write(key, one);
			}
		}

		// download params
		double minsup;

		// bookkeeping
		private long reducestart, reduceend;
		private int taskid;

		// save time
		private IntWritable one = new IntWritable(1);

		@Override
		public void setup(Context context) {
			// download params
			minsup = context.getConfiguration().getDouble(PHASE1MINSUP_CONFIG,
					100);

			// my id
			taskid = context.getTaskAttemptID().getTaskID().getId();

			// bookkeeping
			reducestart = System.currentTimeMillis();
			System.err.println(Commons.PREFIX + "(1/2) " + taskid
					+ " Reduce Task start time: " + reducestart);
		}

		@Override
		public void cleanup(Context context) {
			// bookkeeping
			reduceend = System.currentTimeMillis();
			System.err.println(Commons.PREFIX + "(1/2) " + taskid
					+ "Reduce Task end time: " + reduceend);
			System.err.println(Commons.PREFIX + "(1/2) " + taskid
					+ "Reduce Task execution time: "
					+ (reduceend - reducestart));
		}
	}

	public static void run_on_hadoop_phase2() {

	}

	// cmd line params
	private static String inputpath;
	private static String outputpath;
	private static double minsup;
	private static double phase1minsup;
	private static String outputpath1stphase;

	public static void main(String[] args) throws IOException, ParseException,
			ClassNotFoundException, InterruptedException {
		// hadoop cmd parse
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();

		// my cmd parse
		// definition stage
		Options options = buildOptions();

		// parsing stage
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, otherArgs);

		// interrogation stage
		inputpath = cmd.getOptionValue("inpath");
		outputpath = cmd.getOptionValue("outpath");
		minsup = Double.parseDouble(cmd.getOptionValue("minsupport"));
		if (cmd.hasOption("phase1minsup")) {
			phase1minsup = Double.parseDouble(cmd
					.getOptionValue("phase1minsup"));
		} else {
			phase1minsup = minsup;
		}
		double beta = 1;
		if (cmd.hasOption("phase1minsupbeta")) {
			beta = Double.parseDouble(cmd.getOptionValue("phase1minsupbeta"));
			phase1minsup = beta * minsup;
		}

		// verify stage
		// skip minsup's verification
		if (phase1minsup > 100 || phase1minsup < 0) {
			System.err.println(Commons.PREFIX + "phase1minsup out of range");
			System.err.println(Commons.PREFIX + "phase1minsup set to minsup");
			phase1minsup = minsup;
		}

		// show stage
		System.err.println(Commons.PREFIX + "inpath: " + inputpath);
		System.err.println(Commons.PREFIX + "outpath: " + outputpath);
		System.err.println(Commons.PREFIX + "minsupport: " + minsup);
		System.err.println(Commons.PREFIX + "phase1minsup: " + phase1minsup);

		// main logic
		outputpath1stphase = outputpath + "-1stphase";

		long starttime = System.currentTimeMillis();

		long phase1starttime = System.currentTimeMillis();

		run_on_hadoop_phase1();

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

	@SuppressWarnings("static-access")
	private static Options buildOptions() {
		Options options = new Options();

		options.addOption(OptionBuilder.withArgName("inpath").hasArg()
				.isRequired().withDescription("hdfs input folder")
				.create("inpath"));

		options.addOption(OptionBuilder.withArgName("outpath").hasArg()
				.isRequired().withDescription("hdfs ouput folder")
				.create("outpath"));

		options.addOption(OptionBuilder.withArgName("minsupport").hasArg()
				.isRequired().withDescription("minimum support in percentage")
				.create("minsupport"));

		options.addOption(OptionBuilder.withArgName("phase1minsup").hasArg()
				.withDescription("phase 1 minsupport in percentage")
				.create("phase1minsup"));

		options.addOption(OptionBuilder.withArgName("phase1minsupbeta")
				.hasArg().withDescription("phase 1 minsup ratio")
				.create("phase2minsupbeta"));

		return options;
	}
}
