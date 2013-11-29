// matrix
// each cell has a[i][j] grains
// go from 0,0 to n,m to maximize
// the grains collected. n is the row
// m is the column
// don't need to know the path

// lesson:
// 1. "type of formal parameter 1 is incomplete"
//    shows when pass >=2 dimension array into a a[][]
//    it should be a[3][4], or a[][4]

#include <stdio.h>
#include <stdlib.h>

int grainsmatrix(int matrix[][3], const int n, const int m) {
  int i, j;

  int *help = (int*)malloc(sizeof(int) * m);

  help[0] = matrix[0][0];
  for (i = 0, j = 1; j < m; j++) {
    help[j] = help[j - 1] + matrix[i][j];
  }

  for (i = 1; i < n; i++) {
    help[0] = help[0] + matrix[i][0];
    for (j = 1; j < m; j++) {
      help[j] = matrix[i][j] + 
                (help[j - 1] > help[j] ? help[j - 1] : help[j]);
    }
  }

  return help[m - 1];
}

int main() {
  int matrix[3][3] = {{1,2,3},{4,5,6},{7,8,9}};
  int grains = grainsmatrix(matrix, 3, 3);
  printf("%d\n", grains);
  return 0;
}
