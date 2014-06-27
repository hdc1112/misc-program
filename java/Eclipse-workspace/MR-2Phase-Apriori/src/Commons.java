public class Commons {

	// Warning:
	// Whenever you make change to the log, you increase this log_ver
	// "change" includes but not limited to update log info, add new log, delete
	// existing log.
	//
	// After you are satisfied with the current logging, make sure to
	// use git commit to get a commit id, such that three things are tightly
	// bundled together (loganalyzer_id, log_version, log_version_commit_id)
	//
	// this file will maintain (log_version, log_version_commit_id)
	// loganalyzer will maintain (loganalyzer_id, log_version)
	// Remember loganalyzer also has many other prerequisite configs, see
	// loganalyzer
	//
	// Once it's committed, any further change to logging is in the next
	// version. Prepare that any change will entail a new loganalyzer
	private static final int log_ver = 5;
	public static final String PREFIX = "[MR2PhaseApriori][logv " + log_ver
			+ "] ";

	public final static String DATASETSEPARATOR = " ";
	public final static String SEPARATOR = ",";
	public final static String PURCHASED = "1";

	// OPT1 description: write ">= minsup" freqs into HDFS as cache
	// OPT1's switch,
	// if set to "true", then it's opened;
	// if set to "false", then it doesn't exist.
	private static final boolean enableOPT1 = true;

	// OPT1 switch judge
	public static boolean enabledOPT1() {
		return enableOPT1;
	}

	// OPT2 description: write ">= alpha * minsup, < minsup" freqs
	// into HDFS as cache, alpha >=0 && alpha < 1
	// OPT2's switch
	// if OPT1 is true,
	// -- if set to "true", then it's opened;
	// -- if set to "false", then it doesn't exist.
	// if OPT1 is false,
	// -- then it doesn't exist.
	// i.e. OPT2 relies on OPT1
	// The intuition is that OPT2 is designed to cache more.
	private static final boolean enableOPT2 = true;

	// OPT2 switch judge
	public static boolean enabledOPT2() {
		return enableOPT1 && enableOPT2;
	}
}

// logv1, finished at b1666a7598188829df69eb3b9c0e0f477f42c52d
// logv2, finished at d162ba65148e29a8025423404c8ba18b7e3e5afe
