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
import file_info.file_on_server;
import db_operations.db_operations;
 
public class JdbcTest {
 
    public static void main(String[] args) {
    	db_operations db = new db_operations();
        db.insertFile_to_db(150.15, "Mama", "2016-05-19 16:00:00", "2016-05-19 16:00:00", 3, "/ld");
 
        db.removeFile_from_db("/la");
        List<file_info_storager> file_info_storager_list= db.selectFile_info_storager();
        List<file_on_server> file_on_server_list = db.selectFile_on_server();
        
        List<String> path_list=db.generate_path_list(3);
        
        System.out.println("List of paths: ");
        for(String s: path_list)
            System.out.println(s);
        
 
        System.out.println("List of files: ");
        for(file_info_storager c: file_info_storager_list)
            System.out.println(c);
 
        System.out.println("List of files on servers:");
        for(file_on_server k: file_on_server_list)
            System.out.println(k);
 
        db.closeConnection();
    }
}