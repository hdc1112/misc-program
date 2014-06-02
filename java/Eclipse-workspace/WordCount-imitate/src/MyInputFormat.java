import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;

import com.google.common.base.Charsets;

public class MyInputFormat extends FileInputFormat<LongWritable, Text> {
	@Override
	protected boolean isSplitable(JobContext context, Path filename) {
		return false;
	}

	@Override
	public RecordReader<LongWritable, Text> createRecordReader(InputSplit split,
			TaskAttemptContext context) throws IOException, InterruptedException {
//		String delimiter = context.getConfiguration().get("textinputformat.record.delimiter");
//		byte[] recordDelimiterBytes = null;
//		if (null != delimiter) {
//			recordDelimiterBytes = delimiter.getBytes(Charsets.UTF_8);
//		}
//		return new LineRecordReader(recordDelimiterBytes);
		return new MyRecordReader();
	}


}
