import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class PermuteRows {
	public static void main(String[] args) throws IOException, ParseException {

		// definition stage
		Options options = buildOptions();

		// parsing stage
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);

		// interrogatin stage
		String filename = cmd.getOptionValue("datafile");
		String dumpfile = cmd.getOptionValue("permutefile", null);

		// cmd show stage
		System.err.println("filename: " + filename);
		System.err.println("dumpfile: " + dumpfile);

		// cmd verify stage (skip)

		// main logic
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
		System.out.println("Printing this random permutation ...");
		for (int i = 0; i < linesnum; i++) {
			// for (int i = 0; i < Math.min(linesnum, 100); i++) {
			System.out.print(newplace[i] + " ");
		}
		System.out.println();

		// dump permutation to a file
		if (dumpfile != null) {
			BufferedWriter perbw = new BufferedWriter(new FileWriter(new File(
					dumpfile)));
			for (int i = 0; i < linesnum; i++) {
				perbw.write(Integer.toString(newplace[i]) + " ");
			}
			perbw.close();
		}

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

	private static Options buildOptions() {
		Options options = new Options();

		Option option1 = OptionBuilder.withArgName("permutefile").hasArg()
				.withDescription("Dump permutation to a file")
				.create("permutefile");

		Option option2 = OptionBuilder.withArgName("datafile").hasArg()
				.isRequired().withDescription("Data input file")
				.create("datafile");

		options.addOption(option1);
		options.addOption(option2);

		return options;
	}
}
