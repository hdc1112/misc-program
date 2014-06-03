import java.util.ArrayList;
import java.util.StringTokenizer;

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
	private int minsupport;
	private ArrayList<String> dataset;

	private ArrayList<String> candidates;

	public final static String SEPARATOR = " ";
	public final static String PURCHASED = "1";

	public LocalApriori(int transactions, int items, int minsupport,
			ArrayList<String> dataset) {
		this.transactions = transactions;
		this.items = items;
		this.minsupport = minsupport;
		this.dataset = dataset;

		this.candidates = new ArrayList<String>();
	}

	public void apriori() {
		int itemsetNumber = 0;
		do {
			itemsetNumber++;
			generateCandidates(itemsetNumber);
			calculateFrequentItemsets(itemsetNumber);
			// if you want to get all length
			// frequent itemset, you must
			// store the candidates to some
			// other place at here
//		} while (candidates.size() > 1);
		} while (itemsetNumber <= 1);
	}

	public ArrayList<String> frequentItemset() {
		return candidates;
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
			candidates = tempCandidates;
			return;
		}

		if (number == 2) {
			for (int i = 0; i < candidates.size(); i++) {
				// at this stage, candidates.get(i) contains
				// only one token. The candidates.get(i)
				// will look like "3 4 5"
				st1 = new StringTokenizer(candidates.get(i));
				str1 = st1.nextToken();
				for (int j = i + 1; j < candidates.size(); j++) {
					st2 = new StringTokenizer(candidates.get(j));
					str2 = st2.nextToken();
					tempCandidates.add(str1 + " " + str2);
				}
			} // end of for
			candidates = tempCandidates;
			return;
		}

		for (int i = 0; i < candidates.size(); i++) {
			for (int j = i + 1; j < candidates.size(); j++) {
				str1 = new String();
				str2 = new String();
				st1 = new StringTokenizer(candidates.get(i));
				st2 = new StringTokenizer(candidates.get(j));
				for (int s = 0; s < number - 2; s++) {
					str1 = str1 + " " + st1.nextToken();
					str2 = str2 + " " + st2.nextToken();
				}
				if (str2.compareToIgnoreCase(str1) == 0) {
					String can = str1 + " " + st1.nextToken() + " "
							+ st2.nextToken();
					tempCandidates.add(can.trim());
				}
			}
			candidates = tempCandidates;
			return;
		}
	}

	private void calculateFrequentItemsets(int number) {
		ArrayList<String> frequentCandidates = new ArrayList<String>();
		StringTokenizer st, stFile;
		boolean match;
		boolean[] trans = new boolean[items];
		int[] count = new int[candidates.size()];

		for (int i = 0; i < transactions; i++) {
			stFile = new StringTokenizer(dataset.get(i), SEPARATOR);
			for (int j = 0; j < items; j++) {
				trans[j] = stFile.nextToken().equalsIgnoreCase(PURCHASED);
			}

			for (int c = 0; c < candidates.size(); c++) {
				match = false;
				st = new StringTokenizer(candidates.get(c));
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
		for (int i = 0; i < candidates.size(); i++) {
			if ((count[i] / (double) transactions) >= (minsupport / 100.0)) {
				frequentCandidates.add(candidates.get(i));
			}
		}
		candidates = frequentCandidates;
	}
}
