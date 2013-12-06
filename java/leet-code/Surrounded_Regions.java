import java.util.Queue;
import java.util.LinkedList;

// This didn't pass leetcode test since
// the time exceeded the limitation.
// I have tried one cpp code downloaded
// from web, it only took 64ms to run 56 tests,
// however, my java code takes above 1s to run
// only one test. They are in different
// machines, but this is already quite impressive
// for the slowness of java.
public class Surrounded_Regions {
  private final boolean UNVISITED = false;
  private final boolean VISITED = true;

  private int height;
  private int width;

  private boolean visited[][];

  class Coordinate {
    int i, j;

    Coordinate(int x, int y) {
      this.i = x;
      this.j = y;
    }
  }

  private void bfs4(char[][] board, char[][] retbd, final int i, final int j) {

    Queue<Coordinate> q = new LinkedList<Coordinate>();
    q.add(new Coordinate(i, j));

    int up_x, up_y;
    int down_x, down_y;
    int left_x, left_y;
    int right_x, right_y;

    Coordinate e = null;
    while (q.isEmpty() == false) {
      e = q.remove();
      board[e.i][e.j] = 'X';
      retbd[e.i][e.j] = 'O';

      up_x = e.i - 1;
      up_y = e.j;
      down_x = e.i + 1;
      down_y = e.j;
      left_x = e.i;
      left_y = e.j - 1;
      right_x = e.i;
      right_y = e.j + 1;

      if (up_x >= 0 && board[up_x][up_y] == 'O') {
        q.add(new Coordinate(up_x, up_y));
      }

      if (down_x <= height - 1 && board[down_x][down_y] == 'O') {
        q.add(new Coordinate(down_x, down_y));
      }

      if (left_y >= 0 && board[left_x][left_y] == 'O') {
        q.add(new Coordinate(left_x, left_y));
      }

      if (right_y <= width - 1 && board[right_x][right_y] == 'O') {
        q.add(new Coordinate(right_x, right_y));
      }
    } /* end of while */
  }


  private void bfs2(char[][] board, final int i, final int j, boolean toX) {

    Queue<Coordinate> q = new LinkedList<Coordinate>();
    q.add(new Coordinate(i, j));

    int up_x, up_y;
    int down_x, down_y;
    int left_x, left_y;
    int right_x, right_y;

    Coordinate e = null;
    while (q.isEmpty() == false) {
      e = q.remove();
      visited[e.i][e.j] = true;
      board[e.i][e.j] = toX == true ? 'X' : 'O';

      up_x = e.i - 1;
      up_y = e.j;
      down_x = e.i + 1;
      down_y = e.j;
      left_x = e.i;
      left_y = e.j - 1;
      right_x = e.i;
      right_y = e.j + 1;

      if (up_x >= 0 && visited[up_x][up_y] == false && board[up_x][up_y] == 'O') {
        q.add(new Coordinate(up_x, up_y));
      }

      if (down_x <= height - 1 && visited[down_x][down_y] == false && board[down_x][down_y] == 'O') {
        q.add(new Coordinate(down_x, down_y));
      }

      if (left_y >= 0 && visited[left_x][left_y] == false && board[left_x][left_y] == 'O') {
        q.add(new Coordinate(left_x, left_y));
      }

      if (right_y <= width - 1 && visited[right_x][right_y] == false && board[right_x][right_y] == 'O') {
        q.add(new Coordinate(right_x, right_y));
      }
    } /* end of while */
  }

  private boolean bfs(char[][] board, final int i, final int j,
      boolean original) {

    boolean ret = false;

    if (i == 0 || i == height - 1 || j == 0 || j == width - 1) {
      ret = true;
    }

    Queue<Coordinate> q = new LinkedList<Coordinate>();
    q.add(new Coordinate(i, j));

    int up_x, up_y;
    int down_x, down_y;
    int left_x, left_y;
    int right_x, right_y;

    Coordinate e = null;
    while (q.isEmpty() == false) {
      e = q.remove();
      visited[e.i][e.j] = original == UNVISITED ? VISITED : UNVISITED;

      if (original == VISITED) {
        board[e.i][e.j] = 'X';
      }

      up_x = e.i - 1;
      up_y = e.j;
      down_x = e.i + 1;
      down_y = e.j;
      left_x = e.i;
      left_y = e.j - 1;
      right_x = e.i;
      right_y = e.j + 1;

      if (up_x >= 0 && board[up_x][up_y] == 'O'
          && visited[up_x][up_y] == original) {
        if (up_x == 0) {
          ret = true;
        }
        q.add(new Coordinate(up_x, up_y));
          }

      if (down_x <= height - 1 && board[down_x][down_y] == 'O'
          && visited[down_x][down_y] == original) {
        if (down_x == height - 1) {
          ret = true;
        }
        q.add(new Coordinate(down_x, down_y));
          }

      if (left_y >= 0 && board[left_x][left_y] == 'O'
          && visited[left_x][left_y] == original) {
        if (left_y == 0) {
          ret = true;
        }
        q.add(new Coordinate(left_x, left_y));
          }

      if (right_y <= width - 1 && board[right_x][right_y] == 'O'
          && visited[right_x][right_y] == original) {
        if (right_y == width - 1) {
          ret = true;
        }
        q.add(new Coordinate(right_x, right_y));
          }
    } /* end of while */

    return ret;
  }

