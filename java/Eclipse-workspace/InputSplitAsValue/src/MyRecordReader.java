import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;

import sun.misc.IOUtils;

public class MyRecordReader extends RecordReader<NullWritable, BytesWritable> {
	public static final String MAX_LINE_LENGTH = "mapreduce.input.linerecordreader.line.maxlength";

	private LongWritable key;
	private Text value;
	private long start;
	private long pos;
	private long end;
	private LineReader in;
	private FSDataInputStream fileIn;
	private byte[] buffer;

	private int maxLineLength;

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		if (in != null) {
			in.close();
		}
	}

	@Override
	public NullWritable getCurrentKey() throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		// return key;
		return NullWritable.get();
	}

	@Override
	public BytesWritable getCurrentValue() throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		// return value;
		// return new Text("fake");
		return new BytesWritable(new byte[] { '\5', '\6' });
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		FileSplit split = (FileSplit) genericSplit;
		Configuration job = context.getConfiguration();
		this.maxLineLength = job.getInt(MAX_LINE_LENGTH, Integer.MAX_VALUE);
		start = split.getStart();
		end = start + split.getLength();
		final Path file = split.getPath();

		final FileSystem fs = file.getFileSystem(job);
		fileIn = fs.open(file);
		// fileIn.seek(start);
		// in = new LineReader(fileIn, job);
		// this.pos = start;
		buffer = new byte[(int) split.getLength()];
		// IOUtils.readFully(fileIn, buffer, 0, (int) split.getLength());
	}

	private int maxBytesToConsume(long pos) {
		return (int) Math.min(Integer.MAX_VALUE, end - pos);
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		if (key == null) {
			key = new LongWritable();
		}
		key.set(pos);
		if (value == null) {
			value = new Text();
		}
		int newSize = 0;
		while (pos <= end) {
			newSize = in.readLine(value, maxLineLength,
					Math.max(maxBytesToConsume(pos), maxLineLength));
			pos += newSize;
			if (newSize < maxLineLength) {
				break;
			}
		}
		if (newSize == 0) {
			key = null;
			value = null;
			return false;
		} else {
			return true;
		}
	}
}
