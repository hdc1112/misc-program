#include <stdio.h>
#include <sys/time.h>

int main() {
  struct timeval tv;
  // i don't know why strace can't 
  // capture gettimeofday syscall
  gettimeofday(&tv, NULL);

  return 0;
}

