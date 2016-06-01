/**
 * 
 */
/**
 * @author wyq
 *
 */
package test;

import java.util.List;

import file_info.file_info_storager;
import file_info.file_on_serverfile;
import file_info.file_server;
import db_operations.db_operations;
 
public class JdbcTest {
 
    public static void main(String[] args) {
    	db_operations db = new db_operations();
    	 for(int i=1; i<9; i++){
             String name;
             name="/xyz"+i;
             db.insertFile_to_db(name, 160.15, "2016-05-19 16:00:00", "2016-05-19 16:00:00", i%3, "2016-05-19 16:00:00");
        }
    	
 
        //db.removeFile_from_db("/la");
        List<file_info_storager> file_info_storager_list= db.selectFile_info_storager();
        List<file_on_serverfile> file_on_serverfile_list = db.selectFile_on_serverfile();
        List<file_server> file_server_list = db.selectFile_server();
        
        List<String> path_list=db.generate_path_list();
        
        System.out.println("List of paths: ");
        for(String s: path_list)
            System.out.println(s);
        
 
        System.out.println("List of files: ");
        for(file_info_storager c: file_info_storager_list)
            System.out.println(c);
 
        System.out.println("List of files on servers:");
        for(file_on_serverfile k: file_on_serverfile_list)
            System.out.println(k);
        
        System.out.println("List of servers:");
        for(file_server k: file_server_list)
            System.out.println(k);
 
        db.closeConnection();
    }
}