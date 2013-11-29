// qsort

// lesson:
// 1. retvalue should be right
// 2. printf stdio.h!
// 3. param numbers

#include <stdio.h>

void __swap(int a[], int x, int y) {
  int tmp = a[x];
  a[x] = a[y];
  a[y] = tmp;
}

int __partition(int a[], int p, int q) {
  int x = p - 1;
  int i;
  for (i = p; i < q; i++) {
    if (a[i] < a[q]) {
      __swap(a, x+1, i);
      x++;
    }
  }
  __swap(a, x+1, q);
  return x+1;
}

void __qsort(int a[], int p, int q) {
  if (p < q) {
    int r = __partition(a, p, q);
    __qsort(a, p, r-1);
    __qsort(a, r+1, q);
  }
}

void qsort(int a[], int n) {
  __qsort(a, 0, n-1);
}

int main() {
  int a[] = {9,8,7,6,5,4,3,2,1};
  int n = 9;
  qsort(a, n);
  int i;
  for (i=0;i<n;i++)
    printf("%d\n", a[i]);
  return 0;
}
