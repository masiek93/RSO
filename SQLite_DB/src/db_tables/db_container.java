package db_tables;
import java.util.List;

import db_tables.file_info_storager;
import db_tables.file_on_serverfile;
import db_tables.file_server;
public class db_container {
	public List<file_info_storager> table1;
	public List<file_on_serverfile> table2;
	public List<file_server> table3;
	
    public db_container() {}
    public db_container(List<file_info_storager> table1, List<file_on_serverfile> table2, List<file_server> table3) {
        this.table1 = table1;
        this.table2= table2;
        this.table3 = table3;

    }
}
