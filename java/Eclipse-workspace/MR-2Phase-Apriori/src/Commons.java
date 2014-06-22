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
	// Remember loganalyzer also has many other prerequisite configs, see loganalyzer
	//
	// Once it's committed, any further change to logging is in the next version.
	// Prepare that any change will entail a new loganalyzer
	private static int log_ver = 2;
	public static String PREFIX = "[MR2PhaseApriori][logv " + log_ver + "] ";

	public final static String DATASETSEPARATOR = " ";
	public final static String SEPARATOR = ",";
	public final static String PURCHASED = "1";
}

// logv1, finished at b1666a7598188829df69eb3b9c0e0f477f42c52d
// logv2, finished at d162ba65148e29a8025423404c8ba18b7e3e5afe