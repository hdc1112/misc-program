import java.util.*;

public class Word_Ladder_II {
	public static void main(String[] args) {
		Word_Ladder_II w = new Word_Ladder_II();
		HashSet<String> set = new HashSet<String>();

		String[] array = { "kid", "tag", "pup", "ail", "tun", "woo", "erg",
				"luz", "brr", "gay", "sip", "kay", "per", "val", "mes", "ohs",
				"now", "boa", "cet", "pal", "bar", "die", "war", "hay", "eco",
				"pub", "lob", "rue", "fry", "lit", "rex", "jan", "cot", "bid",
				"ali", "pay", "col", "gum", "ger", "row", "won", "dan", "rum",
				"fad", "tut", "sag", "yip", "sui", "ark", "has", "zip", "fez",
				"own", "ump", "dis", "ads", "max", "jaw", "out", "btu", "ana",
				"gap", "cry", "led", "abe", "box", "ore", "pig", "fie", "toy",
				"fat", "cal", "lie", "noh", "sew", "ono", "tam", "flu", "mgm",
				"ply", "awe", "pry", "tit", "tie", "yet", "too", "tax", "jim",
				"san", "pan", "map", "ski", "ova", "wed", "non", "wac", "nut",
				"why", "bye", "lye", "oct", "old", "fin", "feb", "chi", "sap",
				"owl", "log", "tod", "dot", "bow", "fob", "for", "joe", "ivy",
				"fan", "age", "fax", "hip", "jib", "mel", "hus", "sob", "ifs",
				"tab", "ara", "dab", "jag", "jar", "arm", "lot", "tom", "sax",
				"tex", "yum", "pei", "wen", "wry", "ire", "irk", "far", "mew",
				"wit", "doe", "gas", "rte", "ian", "pot", "ask", "wag", "hag",
				"amy", "nag", "ron", "soy", "gin", "don", "tug", "fay", "vic",
				"boo", "nam", "ave", "buy", "sop", "but", "orb", "fen", "paw",
				"his", "sub", "bob", "yea", "oft", "inn", "rod", "yam", "pew",
				"web", "hod", "hun", "gyp", "wei", "wis", "rob", "gad", "pie",
				"mon", "dog", "bib", "rub", "ere", "dig", "era", "cat", "fox",
				"bee", "mod", "day", "apr", "vie", "nev", "jam", "pam", "new",
				"aye", "ani", "and", "ibm", "yap", "can", "pyx", "tar", "kin",
				"fog", "hum", "pip", "cup", "dye", "lyx", "jog", "nun", "par",
				"wan", "fey", "bus", "oak", "bad", "ats", "set", "qom", "vat",
				"eat", "pus", "rev", "axe", "ion", "six", "ila", "lao", "mom",
				"mas", "pro", "few", "opt", "poe", "art", "ash", "oar", "cap",
				"lop", "may", "shy", "rid", "bat", "sum", "rim", "fee", "bmw",
				"sky", "maj", "hue", "thy", "ava", "rap", "den", "fla", "auk",
				"cox", "ibo", "hey", "saw", "vim", "sec", "ltd", "you", "its",
				"tat", "dew", "eva", "tog", "ram", "let", "see", "zit", "maw",
				"nix", "ate", "gig", "rep", "owe", "ind", "hog", "eve", "sam",
				"zoo", "any", "dow", "cod", "bed", "vet", "ham", "sis", "hex",
				"via", "fir", "nod", "mao", "aug", "mum", "hoe", "bah", "hal",
				"keg", "hew", "zed", "tow", "gog", "ass", "dem", "who", "bet",
				"gos", "son", "ear", "spy", "kit", "boy", "due", "sen", "oaf",
				"mix", "hep", "fur", "ada", "bin", "nil", "mia", "ewe", "hit",
				"fix", "sad", "rib", "eye", "hop", "haw", "wax", "mid", "tad",
				"ken", "wad", "rye", "pap", "bog", "gut", "ito", "woe", "our",
				"ado", "sin", "mad", "ray", "hon", "roy", "dip", "hen", "iva",
				"lug", "asp", "hui", "yak", "bay", "poi", "yep", "bun", "try",
				"lad", "elm", "nat", "wyo", "gym", "dug", "toe", "dee", "wig",
				"sly", "rip", "geo", "cog", "pas", "zen", "odd", "nan", "lay",
				"pod", "fit", "hem", "joy", "bum", "rio", "yon", "dec", "leg",
				"put", "sue", "dim", "pet", "yaw", "nub", "bit", "bur", "sid",
				"sun", "oil", "red", "doc", "moe", "caw", "eel", "dix", "cub",
				"end", "gem", "off", "yew", "hug", "pop", "tub", "sgt", "lid",
				"pun", "ton", "sol", "din", "yup", "jab", "pea", "bug", "gag",
				"mil", "jig", "hub", "low", "did", "tin", "get", "gte", "sox",
				"lei", "mig", "fig", "lon", "use", "ban", "flo", "nov", "jut",
				"bag", "mir", "sty", "lap", "two", "ins", "con", "ant", "net",
				"tux", "ode", "stu", "mug", "cad", "nap", "gun", "fop", "tot",
				"sow", "sal", "sic", "ted", "wot", "del", "imp", "cob", "way",
				"ann", "tan", "mci", "job", "wet", "ism", "err", "him", "all",
				"pad", "hah", "hie", "aim", "ike", "jed", "ego", "mac", "baa",
				"min", "com", "ill", "was", "cab", "ago", "ina", "big", "ilk",
				"gal", "tap", "duh", "ola", "ran", "lab", "top", "gob", "hot",
				"ora", "tia", "kip", "han", "met", "hut", "she", "sac", "fed",
				"goo", "tee", "ell", "not", "act", "gil", "rut", "ala", "ape",
				"rig", "cid", "god", "duo", "lin", "aid", "gel", "awl", "lag",
				"elf", "liz", "ref", "aha", "fib", "oho", "tho", "her", "nor",
				"ace", "adz", "fun", "ned", "coo", "win", "tao", "coy", "van",
				"man", "pit", "guy", "foe", "hid", "mai", "sup", "jay", "hob",
				"mow", "jot", "are", "pol", "arc", "lax", "aft", "alb", "len",
				"air", "pug", "pox", "vow", "got", "meg", "zoe", "amp", "ale",
				"bud", "gee", "pin", "dun", "pat", "ten", "mob" };

		// String[] array = { "hot","cog","dot","dog","hit","lot","log" };
		for (String e : array) {
			set.add(e);
		}
		ArrayList<ArrayList<String>> ret = w.findLadders("cet", "ism", set);
		for (ArrayList<String> out : ret) {
			for (String s : out) {
				System.out.print(s + " ");
			}
			System.out.println();
		}
	}