  // return value: if on the edge, return true
  private boolean dfs(char[][] board, final int i, final int j,
      boolean original) {
    boolean ret = false;

    if (i == 0 || i == height - 1 || j == 0 || j == width - 1) {
      ret = true;
    }

    visited[i][j] = original == UNVISITED ? VISITED : UNVISITED;

    int up_x = i - 1, up_y = j;
    int down_x = i + 1, down_y = j;
    int left_x = i, left_y = j - 1;
    int right_x = i, right_y = j + 1;
    boolean ret_up, ret_down, ret_left, ret_right;

    ret_up = ret_down = ret_left = ret_right = false;

    if (up_x >= 0 && board[up_x][up_y] == 'O'
        && visited[up_x][up_y] == original) {
      ret_up = dfs(board, up_x, up_y, original);
        }

    if (down_x <= height - 1 && board[down_x][down_y] == 'O'
        && visited[down_x][down_y] == original) {
      ret_down = dfs(board, down_x, down_y, original);
        }

    if (left_y >= 0 && board[left_x][left_y] == 'O'
        && visited[left_x][left_y] == original) {
      ret_left = dfs(board, left_x, left_y, original);
        }

    if (right_y <= width - 1 && board[right_x][right_y] == 'O'
        && visited[right_x][right_y] == original) {
      ret_right = dfs(board, right_x, right_y, original);
        }

    if (original == VISITED) {
      board[i][j] = 'X';
    }

    if (ret) {
      return true;
    } else if (ret_up == true || ret_down == true || ret_left == true
        || ret_right == true) {
      return true;
        }

    return false;
  }

  private void _init(char[][] board) {
    height = board.length;
    width = board[0].length;

    // java would init this to 0
    visited = new boolean[height][width];
  }

