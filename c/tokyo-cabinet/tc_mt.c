/* trivial */
#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>

/* db */
#include <tcutil.h>
#include <tcbdb.h>
#include <stdbool.h>
#include <stdint.h>
#include <string.h>

/* config params */
#define DEFAULT_BUF_SIZE 150
//#define NUM_THREADS 20
#define NUM_THREADS 20
#define DEFAULT_LOOP_SIZE 20
#define DEFAULT_DB_PATH "casket.tcb"
int loop_size;
char *dbpath;
/* config params end */

/* serial transaction */
#define SERIAL_TXN
//#undef SERIAL_TXN
/* serial transaction end */

/* transaction real work */
#define TXN_REAL_WORK
//#undef TXN_REAL_WORK
/* transaction real work end */


typedef struct thread_data {
  int id;
  TCBDB *bdb;
#ifdef SERIAL_TXN
  pthread_mutex_t *mutex;
#endif
} thread_data;

void config() {
  char *loop_size_env = NULL;
  char *dbpath_env = NULL;

  /* loop_size */
  if ((loop_size_env = getenv("TC_MT_FOLDER_LOOP_SIZE")) == NULL) {
    fprintf(stderr, "No TC_MT_FOLDER_LOOP_SIZE environment variable.\n");
    fprintf(stderr, "Use default loop_size = %d\n", DEFAULT_LOOP_SIZE);
    loop_size = DEFAULT_LOOP_SIZE;
  } else {
    fprintf(stderr, "User defined loop_size = %d\n", loop_size);
    loop_size = atoi(loop_size_env);
  }

  /* dbpath */
  if ((dbpath_env = getenv("TC_MT_FOLDER_DB_PATH")) == NULL) {
    fprintf(stderr, "No TC_MT_FOLDER_DB_PATH environment variable.\n");
    fprintf(stderr, "Use default db_path = %s\n", DEFAULT_DB_PATH);
    dbpath = DEFAULT_DB_PATH;
  } else {
    fprintf(stderr, "User defined dbpath = %s\n", dbpath_env);
    dbpath = dbpath_env;
  }
}

void *thread_work(void *data) {
#define BUF_SIZE DEFAULT_BUF_SIZE
  char keystr[BUF_SIZE];
  char valstr[BUF_SIZE];
  thread_data *td = (thread_data*)data;

  printf("I am thread %d, bdb = %p\n", td->id, td->bdb);

#ifdef SERIAL_TXN
  pthread_mutex_lock(td->mutex);
#endif

  tcbdbtranbegin(td->bdb);

  int i = 0, index;
  for (i = 1; i <= loop_size; i++) {
    index = (td->id - 1)* loop_size + i;
    memset((char*)keystr, 0, BUF_SIZE);
    memset((char*)valstr, 0, BUF_SIZE);
    sprintf(keystr, "%s%d", "key", index);
    sprintf(valstr, "%s%d", "value", index);

#ifdef TXN_REAL_WORK
    tcbdbput2(td->bdb, keystr, valstr);
#endif
  }

  tcbdbtrancommit(td->bdb);

#ifdef SERIAL_TXN
  pthread_mutex_unlock(td->mutex);
#endif

  pthread_exit(0);
#undef BUF_SIZE
}

int main() {
  pthread_t pt[NUM_THREADS + 1];
  thread_data tdata[NUM_THREADS + 1];
  int i;

  /* db */
  TCBDB *bdb;
  int ecode;
  char *key, *value;

  /* config */
  config();

  bdb = tcbdbnew();

  if (!tcbdbopen(bdb, dbpath, BDBOWRITER | BDBOCREAT)) {
    ecode = tcbdbecode(bdb);
    fprintf(stderr, "db open error: %s\n", tcbdberrmsg(ecode));
  }

#ifdef SERIAL_TXN
  pthread_mutex_t mutex;
  pthread_mutex_init(&mutex, NULL);
#endif

  for (i = 1; i <= NUM_THREADS; i++) {
    tdata[i].id = i;
    tdata[i].bdb = bdb;
#ifdef SERIAL_TXN
    tdata[i].mutex = &mutex;
#endif
    pthread_create(pt + i, NULL, &thread_work, tdata + i);
  }

  for (i = 1; i <= NUM_THREADS; i++) {
    pthread_join(pt[i], NULL);
  }

#ifdef SERIAL_TXN
  pthread_mutex_destroy(&mutex);
#endif

  /* close db */
  if (!tcbdbclose(bdb)) {
    ecode = tcbdbecode(bdb);
    fprintf(stderr, "db close error: %s\n", tcbdberrmsg(ecode));
  }

  /* delete db obj */
  tcbdbdel(bdb);

  return EXIT_SUCCESS;
}
