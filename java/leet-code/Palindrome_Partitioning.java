import java.util.ArrayList;
import java.util.Iterator;

// public class Palindrome_Partitioning {
public class Solution {
  private boolean isPalindrome(char[] a, int s, int e) {
    if (s > e) {
      return true;
    }
    while (s <= e) {
      if (a[s] != a[e]) {
        return false;
      }
      s++;
      e--;
    }
    return true;
  }

  private ArrayList<ArrayList<String>> retElement(Object[] a, int i) {
    return (ArrayList<ArrayList<String>>)a[i];
  }

  private ArrayList<ArrayList<String>> solve(char[] s, int len) {
    Object[] a = new Object[len];
    for (int i = 0; i < len; i++ ) {
      a[i] = new ArrayList<ArrayList<String>>();
    }

    final int rlimit = len - 1;
    for (int loop = rlimit; loop >= 0; loop--) {
      if (loop == rlimit) {
        if (isPalindrome(s, loop, rlimit)) {
          ArrayList<String> as = new ArrayList<String>();
          as.add(new String(s, rlimit, 1));
          retElement(a, loop).add(as);
        }
        continue;
      }
      for (int j = loop; j <= rlimit; j++) {
        if (j == rlimit) {
          if (isPalindrome(s, loop, j)) {
            ArrayList<String> as = new ArrayList<String>();
            as.add(new String(s, loop, rlimit - loop + 1));
            retElement(a, loop).add(as);
            continue;
          }
        }
        if (isPalindrome(s, loop, j)) {
          Iterator<ArrayList<String>> it = retElement(a, j + 1).iterator();
          for (; it.hasNext();) {
            ArrayList<String> as = new ArrayList<String>(it.next());
            as.add(0, new String(s, loop, j - loop + 1));
            retElement(a, loop).add(as);
          } /* end of 3rd for */
        }
      } /* end of 2nd for */
    } /* end of 1st for */

    return retElement(a, 0);
  }

  public ArrayList<ArrayList<String>> partition(String s) {
    char[] ca = s.toCharArray();
    return solve(ca, ca.length);
  }

  /* 
  public static void main(String[] args) {
    Palindrome_Partitioning pp = new Palindrome_Partitioning();
    ArrayList<ArrayList<String>> aas = pp.partition(new String("aaabba"));
    for (Iterator<ArrayList<String>> it = aas.iterator(); it.hasNext();) {
      ArrayList<String> as = it.next();
      for (Iterator<String> it2 = as.iterator(); it2.hasNext();) {
        System.out.print(it2.next() + " ");
      }
      System.out.println();
    }
  }
  */
}
