import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class PermuteRows {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: java PermuteRows inputfile");
			System.exit(1);
		}
		String filename = args[0];
		File file = new File(filename);

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		int linesnum = 0;
		while ((line = br.readLine()) != null) {
			linesnum++;
		}
		System.out.println(filename + " : " + linesnum);
		String[] rows = new String[linesnum];
		boolean[] occupied = new boolean[linesnum];

		br.close();

		br = new BufferedReader(new FileReader(file));

		int[] newplace = new int[linesnum];
		for (int i = 0; i < linesnum; i++) {
			newplace[i] = i;
		}
		FisherYatesShuffle.shuffleArray(newplace);
		for (int i = 0; i < linesnum; i++) {
			System.out.print(newplace[i] + " ");
		}
		System.out.println();

		int pointer = -1;
		while ((line = br.readLine()) != null) {
			pointer++;
			rows[newplace[pointer]] = line;
		}

		br.close();

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename
				+ "-randpermute")));

		for (int i = 0; i < linesnum; i++) {
			bw.write(rows[i]);
			bw.write("\n");
		}

		bw.close();
	}
}
