// stock
// buy in, sell out. maximize difference

#include <stdio.h>

void calculate(int [], int, int*, int*);

int main() {
  int price[] = { 1, 3, 2};
  int start, end;
  calculate(price, sizeof(price) / sizeof(price[0]), &start, &end);
  printf("price[%d] - price[%d] = %d\n", end, start, price[end] - price[start]);
  
  return 0;
}

void calculate(int price[], int len, int *start, int *end) {
  int min = price[0];
  *start = 0;

  int max = 0;
  *end = 0;

  int i;
  for (i = 1; i < len; i++) {
    if (price[i] < min) {
      min = price[i];
      *start = i;
    }
    if (price[i] - min > max)  {
      max = price[i] - min;
      *end = i;
    }
  }
}
