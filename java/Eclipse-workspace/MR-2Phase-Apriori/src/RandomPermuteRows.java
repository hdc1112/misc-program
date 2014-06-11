import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RandomPermuteRows {
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java RandomPermuteRows outputfile");
			System.exit(1);
		}

		String filename = args[0];
		int columns = Integer.parseInt(args[1]);

		// suppose #rows = 1000, all 1 = 500, one column 1 = 500
		// suppose #columns = 30
		// suppose #splits = 2

		// final int columns = 30;
		final int splitrows = 500;
		final int totalrows = 1000;

		int[] oneorzero = new int[totalrows];

		for (int i = 0; i < splitrows; i++) {
			oneorzero[i] = 1;
		}

		FisherYatesShuffle.shuffleArray(oneorzero);

		String[] strs = new String[totalrows];

		for (int i = 0; i < totalrows; i++) {
			StringBuffer sb = new StringBuffer();
			if (oneorzero[i] == 1) {
				for (int j = 0; j < columns; j++) {
					sb.append("1 ");
				}
			} else {
				sb.append("1 ");
				for (int j = 1; j < columns; j++) {
					sb.append("0 ");
				}
			}
			strs[i] = sb.toString();
		}

		BufferedWriter bw = new BufferedWriter(new FileWriter(
				new File(filename)));

		for (int i = 0; i < totalrows; i++) {
			bw.write(strs[i]);
			bw.write("\n");
		}

		bw.close();

		int k = 0;
		for (int i = 0; i < splitrows; i++) {
			if (strs[i].startsWith("1 1") == true) {
				k++;
			}
		}
		System.out.println("Complete 1 rows in the first split: " + k);
	}
}
