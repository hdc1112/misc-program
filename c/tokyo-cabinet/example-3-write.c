#include <tcutil.h>
#include <tcbdb.h>
#include <stdlib.h>
#include <stdbool.h>
#include <stdint.h>
#include <string.h>

int main(int argc, char **argv){
  TCBDB *bdb;
  BDBCUR *cur;
  int ecode;
  char *key, *value;

  /* create the object */
  bdb = tcbdbnew();

  int loop_size = 9000;
  char *loop_size_env = NULL;

  if ((loop_size_env = getenv("TC_EXAMPLE_3_FOLDER_LOOP_SIZE")) == NULL) {
    fprintf(stderr, "No TC_EXAMPLE_3_FOLDER_LOOP_SIZE environment variable.\n");
    fprintf(stderr, "Use default loop_size = %d\n", loop_size);
  } else {
    loop_size = atoi(loop_size_env);
    fprintf(stderr, "User defined loop_size = %d\n", loop_size);
  }

  char *dbpath = NULL;
  if ((dbpath = getenv("TC_EXAMPLE_3_FOLDER_DB_PATH")) == NULL) {
    fprintf(stderr, "No TC_EXAMPLE_3_FOLDER_DB_PATH environment variable.\n");
    dbpath = "casket.tcb";
    fprintf(stderr, "Use default db_path = %s\n", dbpath);
  } else {
    fprintf(stderr, "User defined dbpath = %s\n", dbpath);
  }


  /* open the database */
  if(!tcbdbopen(bdb, dbpath, BDBOWRITER | BDBOCREAT)){
    ecode = tcbdbecode(bdb);
    fprintf(stderr, "open error: %s\n", tcbdberrmsg(ecode));
  }

#define BUFSIZE 100
  
  char keystr[BUFSIZE], valstr[BUFSIZE];

  tcbdbtranbegin(bdb);

  int i = 0;
  for (i = 1; i <= loop_size; i++) {
    memset((char*)keystr, 0, BUFSIZE);
    memset((char*)valstr, 0, BUFSIZE);
    sprintf(keystr, "%s%d", "key", i);
    sprintf(valstr, "%s%d", "value", i);
    tcbdbput2(bdb, keystr, valstr);
  }

  tcbdbtrancommit(bdb);

  /* traverse records */
  /*cur = tcbdbcurnew(bdb);
  tcbdbcurfirst(cur);
  while((key = tcbdbcurkey2(cur)) != NULL){
    value = tcbdbcurval2(cur);
    if(value){
      printf("%s:%s\n", key, value);
      free(value);
    }
    free(key);
    tcbdbcurnext(cur);
  }
  tcbdbcurdel(cur);*/

  /* close the database */
  if(!tcbdbclose(bdb)){
    ecode = tcbdbecode(bdb);
    fprintf(stderr, "close error: %s\n", tcbdberrmsg(ecode));
  }

  /* delete the object */
  tcbdbdel(bdb);

  return 0;
}
