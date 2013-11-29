#include <stdio.h>

int main(void) {
  switch(3) {
    case 1:
      printf("1\n");
      break;
    case 2:
      printf("2\n");
      break;
    default:
      printf("default\n");
    case 3:
      printf("3\n");
      break;
  }
  return 0;
}
