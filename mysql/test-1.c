//example file hellow/main.c
#include <stdio.h>
#include <mysql.h> // functions from libmysqlclient
int main(int argc, char *argv[])
{
  int i;
  MYSQL *conn; // connection
  MYSQL_RES *result; // result of the SELECT query
  MYSQL_ROW row; // a record of the SELECT query
  // create connection to MySQL
  conn = mysql_init(NULL);
  if(mysql_real_connect(
        conn, "localhost", "root", "XXX",
        "mylibrary", 0, NULL, 0) == NULL) {
    fprintf(stderr, "sorry, no database connection ...\n");
    return 1;
  }
  // only if Unicode output (utf8) is desired
  mysql_query(conn, "SET NAMES 'utf8'");
  // create list of all pulishers and number of titles published for each pulisher
  const char *sql="SELECT COUNT(titleID), publName \
                   FROM publishers, titles \
                   WHERE publishers.publID = titles.publID \
                   GROUP BY publishers.publID \
                   ORDER BY publName";
  if(mysql_query(conn, sql)) {
    fprintf(stderr, "%s\n", mysql_error(conn));
    fprintf(stderr, "%s\n", sql);
    return 1;
  }
  // process result
  result = mysql_store_result(conn);
  if(result==NULL) {
    if(mysql_error(conn))
      fprintf(stderr, "%s\n", mysql_error(conn));
    else
      fprintf(stderr, "%s\n", "unknown error\n");
    return 1;
  }
  printf("%i records found \n", (int)mysql_num_rows(result));
  // loop over all records
  while((row = mysql_fetch_row(result)) != NULL) {
    for(i=0; i < mysql_num_fields(result); i++) {
      if(row[i] == NULL)
        printf("[NULL]\t");
      else
        printf("%s\t", row[i]);
    }
    printf("\n");
  }
  // release memory, sever connection
  mysql_free_result(result);
  mysql_close(conn);
  return 0;
}
