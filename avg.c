#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>

int main(void)
{
  char *line = NULL;
  size_t len = 0;
  ssize_t read;

  long sum = 0;
  int n = 0;
  long last;

  while ((read = getline(&line, &len, stdin)) != -1) {
    // printf("Retrieved line of length %zu :\n", read);
    n++;
    last = atol(line);
    sum += last;
  }

  printf("%f\n", (sum - last) / (double) (n - 1));

  free(line);
  exit(EXIT_SUCCESS);
}
