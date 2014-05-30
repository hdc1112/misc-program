import java.io.IOException;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;


public class MyInputFormat extends FileInputFormat<NullWritable, BytesWritable> {

	@Override
	public RecordReader<NullWritable, BytesWritable> getRecordReader(
			InputSplit split, JobConf conf, Reporter arg2) throws IOException {
		// TODO Auto-generated method stub
		WholeFileRecordReader rr = new WholeFileRecordReader();
		rr.initialize(split, conf);
		return 
	}

	@Override
	public RecordReader<NullWritable, BytesWritable> getRecordReader(
			org.apache.hadoop.mapred.InputSplit arg0, JobConf arg1,
			Reporter arg2) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
