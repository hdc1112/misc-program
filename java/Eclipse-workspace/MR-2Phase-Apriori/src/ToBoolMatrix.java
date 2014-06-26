import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class ToBoolMatrix {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: java ToBoolMatrix /path/to/file");
			System.exit(1);
		}
		String filepath = args[0];

		System.err.println("ToBoolMatrix");
		System.err.println(filepath);

		BufferedReader br = new BufferedReader(new FileReader(
				new File(filepath)));
		String line;
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE; // assume the min >= 0
		int rows = 0;
		while ((line = br.readLine()) != null) {
			rows++;
			StringTokenizer st = new StringTokenizer(line);

			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				int tokenv = Integer.parseInt(token);
				if (tokenv > max) {
					max = tokenv;
				}
				if (tokenv < min) {
					min = tokenv;
				}
			}
		}

		System.err.println("max = " + max);
		System.err.println("min = " + min);
		System.err.println("rows = " + rows);

		br.close();

		int loopc = 0;

		boolean[] linebool = new boolean[max + 1];

		br = new BufferedReader(new FileReader(new File(filepath)));
		while ((line = br.readLine()) != null) {
			loopc++;

			for (int i = 0; i < linebool.length; i++) {
				linebool[i] = false;
			}

			StringTokenizer st = new StringTokenizer(line);
			while (st.hasMoreTokens()) {
				int tokenv = Integer.parseInt(st.nextToken());
				linebool[tokenv] = true;
			}
			// System.err.println(loopc);
			for (int i = min; i <= max; i++) {
				if (linebool[i] == false) {
					System.out.print("0 ");
				} else {
					System.out.print("1 ");
				}
			}
			System.out.println();
		}

		br.close();
	}
}
