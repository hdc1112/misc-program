#include <stdio.h>
#include <sqlite3.h>
#include <stdlib.h>

int main(int argc, char *argv[]) {
  int retval;
  int q_cnt = 5, q_size = 150, ind = 0;
  char **queries = malloc(sizeof(char) * q_cnt * q_size);

  sqlite3_stmt *stmt;
  sqlite3 *handle;

  retval = sqlite3_open("sampledb.sqlite3", &handle);
  if (retval) {
    printf("Database connection failed\n");
    return -1;
  }

  printf("Connection successful\n");

  char create_table[100] = "create table if not exists"
    " users (uname text primary key, pass text not null,"
    " activated integer)";

  retval = sqlite3_exec(handle, create_table, 0, 0, 0);

  queries[ind++] = "insert into users values"
    " ('manish', 'manish', 1)";
  retval = sqlite3_exec(handle, queries[ind - 1], 0, 0, 0);

  queries[ind++] = "insert into users values"
    " ('mehul', 'pulsar', 0)";
  retval = sqlite3_exec(handle, queries[ind - 1], 0, 0, 0);

  queries[ind++] = "select * from users";
  retval = sqlite3_prepare_v2(handle, queries[ind - 1], -1, &stmt, 0);
  if (retval) {
    printf("selecting data from db failed\n");
    return -1;
  }

  int cols = sqlite3_column_count(stmt);

  while (1) {
    retval = sqlite3_step(stmt);

    if (retval == SQLITE_ROW) {
      for (int col = 0; col < cols; col++) {
        const char *val = (const char*)sqlite3_column_text(stmt, col);
        printf("%s = %s\t", sqlite3_column_name(stmt, col), val);
      }
      printf("\n");
    } else if (retval == SQLITE_DONE) {
      printf("All rows fetched\n");
      break;
    } else {
      printf("Some error encountered\n");
      return -1;
    }
  }

  sqlite3_close(handle);
  return 0;
}
