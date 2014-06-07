import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class ControlkRows {
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java ControlkRows outputfile k");
			System.exit(1);
		}

		int k = Integer.parseInt(args[1]);

		// suppose #rows = 1000, all 1 = 500, one column 1 = 500
		// suppose #columns = 30
		// suppose #splits = 2

		final int columns = 30;
		final int splitrows = 500;
		final int totalrows = 1000;

		String[] strs = new String[1000];

		// fill all 1
		for (int i = 0; i < k; i++) {
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < columns; j++) {
				sb.append("1 ");
			}
			strs[i] = sb.toString();
		}
		for (int i = 0; i < (splitrows - k); i++) {
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < columns; j++) {
				sb.append("1 ");
			}
			strs[500 + i] = sb.toString();
		}

		// fill one column 1
		for (int i = 0; i < (splitrows - k); i++) {
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < columns; j++) {
				if (j == 0) {
					sb.append("1 ");
				} else {
					sb.append("0 ");
				}
			}
			strs[i + k] = sb.toString();
		}

		for (int i = 0; i < k; i++) {
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < columns; j++) {
				if (j == 0) {
					sb.append("1 ");
				} else {
					sb.append("0 ");
				}
			}
			strs[splitrows + (splitrows - k) + i] = sb.toString();
		}

		String filename = args[0];
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));

		for (int i = 0; i < totalrows; i++) {
			bw.write(strs[i]);
			bw.write("\n");
		}

		bw.close();
	}
}
