import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

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
			// for (Iterator<String> it = array.iterator(); it.hasNext();) {
			// context.write(new Text(it.next()), one);
			// }
			// now call the local apriori algorithm

			LocalApriori localalg = new LocalApriori(transactions, items,
					minsupport, dataset);
			localalg.apriori();
			ArrayList<String> frequent = localalg.frequentItemset();
			for (Iterator<String> it = frequent.iterator(); it.hasNext();) {
				// at this stage, we don't care about the
				// local occurrences of that itemset, so just
				// emit one for every itemset
				context.write(new Text(it.next()), one);
			}
		}
	}

	public static class FirstPhaseReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
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
		transactions = Integer.parseInt(otherArgs[2]);
		items = Integer.parseInt(otherArgs[3]);
		minsupport = Integer.parseInt(otherArgs[4]);

		run_on_hadoop_phase1();
	}
}
