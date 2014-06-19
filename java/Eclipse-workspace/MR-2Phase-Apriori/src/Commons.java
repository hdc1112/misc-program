public class Commons {

	// Warning:
	// Whenever you make change to the log, you increase this log_ver
	// "change" includes but not limited to update log info, add new log, delete
	// existing log.
	// After you are satisfied with the current logging, make sure to
	// use git commit to get a commit id, such that three things are tightly
	// bundled together (loganalyzer_id, log_version, log_version_commit_id)
	// this file will maintain (log_version, log_version_commit_id)
	// loganalyzer will maintain (loganalyzer_id, log_version)
	private static int log_ver = 1;
	public static String PREFIX = "[MR2PhaseApriori][logv " + log_ver + "] ";

	public final static String DATASETSEPARATOR = " ";
	public final static String SEPARATOR = ",";
	public final static String PURCHASED = "1";
}
