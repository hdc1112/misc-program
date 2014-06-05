import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/* This generator accepts matrix's row, column */
/* as parameter, and always output 1 in the first column, the rest is 0 */
/* the output is written to system temp dir */

public class DatasetGenerator3 {
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: <rows> <columns>");
			System.exit(1);
		}
		int rows = Integer.parseInt(args[0]);
		int columns = Integer.parseInt(args[1]);
		long timestamp = System.currentTimeMillis();

		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		String filename = "inputfile-" + Long.toString(timestamp);

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(baseDir,
				filename)));

		Random random = new Random();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				double d = random.nextDouble();
				if (j == 0) {
					bw.write("1 ");
				} else {
					bw.write("0 ");
				}
			}
			bw.write("\n");
		}

		bw.close();
	}
}
