import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* this program is adapted from Apriori.java whose author is Nathan Magnus */
/* the original link is http://www2.cs.uregina.ca/~dbd/cs831/notes/itemsets/Apriori.java */
/* this program is re-written for my MR2PhaseApriori.java */
/* the original copyright announcement is copied at here as required */
/* @author Nathan Magnus, under the supervision of Howard Hamilton */
/* Copyright: University of Regina, Nathan Magnus and Su Yibin, June 2009. */
/* No reproduction in whole or part without maintaining this copyright notice */
/* and imposing this condition on any subsequent users. */

public class LocalApriori {

	private int items;
	private int transactions;
	// private int minsupport;
	private double minsupport;
	private ArrayList<String> dataset;

	private ArrayList<ArrayList<String>> g_candidates = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<Integer>> g_frequencies = new ArrayList<ArrayList<Integer>>();

	private ArrayList<String> candidates = new ArrayList<String>();
	private ArrayList<Integer> frequencies = new ArrayList<Integer>();
	private int totalloops = 0;

	public LocalApriori(int transactions, int items, double minsupport,
			ArrayList<String> dataset) {
		this.transactions = transactions;
		this.items = items;
		this.minsupport = minsupport;
		this.dataset = dataset;
		// if you use this constructor, then tolerate will be 1,
		// so no matter OPT2 is enabled or not, the final tolerate
		// candidates will be empty, the same with OPT2 disabled.
	}

	public void apriori() {
		int itemsetNumber = 0;

		int loop = 0;
		do {
			itemsetNumber++;

			loop++;
			System.err.println(Commons.PREFIX + "Starting loop: " + loop);
			long loopstart = System.currentTimeMillis();

			// after these two calls
			// candidates will store the frequent itemset
			// count[] will store each one's occurrences
			generateCandidates(itemsetNumber);

			System.err.println(Commons.PREFIX + "Before filtering in loop "
					+ loop + " number of candidates: " + candidates.size());
			long loopmid = System.currentTimeMillis();

			calculateFrequentItemsets(itemsetNumber);

			// add this iteration's result into global result
			// these two lines of code are based on the
			// assumption there won't be any "write" operation
			// on the data structure. So far, this assumption
			// is true.
			g_candidates.add(candidates);
			g_frequencies.add(frequencies);

			// OPT2, if disabled, there won't be any
			// tolerating candidates
			if (Commons.enabledOPT2()) {
				g_tol_candidates.add(tol_candidates);
				g_tol_frequencies.add(tol_frequencies);
			}

			long loopend = System.currentTimeMillis();
			System.err.println(Commons.PREFIX + "After loop " + loop
					+ " number of candidates: " + candidates.size());
			System.err.println(Commons.PREFIX + "Ending loop: " + loop
					+ " Takes " + (loopend - loopstart) + ", filtering takes "
					+ (loopend - loopmid) + " Percentage: "
					+ ((loopend - loopmid) / (double) (loopend - loopstart)));
			System.err.println();

		} while (candidates.size() > 1);

		totalloops = loop;
	}

	public ArrayList<ArrayList<String>> frequentItemset() {
		return g_candidates;
	}

	public ArrayList<ArrayList<Integer>> frequencies() {
		return g_frequencies;
	}

	public int getTotalLoops() {
		return totalloops;
	}

