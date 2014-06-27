import java.util.Date;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CmdLineTest {

	public static void main(String[] args) throws ParseException {
		Options options = new Options();

		Option option1 = OptionBuilder.withArgName("inpath").hasArg()
				.isRequired().withDescription("hdfs input folder")
				.create("inpath");

		Option option2 = OptionBuilder.withArgName("outpath").hasArg()
				.isRequired().withDescription("hdfs output folder")
				.create("outpath");

		Option option3 = OptionBuilder.withArgName("columns").hasArg()
				.isRequired().withDescription("columns in the input")
				.create("columns");

		Option option4 = OptionBuilder.withArgName("minsupport").hasArg()
				.isRequired().withDescription("minimum support in percentage")
				.create("minsupport");

		Option option5 = OptionBuilder.withArgName("tolerate").hasArg()
				.withDescription("tolerate ratio, optional").create("tolerate");

		Option option6 = OptionBuilder.withArgName("enableOPT1").hasArg(false)
				.withDescription("enable OPT1 (cache)").create("enableOPT1");

		Option option7 = OptionBuilder.withArgName("enableOPT2").hasArg(false)
				.withDescription("enable OPT2 (tolerate)").create("enableOPT2");

		options.addOption(option1);
		options.addOption(option2);
		options.addOption(option3);
		options.addOption(option4);
		options.addOption(option5);
		options.addOption(option6);
		options.addOption(option7);

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);

		System.out.println(cmd.getOptionValue("inpath"));
		System.out.println(cmd.getOptionValue("outpath"));
		System.out.println(cmd.getOptionValue("columns"));
		System.out.println(cmd.getOptionValue("minsupport"));
		System.out.println(cmd.getOptionValue("tolerate", "1"));
		System.out.println(cmd.hasOption("enableOPT1"));
		System.out.println(cmd.hasOption("enableOPT2"));
	}
}
