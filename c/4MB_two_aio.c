#include <unistd.h>
#include <sys/time.h>
#include <aio.h>

static struct aiocb *wrapper_aio(int fd,
    volatile char *buf,
    size_t bytes,
    off_t offset,
    int opcode) {

  struct aiocb *cb = (struct aiocb *)malloc(sizeof(struct aiocb));
  if (cb == NULL)
    return NULL;
  memset(cb, 0, sizeof(struct aiocb));
  cb->aio_nbytes = bytes;
  cb->aio_fildes = fd;
  cb->aio_offset = offset;
  cb->aio_buf = buf;
  int ret;
  switch (opcode) {
    case WORMUP_READ:
      if ((ret = aio_read(cb)) == -1) {
        free(cb);
        return NULL;
      }
      break;
    case WORMUP_WRITE:
      if ((ret = aio_write(cb)) == -1) {
        free(cb);
        return NULL;
      }
      break;
    default:
      return NULL;
  }
  return cb;
}


int main(void) {
  int disk_fd, ssd_fd;
  if ((disk_fd = open("/tmp/10Gfile", O_RDONLY)) == -1) {
    perror("/tmp/10Gfile open failed");
    return 1;
  }
  if ((ssd_fd = open("/mnt/ssd/10Gfile", O_RDONLY)) == -1) {
    perror("/mnt/ssd/10Gfile open failed");
    return 1;
  }

  struct aiocb *ssd_cb = wrapper_aio(ssd_fd, temp, ssd_len, off, WORMUP_READ);
  struct aiocb *disk_cb = NULL;
  if (disk_len && NULL == (disk_cb = 
        wrapper_aio(disk_fd, temp + HEAD_SIZE_BYTES, 
          disk_len, off + HEAD_SIZE_BYTES, WORMUP_READ)))
    goto err_label;
  while (ssd_cb && aio_error(ssd_cb) == EINPROGRESS);
  while (disk_cb && aio_error(disk_cb) == EINPROGRESS);
}