	private void generateCandidates(int number) {
		ArrayList<String> tempCandidates = new ArrayList<String>();
		String str1, str2;
		StringTokenizer st1, st2;

		// one example of candidates is like
		// [ "3 4 5", "1 2 3"]

		if (number == 1) {
			for (int i = 1; i <= items; i++) {
				tempCandidates.add(Integer.toString(i));
			}
		}

		if (number == 2) {
			for (int i = 0; i < candidates.size(); i++) {
				// at this stage, candidates.get(i) contains
				// only one token. The candidates.get(i)
				// will look like "3 4 5"
				st1 = new StringTokenizer(candidates.get(i), Commons.SEPARATOR);
				str1 = st1.nextToken();
				for (int j = i + 1; j < candidates.size(); j++) {
					st2 = new StringTokenizer(candidates.get(j),
							Commons.SEPARATOR);
					str2 = st2.nextToken();
					tempCandidates.add(concat(str1, str2));
				}
			}
		}

		if (number > 2) {
			for (int i = 0; i < candidates.size(); i++) {
				for (int j = i + 1; j < candidates.size(); j++) {
					str1 = new String();
					str2 = new String();
					st1 = new StringTokenizer(candidates.get(i),
							Commons.SEPARATOR);
					st2 = new StringTokenizer(candidates.get(j),
							Commons.SEPARATOR);
					for (int s = 0; s < number - 2; s++) {
						if (s == 0) {
							str1 = st1.nextToken();
							str2 = st2.nextToken();
						} else {
							str1 = concat(str1, st1.nextToken());
							str2 = concat(str2, st2.nextToken());
						}
					}
					if (str2.compareToIgnoreCase(str1) == 0) {
						String can = concat(concat(str1, st1.nextToken()),
								st2.nextToken());
						tempCandidates.add(can.trim());
					}
				}
			}
		}

		candidates = tempCandidates;
	}

	private void calculateFrequentItemsets(int number) {
		ArrayList<String> frequentCandidates = new ArrayList<String>();
		StringTokenizer st, stFile;
		boolean match;
		boolean[] trans = new boolean[items];
		int[] count = new int[candidates.size()];

		for (int i = 0; i < transactions; i++) {
			stFile = new StringTokenizer(dataset.get(i),
					Commons.DATASETSEPARATOR);
			for (int j = 0; j < items; j++) {
				trans[j] = stFile.nextToken().equalsIgnoreCase(
						Commons.PURCHASED);
			}

			for (int c = 0; c < candidates.size(); c++) {
				match = false;
				st = new StringTokenizer(candidates.get(c), Commons.SEPARATOR);
				while (st.hasMoreTokens()) {
					match = trans[Integer.parseInt(st.nextToken()) - 1];
					if (!match) {
						break;
					}
				}
				if (match) {
					count[c]++;
				}
			}
		}

		// OPT2
		if (Commons.enabledOPT2()) {
			tol_candidates = new ArrayList<String>();
			tol_frequencies = new ArrayList<Integer>();
		}

		frequencies = new ArrayList<Integer>();
		for (int i = 0; i < candidates.size(); i++) {
			if ((count[i] / (double) transactions) >= (minsupport / 100.0)) {
				frequentCandidates.add(candidates.get(i));
				frequencies.add(count[i]);
			} else {
				if (Commons.enabledOPT2()) {
					if ((count[i] / (double) transactions) >= (tolerate
							* minsupport / 100.0)) {
						tol_candidates.add(candidates.get(i));
						tol_frequencies.add(count[i]);
					} else {
						// even in tolerance mode, this candidate didn't pass
					}
				}
			}
		}
		candidates = frequentCandidates;
	}

	private String concat(String s1, String s2) {
		return s1 + Commons.SEPARATOR + s2;
	}

	// OPT2
	// correctness of one constructor depends on this default value
	private double tolerate = 1; // by default zero tolerance
	private ArrayList<String> tol_candidates = new ArrayList<String>();
	private ArrayList<Integer> tol_frequencies = new ArrayList<Integer>();

	private ArrayList<ArrayList<String>> g_tol_candidates = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<Integer>> g_tol_frequencies = new ArrayList<ArrayList<Integer>>();

	// OPT2
	// tolerate must be >=0, and <1, otherwise it would be treated as 1
	public LocalApriori(int transactions, int items, double minsupport,
			ArrayList<String> dataset, double tolerate) {
		this(transactions, items, minsupport, dataset);
		this.tolerate = tolerate;
		if (tolerate >= 1 || tolerate < 0) {
			System.err.println(Commons.PREFIX
					+ "Warning: Invalid tolerate, set to default value");
			this.tolerate = 1;
		}
		System.err.println(Commons.PREFIX + "tolerate = " + tolerate);
	}

	// OPT2
	public ArrayList<ArrayList<String>> tolerateItemset() {
		return g_tol_candidates;
	}

	// OPT2
	public ArrayList<ArrayList<Integer>> tolerateFrequencies() {
		return g_tol_frequencies;
	}
}
