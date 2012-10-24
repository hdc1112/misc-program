public class MinWin {
  public static void main(String[] args) {
    // char[] s = "abxcxxbxa".toCharArray();
    // char[] s = "abaoewhrqpfsz".toCharArray();
    // char[] s = "abnc".toCharArray();
    // char[] s = "axbxxcxba".toCharArray();
    // char[] s = "aabbccdd".toCharArray();
    char[] s = "qpkjsvlakjdvbzpvqpewqraiqpihvcqazcb".toCharArray();

    // pattern
    char[] t = "abc".toCharArray();

    roll(s, t);
  }

  public static void roll(char[] s, char[] t) {
    // MinWin's length
    int min = Integer.MAX_VALUE;
    // MinWin's starting index, ending index
    int min_s, min_e;
    min_s = min_e = -1;

    // in the pattern
    boolean[] exist = new boolean[256];
    for (int i = 0; i < t.length; i++)
      exist[(int) t[i]] = true;

    // a naive queue
    char[] q = new char[s.length];
    int head, tail;
    head = tail = 0;

    // queue's ele's index
    int[] qi = new int[s.length];

    // in queue?
    boolean[] inq = new boolean[256];

    // a temp beginning index
    int begin = -1;

    for (int i = 0; i < s.length; i++) {
      if (exist[(int) s[i]]) {
        // if this character is one of us
        if (tail == head) {
          // if queue is empty
          q[tail] = s[i];
          inq[(int) s[i]] = true;
          qi[tail] = i;
          tail++;
          begin = i;
        } else {
          // if queue is not empty
          if (q[head] == s[i]) {
            // if this character is the same
            // with the head character
            // inq[(int)q[head]] = false;
            head++;
            begin = qi[head];

            q[tail] = s[i];
            qi[tail] = i;
            tail++;
          } else {
            // if this character is not the same
            // with the head character
            if (inq[(int) s[i]])
              // if this character shows up
              // in the queue
              continue;
            q[tail] = s[i];
            qi[tail] = i;
            tail++;
            inq[(int) s[i]] = true;
          }

          if (tail - head == t.length) {
            if (i - begin + 1 < min) {
              min = i - begin + 1;
              min_s = begin;
              min_e = i;
            }
            inq[(int) q[head]] = false;
            head++;
            begin = qi[head];
          }
        }
      } else {
        // if this character is not one of us
        continue;
      }
    }

    if (min != Integer.MAX_VALUE)
      System.out
        .printf("MinWin is of length %d, starting index is %d, ending index is %d\n",
            min, min_s, min_e);
    else
      System.out.println("No match window found");

  }
}
