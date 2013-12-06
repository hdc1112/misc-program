// Open the database
db.open( NULL, db_file_name.c_str(), NULL, DB_BTREE, open_flags, 0 );

int key_content = 4000;
int data_content = 4000;

DWORD start = ::GetTickCount(); // start counter

 while( i <= p_count )
/ {
//     /*sprintf_s( rec_buf, "my_record_%d", i ); 
//         std::string description = rec_buf;*/
//
//
//             Dbt key( &key_content, sizeof(int) );
//                 Dbt data( &data_content, sizeof(int) );
//
//                     db.put( NULL, &key, &data, DB_NOOVERWRITE );
//                     }
//
//                         DWORD end = ::GetTickCount(); // stop counter
//                             DWORD duration = end - start;
//                                 std::cout << "Duration for "  << p_count << " records: " << duration << " ms" << std::endl;