  // _solve1:
  // Iterate the matrix, if it's 'O',
  // then DFS to judge whether it's on edge,
  // if it's not on edge, DFS again to flip to 'X'
  private void _solve1(char[][] board) {
    _init(board);

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (visited[i][j] || board[i][j] == 'X') {
          continue;
        }

        /*
         * boolean on_edge = dfs(board, i, j, UNVISITED); if (on_edge ==
         * false) { dfs(board, i, j, VISITED); }
         */

        boolean on_edge = bfs(board, i, j, UNVISITED);
        if (on_edge == false) {
          bfs(board, i, j, VISITED);
        }
      } /* end of 2nd for */
    } /* end of 1st for */
  }

  // _solve2:
  // first iterate four boundaries
  // mark 'O' as visited.
  // then bfs every internal 'O' and flip
  private void _solve2(char[][] board) {
    _init(board);

    int x = 0, y = 0;
    for (x = 0, y = 0; y <= width - 1; y++) {
      if (board[x][y] == 'O' && visited[x][y] == false) {
        bfs2(board, x, y, false);
      }
    }

    for (x = height - 1, y = 0; y <= width - 1; y++) {
      if (board[x][y] == 'O' && visited[x][y] == false) {
        bfs2(board, x, y, false);
      }
    }

    for (y = 0, x = 0; x <= height - 1; x++) {
      if (board[x][y] == 'O' && visited[x][y] == false) {
        bfs2(board, x, y, false);
      }
    }

    for (y = width - 1, x = 0; x <= height - 1; x++) {
      if (board[x][y] == 'O' && visited[x][y] == false) {
        bfs2(board, x, y, false);
      }
    }

    for (int i = 1; i <= height - 2; i++) {
      for (int j = 1; j <= width - 2; j++) {
        if (visited[i][j] || board[i][j] == 'X') {
          continue;
        }

        bfs2(board, i, j, true);
      }
    } /* end of 1st for */
  }

  // _solve3:
  // first iterate four boundaries
  // and then iterate the matrix
  // to make every unvisited to 'X'
  private void _solve3(char[][] board) {
    _init(board);

    int x = 0, y = 0;
    for (x = 0, y = 0; y <= width - 1; y++) {
      if (visited[x][y] == false && board[x][y] == 'O') {
        bfs2(board, x, y, false);
      }
    }

    for (x = height - 1, y = 0; y <= width - 1; y++) {
      if (visited[x][y] == false && board[x][y] == 'O') {
        bfs2(board, x, y, false);
      }
    }

    for (y = 0, x = 0; x <= height - 1; x++) {
      if (visited[x][y] == false && board[x][y] == 'O') {
        bfs2(board, x, y, false);
      }
    }

    for (y = width - 1, x = 0; x <= height - 1; x++) {
      if (visited[x][y] == false && board[x][y] == 'O') {
        bfs2(board, x, y, false);
      }
    }

    for (int i = 1; i <= height - 2; i++) {
      for (int j = 1; j <= width - 2; j++) {
        if (visited[i][j] == false) {
          board[i][j] = 'X';
        }
      }
    } /* end of 1st for */
  }

  private void _solve4(char[][] board) {
    _init(board);

    char[][] retbd = new char[height][width];

    int x = 0, y = 0;
    for (x = 0; x < height; x++) {
      for (y = 0; y < width; y++) {
        retbd[x][y] = 'X';
      }
    }

    for (x = 0, y = 0; y <= width - 1; y++) {
      if (visited[x][y] == false && board[x][y] == 'O') {
        bfs4(board, retbd, x, y);
      }
    }

    for (x = height - 1, y = 0; y <= width - 1; y++) {
      if (visited[x][y] == false && board[x][y] == 'O') {
        bfs4(board, retbd, x, y);
      }
    }

    for (y = 0, x = 0; x <= height - 1; x++) {
      if (visited[x][y] == false && board[x][y] == 'O') {
        bfs4(board, retbd, x, y);
      }
    }

    for (y = width - 1, x = 0; x <= height - 1; x++) {
      if (visited[x][y] == false && board[x][y] == 'O') {
        bfs4(board, retbd, x, y);
      }
    }

    board = retbd;
  }

  public void solve(char[][] board) {
    // Start typing your Java solution below
    // DO NOT write main() function
    if (board.length == 0 || board[0].length == 0)
      return;

    /*
     * _solve2, in theory, should be faster than _solve1, but when the grid
     * doesn't have too many 'O's which need to be traversed twice in
     * _solve1, then they have basically the same performance.
     */

    //_solve1(board);
    //_solve2(board);
    _solve3(board);
    //_solve4(board);
  }

  public static void printBoard(char[][] board) {
    int h = board.length;
    int w = board[0].length;

    for (int i = 0; i < h; i++) {
      for (int j = 0; j < w; j++) {
        System.out.print(board[i][j] + " ");
      }
      System.out.println();
    }
    System.out.println();
  }

  public static void main(String[] args) {
    Surrounded_Regions sr = new Surrounded_Regions();
    /*
     * char[][] board = { { 'X', 'X', 'X', 'O', 'X' }, { 'X', 'O', 'O', 'X',
     * 'X' }, { 'X', 'X', 'O', 'O', 'X' }, { 'X', 'O', 'X', 'X', 'O' }, {
     * 'O', 'X', 'X', 'O', 'X' } };
     */

    char[][] board = { "XOOOOOOOOOOOOOOOOOOO".toCharArray(),
      "OXOOOOXOOOOOOOOOOOXX".toCharArray(),
      "OOOOOOOOXOOOOOOOOOOX".toCharArray(),
      "OOXOOOOOOOOOOOOOOOXO".toCharArray(),
      "OOOOOXOOOOXOOOOOXOOX".toCharArray(),
      "XOOOXOOOOOXOXOXOXOXO".toCharArray(),
      "OOOOXOOXOOOOOXOOXOOO".toCharArray(),
      "XOOOXXXOXOOOOXXOXOOO".toCharArray(),
      "OOOOOXXXXOOOOXOOXOOO".toCharArray(),
      "XOOOOXOOOOOOXXOOXOOX".toCharArray(),
      "OOOOOOOOOOXOOXOOOXOX".toCharArray(),
      "OOOOXOXOOXXOOOOOXOOO".toCharArray(),
      "XXOOOOOXOOOOOOOOOOOO".toCharArray(),
      "OXOXOOOXOXOOOXOXOXOO".toCharArray(),
      "OOXOOOOOOOXOOOOOXOXO".toCharArray(),
      "XXOOOOOOOOXOXXOOOXOO".toCharArray(),
      "OOXOOOOOOOXOOXOXOXOO".toCharArray(),
      "OOOXOOOOOXXXOOXOOOXO".toCharArray(),
      "OOOOOOOOOOOOOOOOOOOO".toCharArray(),
      "XOOOOXOOOXXOOXOXOXOO".toCharArray() };
    printBoard(board);
    long t1 = System.currentTimeMillis();
    sr.solve(board);
    long t2 = System.currentTimeMillis();
    printBoard(board);
    System.out.println(t2 - t1);
  }
}