	public ArrayList<ArrayList<String>> findLadders(String start, String end,
			HashSet<String> dict) {
		// Start typing your Java solution below
		// DO NOT write main() function
		// return _solve1(start, end, dict);
		return _solve2(start, end, dict);
	}

	/* _solve1 brute force */
	private ArrayList<ArrayList<String>> _solve1(String start, String end,
			HashSet<String> dict) {
		HashSet<String> set = new HashSet<String>();
		set.add(start);
		ArrayList<ArrayList<String>> ret = __solve1(start, end, dict, set);
		int min = Integer.MAX_VALUE;
		for (ArrayList<String> a : ret) {
			if (a.size() < min) {
				min = a.size();
			}
		}
		for (Iterator<ArrayList<String>> it = ret.iterator(); it.hasNext();) {
			ArrayList<String> a = it.next();
			if (a.size() > min) {
				it.remove();
			}
		}
		return ret;
	}

	private ArrayList<ArrayList<String>> __solve1(String start, String end,
			HashSet<String> dict, HashSet<String> used) {
		if (onedistance(start, end)) {
			ArrayList<String> a = new ArrayList<String>();
			a.add(end);
			a.add(0, start);
			ArrayList<ArrayList<String>> b = new ArrayList<ArrayList<String>>();
			b.add(a);
			return b;
		}
		ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
		for (String e : dict) {
			if (used.contains(e))
				continue;
			if (!onedistance(start, e))
				continue;
			used.add(e);
			ArrayList<ArrayList<String>> sub = __solve1(e, end, dict, used);
			used.remove(e);
			for (ArrayList<String> s : sub) {
				s.add(0, start);
				ret.add(s);
			}
		}
		return ret;
	}

