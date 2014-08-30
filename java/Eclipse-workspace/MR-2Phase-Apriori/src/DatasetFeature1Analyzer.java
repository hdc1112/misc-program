import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;

// feature 1
// each item's occurrences
// sort them, descend
// count #items that appear above 100%
// ... 99%
// etc
public class DatasetFeature1Analyzer {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err
					.println("Usage: java DatasetFeature1Analyzer /path/to/file");
			System.exit(1);
		}
		String filepath = args[0];

		System.err.println("DatasetFeature1Analyzer");
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

		HashMap<Integer, Long> map = new HashMap<Integer, Long>();
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
				if (linebool[i] == true) {
					if (map.containsKey(i)) {
						map.put(i, map.get(i) + 1);
					} else {
						map.put(i, 1L);
					}
				}
			}
		}

		Long[] array = new Long[max + 1];
		int distinct = 0;
		for (int i = 0; i <= max; i++) {
			if (map.containsKey(i)) {
				distinct++;
				array[i] = map.get(i);
			} else {
				array[i] = new Long(0);
			}
		}

		System.err.println("distinct = " + distinct);

		Arrays.sort(array, new Comparator<Long>() {

			@Override
			public int compare(Long arg0, Long arg1) {
				return (int) (arg1.longValue() - arg0.longValue());
			}

		});

		int test = 0;
		for (int i = min; i <= max; i++) {
			// System.out.println(array[i]);
			if (array[i] == 0) {
				break;
			} else {
				test++;
			}
		}
		// System.out.println(test);

		Long[] array2 = new Long[distinct];
		for (int i = 0; i < distinct; i++) {
			array2[i] = array[i];
		}

		int points = 100;
		double step = 100.0 / points;
		int cursor = 0;
		// int denominator = distinct;
		double percent = 100.0;

		// small test input
		// rows = 5;
		// distinct = 5;
		// array2 = new Long[5];
		// array2[0] = 4L;
		// array2[1] = 4L;
		// array2[2] = 3L;
		// array2[3] = 1L;
		// array2[4] = 1L;
		// points = 5;
		// step = 20;
		// denominator = distinct;
		// percent = 100;

		int[] result = new int[points + 1];
		for (int now = 0; now <= points; now++) {
			for (; cursor < distinct;) {
				if (array2[cursor] * 100 >= percent * rows) {
					result[now] += 1;
					cursor++;
				} else {
					break;
				}
			}
			percent -= step;
		}

		for (int i = 0; i <= points; i++) {
			// System.out.println(result[i]);
			if (i == 0)
				continue;
			result[i] = result[i] + result[i - 1];
		}

		// for (int i = 0; i <= points; i++) {
		// System.out.println(result[i]);
		// }

		double[] res = new double[points + 1];
		for (int i = 0; i <= points; i++) {
			res[i] = result[i] * 1.0 / distinct;
		}

		// for (int i = 0; i <= points; i++) {
		// System.out.println(res[i]);
		// }

		percent = 0.0;
		for (int i = points; i >= 0; i--) {
			System.out.println(percent + " " + res[i]);
			percent += step;
		}

		double area = 0.0;
		for (int i = 1; i <= points; i++) {
			area += (res[i] + res[i - 1]) * 1.0 / 2 / points;
		}

		System.err.println(area);

		br.close();
	}
}
