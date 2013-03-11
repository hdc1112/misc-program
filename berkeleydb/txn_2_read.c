#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <db.h>

#define BUF_SIZE (100)
#define LOOP_SIZE (90000)

int main(void)
{
  int ret, ret_c;
  u_int32_t db_flags, env_flags;
  DB *dbp;
  DB_ENV *envp;
  DBT key, data;
  DB_TXN *txn;
  const char *db_home_dir = "/tmp/txn_2_env/";
  const char *file_name = "mydb.db";
  const char keystr[BUF_SIZE];
  const char datastr[BUF_SIZE];
  int i = 0;

  dbp = NULL;
  envp = NULL;

  /* Open the environment */
  ret = db_env_create(&envp, 0);
  if (ret != 0) {
    fprintf(stderr, "Error creating environment handle: %s\n",
        db_strerror(ret));
    return (EXIT_FAILURE);
  }

  env_flags = DB_CREATE | /* Create the environment if it does
                           * not already exist. */
    DB_INIT_TXN | /* Initialize transactions */
    DB_INIT_LOCK | /* Initialize locking. */
    DB_INIT_LOG | /* Initialize logging */
    DB_INIT_MPOOL | /* Initialize the in-memory cache. */
    DB_RECOVER;
  ret = envp->open(envp, db_home_dir, env_flags, 0);
  if (ret != 0) {
    fprintf(stderr, "Error opening environment: %s\n",
        db_strerror(ret));
    goto err;
  }

  /* Initialize the DB handle */
  ret = db_create(&dbp, envp, 0);
  if (ret != 0) {
    envp->err(envp, ret, "Database creation failed");
    goto err;
  }

  db_flags = DB_CREATE | DB_AUTO_COMMIT;
  /*
     Open the database. Note that we are using auto commit for the open,
     so the database is able to support transactions.
     */
  ret = dbp->open(dbp, /* Pointer to the database */
      NULL, /* Txn pointer */
      file_name, /* File name */
      NULL, /* Logical db name */
      DB_BTREE, /* Database type (using btree) */
      db_flags, /* Open flags */
      0); /* File mode. Using defaults */
  if (ret != 0) {
    envp->err(envp, ret, "Database '%s' open failed",
        file_name);
    goto err;
  }

  /* Get the txn handle */
  txn = NULL;
  ret = envp->txn_begin(envp, NULL, &txn, 0);
  if (ret != 0) {
    envp->err(envp, ret, "Transaction begin failed.");
    goto err;
  }

  DBC *cursorp;
  dbp->cursor(dbp, NULL, &cursorp, 0);

  for (i = 0; i < LOOP_SIZE; i++) {
    /* Prepare the DBTs */
    memset(&key, 0, sizeof(DBT));
    memset(&data, 0, sizeof(DBT));

    //sprintf((char*)keystr, "key%d", i);
    //sprintf((char*)datastr, "data%d", i);
  
    /*key.data = (char*)keystr;
    key.size = strlen((char*)keystr) + 1;*/
    //data.data = (char*)datastr;
    //data.size = strlen((char*)datastr) + 1;
  
    /* Perform the database write. If this fails, abort the transaction. */

    /*
    ret = dbp->get(dbp, txn, &key, &data, 0);
    if (ret != 0) {
      envp->err(envp, ret, "Database get failed.");
      txn->abort(txn);
      goto err;
    }*/

    /* Iterate over the database, retrieving each record in turn. */
    while ((ret = cursorp->get(cursorp, &key, &data, DB_NEXT)) == 0) {
      /* Do interesting things with the DBTs here. */
      printf("%s %s\n", key.data, data.data);
    }
    if (ret != DB_NOTFOUND) {
      /* Error handling goes here */
      fprintf(stderr, "error db-notfound\n");
      goto err;
    }
  }

  /* Cursors must be closed */
  if (cursorp != NULL)
    cursorp->close(cursorp);

  /* Commit the transaction. Note that the transaction handle
     can no longer be used. */
  ret = txn->commit(txn, 0);
  if (ret != 0) {
    envp->err(envp, ret, "Transaction commit failed.");
    goto err;
  }

err:
  /* Close the database */
  if (dbp != NULL) {
    ret_c = dbp->close(dbp, 0);
    if (ret_c != 0) {
      envp->err(envp, ret_c, "Database close failed.");
      ret = ret_c;
    }
  }

  /* Close the environment */
  if (envp != NULL) {
    ret_c = envp->close(envp, 0);
    if (ret_c != 0) {
      fprintf(stderr, "environment close failed: %s\n",
          db_strerror(ret_c));
      ret = ret_c;
    }
  }

  return (ret == 0 ? EXIT_SUCCESS : EXIT_FAILURE);
}
