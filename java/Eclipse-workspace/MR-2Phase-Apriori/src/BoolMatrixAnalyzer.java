import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

public class BoolMatrixAnalyzer {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: java BoolMatrixAnalyzer /path/to/file");
			System.exit(1);
		}
		String filepath = args[0];

		System.err.println("BoolMatrixAnalyzer");
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

		int loopc = -1;

		boolean[] linebool = new boolean[max + 1];

		br = new BufferedReader(new FileReader(new File(filepath)));

		int nozero = 0;
		int noone = 0;

		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
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
					nozero++;
					// System.out.print("0 ");
				} else {
					noone++;
					// System.out.print("1 ");
					if (map.containsKey(i)) {
						map.put(i, map.get(i) + 1);
					} else {
						map.put(i, 1);
					}
				}
			}
			System.out.println();
		}

		System.err.println("number of zero = " + nozero);
		System.err.println("number of one = " + noone);
		System.err.println("number of cell = " + (nozero + noone));
		System.err.println("zero percentage = "
				+ (nozero * (1.0) / (nozero + noone)));
		System.err.println("one percentage = "
				+ (noone * (1.0) / (nozero + noone)));

		int nonempty = 0;
		for (int i = min; i <= max; i++) {
			if (map.containsKey(i)) {
				nonempty++;
			}
		}
		System.err.println("nonempty columns = " + nonempty);

		double density = 0.0;
		for (int i = min; i <= max; i++) {
			if (map.containsKey(i)) {
				density += map.get(i) * map.get(i) * 1.0 / rows / rows;
			}
		}
		density /= nonempty;

		System.err.println("density = " + density);

		br.close();
	}
}
