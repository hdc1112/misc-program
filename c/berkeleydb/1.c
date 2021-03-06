#include <db.h>

int main() {
  DB *dbp; /* DB structure handle */
  u_int32_t flags; /* database open flags */
  int ret; /* function return value */
  /* Initialize the structure. This
   * database is not opened in an environment,
   * so the environment pointer is NULL. */
  ret = db_create(&dbp, NULL, 0);
  if (ret != 0) {
    /* Error handling goes here */
  }
  /* Database open flags */
  flags = DB_CREATE; /* If the database does not exist,
                      * create it.*/
  /* open the database */
  ret = dbp->open(dbp, /* DB structure pointer */
      NULL, /* Transaction pointer */
      "/tmp/my_db.db", /* On-disk file that holds the database. */
      NULL, /* Optional logical database name */
      DB_BTREE, /* Database access method */
      flags, /* Open flags */
      0); /* File mode (using defaults) */
  if (ret != 0) {
    /* Error handling goes here */
  }

  /* When we're done with the database, close it. */
  if (dbp != NULL)
    dbp->close(dbp, 0);

  return 0;
}
