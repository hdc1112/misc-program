#include <stdio.h>
#include <stdlib.h>

#include <db.h>

int main(void) {
  int ret, ret_c;
  u_int32_t db_flags, env_flags;
  DB *dbp;
  DB_ENV *envp;
  const char *db_home_dir = "/tmp/myEnvironment";
  const char *file_name = "mydb.db";

  dbp = NULL;
  envp = NULL;

  ret = db_env_create(&envp, 0);
  if (ret != 0) {
    fprintf(stderr, "Error creating environment handle: %s\n",
        db_strerror(ret));
    return (EXIT_FAILURE);
  }

 env_flags = DB_CREATE |
              DB_INIT_TXN |
              DB_INIT_LOCK  |
              DB_INIT_LOG |
              DB_INIT_MPOOL;

  ret = envp->open(envp, db_home_dir, env_flags, 0);
  if (ret != 0) {
    fprintf(stderr, "Error opening environment: %s\n",
        db_strerror(ret));
    goto err;
  }

  ret = envp->set_cachesize(envp, 0, 512 * 1024, 0);
  if (ret != 0) {
    fprintf(stderr, "Error set cachesize: %s\n",
        db_strerror(ret));
    goto err;
  }
 
  ret = db_create(&dbp, envp, 0);
  if (ret != 0) {
    envp->err(envp, ret, "Database creation failed");
    goto err;
  }

  db_flags = DB_CREATE | DB_AUTO_COMMIT;
  ret = dbp->open(dbp,
                  NULL,
                  file_name,
                  NULL,
                  DB_BTREE,
                  db_flags,
                  0);
  if (ret != 0) {
    envp->err(envp, ret, "Database '%s' open failed",
        file_name);
    goto err;
  }

err:
  if (dbp != NULL) {
    ret_c = dbp->close(dbp, 0);
    if (ret_c != 0) {
      envp->err(envp, ret_c, "Database close failed.");
      ret = ret_c;
    }
  }

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
