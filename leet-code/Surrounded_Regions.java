public class Surrounded_Regions {
	private final boolean UNVISITED = false;
	private final boolean VISITED = true;

	private int height;
	private int width;

	private boolean visited[][];

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

	private void _solve1(char[][] board) {
		height = board.length;
		width = board[0].length;

		visited = new boolean[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				visited[i][j] = UNVISITED;
			}
		}

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (visited[i][j] || board[i][j] == 'X') {
					continue;
				}

				boolean on_edge = dfs(board, i, j, UNVISITED);
				if (on_edge == false) {
					dfs(board, i, j, VISITED);
				}
			} /* end of 2nd for */
		}
	}

	public void solve(char[][] board) {
		// Start typing your Java solution below
		// DO NOT write main() function
		_solve1(board);
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
		char[][] board = { { 'X', 'X', 'X', 'O', 'X' },
				{ 'X', 'O', 'O', 'X', 'X' }, { 'X', 'X', 'O', 'O', 'X' },
				{ 'X', 'O', 'X', 'X', 'O' }, { 'O', 'X', 'X', 'O', 'X' } };
		printBoard(board);
		sr.solve(board);
		printBoard(board);
	}
}
