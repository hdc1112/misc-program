#include <stdio.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/types.h>
#include <unistd.h>

int main(void) {
  int fd;
  //if ((fd = open ("/tmp/hole.file", O_CREAT | O_WRONLY)) == -1 ) {
  if ((fd = open ("/tmp/hole.file", O_RDONLY)) == -1 ) {
    perror("/tmp/hole.file open failed");
    return 1;
  }
  
/*  char *a = "abcd";
  if (write(fd, a, 4) != 4) {
    perror("write fail 1");
    close(fd);
    return 1;
  }
  if (lseek(fd, 50, SEEK_CUR) == -1) {
    perror("lseek fail");
    close(fd);
    return 1;
  }
  if (write(fd, a, 4) != 4) {
    perror("write fail 1");
    close(fd);
    return 1;
  }*/

  char a[4];
  lseek(fd, 4, SEEK_CUR);
  read(fd, a, 4);
  putchar(a[0]);
  putchar(a[1]);
  putchar(a[2]);
  putchar(a[3]);

  close(fd);
  return 0;
}
