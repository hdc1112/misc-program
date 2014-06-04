import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/* This program implements the algorithm from the link below */
/* http://www.ijric.org/volumes/Vo12/Vol12No7.pdf */

public class MR2PhaseApriori {

	private static String inputpath;
	private static String outputpath;

	private static int transactions;
	private static int items;
	private static int minsupport;

	private static String TRANSACTION_CONFIG = "MR2PhaseApriori.transactions.value";
	private static String ITEMS_CONFIG = "MR2PhaseApriori.items.value";
	private static String MINSUPPORT_CONFIG = "MR2PhaseApriori.minsupport.value";

	public static void run_on_hadoop_phase1() throws IllegalArgumentException,
			IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();

		conf.set(TRANSACTION_CONFIG, Integer.toString(transactions));
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
		FileOutputFormat.setOutputPath(job, new Path(outputpath));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	public static class FirstPhaseMapper extends
			Mapper<NullWritable, BytesWritable, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);

		private int transactions;
		private int items;
		private int minsupport;

		@Override
		public void setup(Context context) {
			transactions = context.getConfiguration().getInt(
					TRANSACTION_CONFIG, 0);
			items = context.getConfiguration().getInt(ITEMS_CONFIG, 0);
			minsupport = context.getConfiguration()
					.getInt(MINSUPPORT_CONFIG, 0);

		}

		@Override
		public void map(NullWritable key, BytesWritable value, Context context)
				throws IOException, InterruptedException {

			// transform the bytes to strings
			String realstr = new String(value.getBytes());
			String[] rows = realstr.split("\n");
			ArrayList<String> dataset = new ArrayList<String>();
			for (int i = 0; i < rows.length; i++) {
				String thisrow = rows[i].trim();
				if (thisrow.length() != 0) {
					dataset.add(thisrow);
				}
			}

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
		}
	}

	public static class FirstPhaseReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();
		private IntWritable one = new IntWritable(1);

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			// the following is for debugging purpose
			// int sum = 0;
			// for (IntWritable val : values) {
			// sum += val.get();
			// }
			// result.set(sum);
			// context.write(key, result);
			// the following is from paper's description
			context.write(key, one);
		}
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 5) {
			System.err.print("Usage: MR2PhaseApriori <in> <out>");
			System.err.print(" <rows/transactions> <columns/items>");
			System.err.println(" <minimum support>");
			System.exit(2);
		}

		inputpath = otherArgs[0];
		outputpath = otherArgs[1];

		// at this stage, let's assume that each
		// split has the same amount of transactions,
		// the same number of items
		transactions = Integer.parseInt(otherArgs[2]);
		items = Integer.parseInt(otherArgs[3]);

		minsupport = Integer.parseInt(otherArgs[4]);

		run_on_hadoop_phase1();
	}
}