	/* _solve2, graph, bfs, dfs */
	static class GraphNode {
		int index;
		String string;
		ArrayList<Integer> neighbors;
		boolean bfsvisited = false;
		boolean dfsvisited = false;

		public GraphNode(int i, String s) {
			this.index = i;
			this.string = s;
			this.neighbors = new ArrayList<Integer>();
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("index = " + index + "; ");
			sb.append("string = " + string + "; ");
			sb.append("neighbors = ");
			for (Integer i : neighbors) {
				sb.append(i + " ");
			}
			sb.append("; ");
			sb.append("dfsvisited = " + this.dfsvisited + "\n");
			return sb.toString();
		}
	}

	private ArrayList<ArrayList<String>> _solve2(String start, String end,
			HashSet<String> dict) {
		dict.remove(start);
		dict.remove(end);
		GraphNode[] graph = ctr_graph(start, end, dict);
		// printGraph(graph);
		int length = bfs(graph, 0, 1);
		ArrayList<ArrayList<String>> answer = dfs(graph, 0, 1, 1, length);
		return answer;
	}

	private GraphNode[] ctr_graph(String start, String end, HashSet<String> dict) {
		GraphNode startnode = new GraphNode(0, start);
		GraphNode endnode = new GraphNode(1, end);
		GraphNode[] graph = new GraphNode[dict.size() + 2];
		graph[0] = startnode;
		graph[1] = endnode;
		int i = 2;
		for (String s : dict) {
			graph[i] = new GraphNode(i, s);
			i++;
		}
		for (int x = 0; x < graph.length; x++) {
			for (int y = x + 1; y < graph.length; y++) {
				if (!onedistance(graph[x].string, graph[y].string)) {
					continue;
				}
				graph[x].neighbors.add(y);
				graph[y].neighbors.add(x);
			}
		}
		return graph;
	}

	private void printGraph(GraphNode[] graph) {
		for (GraphNode g : graph) {
			System.out.println(g);
		}
	}

	private int bfs(GraphNode[] graph, int s, int e) {
		Queue<Integer> q = new LinkedList<Integer>();
		q.add(s);
		q.add(-1);
		int currentLevel = 1;
		while (q.isEmpty() == false) {
			int h = q.remove();
			if (h < 0) {
				if (q.isEmpty() == false) {
					q.add(h - 1);
					currentLevel = -(h - 1);
				}
				continue;
			}
			if (h == e) {
				return currentLevel;
			}
			graph[h].bfsvisited = true;
			for (Integer n : graph[h].neighbors) {
				if (graph[n].bfsvisited == false) {
					q.add(n);
				}
			}
		}

		return currentLevel;
	}

	private ArrayList<ArrayList<String>> dfs(GraphNode[] graph, int s, int e,
			int current, int limit) {
		if (current == limit) {
			ArrayList<ArrayList<String>> aa = new ArrayList<ArrayList<String>>();
			if (s == e) {
				ArrayList<String> a = new ArrayList<String>();
				a.add(graph[e].string);
				aa.add(a);
			}
			return aa;
		}
		graph[s].dfsvisited = true;
		ArrayList<ArrayList<String>> answer = new ArrayList<ArrayList<String>>();
		for (Integer n : graph[s].neighbors) {
			if (graph[n].dfsvisited == false) {
				ArrayList<ArrayList<String>> sub = dfs(graph, n, e,
						current + 1, limit);
				if (sub.size() > 0) {
					for (ArrayList<String> ele : sub) {
						ele.add(0, graph[s].string);
						answer.add(ele);
					}
				} else {
					// heuristic
					graph[s].dfsvisited = false;
				}
			}
		}
		return answer;
	}

	private boolean onedistance(String a, String b) {
		if (a.length() != b.length() || a.equals(b) == true) {
			return false;
		}
		int diff = 0;
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) != b.charAt(i)) {
				diff++;
				if (diff >= 2) {
					return false;
				}
			}
		}
		return true;
	}
}