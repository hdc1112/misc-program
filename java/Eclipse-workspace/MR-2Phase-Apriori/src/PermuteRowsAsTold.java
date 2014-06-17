import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class PermuteRowsAsTold {
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java PermuteRows inputfile permuteflie");
			System.exit(1);
		}
		String filename = args[0];
		File file = new File(filename);

		String permutefile = args[1];
		File filepermute = new File(permutefile);

		BufferedReader br = new BufferedReader(new FileReader(filepermute));
		String brline = null;
		int rowcursor = 0;
		ArrayList<Integer> array = new ArrayList<Integer>();
		while ((brline = br.readLine()) != null) {
			rowcursor++;
			StringTokenizer st = new StringTokenizer(brline);
			while (st.hasMoreTokens()) {
				int i = Integer.parseInt(st.nextToken());
				array.add(i);
			}
		}

		int[] newplace = new int[array.size()];
		for (int i = 0; i < array.size(); i++) {
			newplace[i] = array.get(i);
		}

		for (int i = 0; i < array.size(); i++) {
			// for (int i = 0; i < Math.min(linesnum, 100); i++) {
			System.out.print(newplace[i] + " ");
		}
		System.out.println();

		// System.err.println("array.size() = " + array.size());

		BufferedReader brreal = new BufferedReader(new FileReader(file));
		String line;
		int linesnum = 0;
		while ((line = brreal.readLine()) != null) {
			linesnum++;
		}
		// System.err.println(filename + " : " + linesnum);

		assert (array.size() == linesnum);

		String[] rows = new String[linesnum];

		brreal.close();

		brreal = new BufferedReader(new FileReader(file));

		int pointer = -1;
		while ((line = brreal.readLine()) != null) {
			pointer++;
			rows[newplace[pointer]] = line;
		}

		brreal.close();

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename
				+ "-permuteastold")));

		for (int i = 0; i < array.size(); i++) {
			// System.err.println(i);
			bw.write(rows[i]);
			bw.write("\n");
		}

		bw.close();
	}
}
