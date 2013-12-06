#include "db_cxx.h"

#include <iostream>
#include <cstdlib>

int main(void) {
  u_int32_t env_flags = DB_CREATE     |
                        DB_INIT_LOCK  |
                        DB_INIT_LOG   |
                        DB_INIT_MPOOL |
                        DB_INIT_TXN;

  u_int32_t db_flags = DB_CREATE | DB_AUTO_COMMIT;
  Db *dbp = NULL;
  const char *file_name = "mydb.db";

  std::string envHome("/tmp/testEnv");
  DbEnv myEnv(0);

  try {
    myEnv.open(envHome.c_str(), env_flags, 0);

    dbp = new Db(&myEnv, 0);
    dbp->open(NULL,
              file_name,
              NULL,
              DB_BTREE,
              db_flags,
              0);
  } catch(DbException &e) {
    std::cerr << "Error opening database environment: "
          << envHome << std::endl;
    std::cerr << e.what() << std::endl;
    return (EXIT_FAILURE);
  }

  try {
    dbp->close(0);
    myEnv.close(0);
  } catch(DbException &e) {
    std::cerr << "Error closing database environment: "
      << envHome << std::endl;
    std::cerr << e.what() << std::endl;
    return (EXIT_FAILURE);
  }

  return (EXIT_SUCCESS);
}
