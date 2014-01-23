#include <stdio.h>

int main() {
  int a = 1;
  (a ? printf("h1\n"), 1 : printf("h2\n"), 2) ? printf("h3\n"), 3 : printf("h4\n"), 4;
  printf("\n");
  printf("\n");
  printf("\n");
  a ? printf("h1\n"), 1 : (printf("h2\n"), 2 ? printf("h3\n"), 3 : printf("h4\n"), 4);
  printf("\n");
  printf("\n");
  printf("\n");
  a ? printf("h1\n"), 1 : printf("h2\n"), 2 ? printf("h3\n"), 3 : printf("h4\n"), 4;
  return 0;
}
