/**
 * 
 */
/**
 * @author wyq
 *
 */
package test;

import java.util.List;
import java.util.LinkedList;

import db_operations.db_operations;
import db_tables.file_info_storager;
import db_tables.file_on_serverfile;
import db_tables.file_server;
import db_tables.db_container;
 
public class JdbcTest {
 
    public static void main(String[] args) {
    	db_operations db = new db_operations("jdbc:sqlite:test8.db");
    	//przyklad dodawania pliku na liste serwerow
    	 for(int i=1; i<9; i++){
    		 List<Integer> server_ids=new LinkedList<Integer>();
             String name;
             name="/xyzc"+i;
             server_ids.add(new Integer(i%5));
             server_ids.add(new Integer((i+1)%5));
             server_ids.add(new Integer((i+2)%5));
             db.insertFile_to_db(name, 160.15, server_ids);
        }
    	 
    	 //przyklad zakladania blokady pliku o id 22 na serwerze plikowym o id 1
    	 db.Lock_file(22, 1);
    	 
    	//przyklad modyfikacji pliku(zmianie jego czasu ostatniego zapisu) o id 22 na serwerze plikowym o id 2
    	 db.modify_file_save_time(22, 2);
    	 
    	//przyklad procedury modyfikacji pliku o id 22 na serwerze plikowym o id 2
    	 db.Lock_file(22, 2);
    	 db.modify_file_save_time(22, 2);
    	 db.Unlock_file(22, 2);
    	
    	
    	 
    	 
    	 //przyklad dodawania serwera do BD, id_servera autoinkrementowane
    	db.insertintoFile_server("192.168.0.2", 1000);
    	
    	//przyklad aktualizacji dostepnego miejsca na serwerze plikowym o id 1
    	db.UpdateFile_server_size(1,500);
    	
    	//przyklad usuwania z bazy danych pliku o podanej nazwie
        db.removeFile_from_db("/xyzc7");
        
        //jesli chcemy tez dostac liste serwerow, ktore mamy poinformowac o usunieciu to zrobic tak
        List<Integer> list_of_servers_to_del_file=db.removeFile_from_db("/xyzc8");
        System.out.println("List of servers to delete file: ");
        for(Integer s: list_of_servers_to_del_file)
            System.out.println(s);
        

        //przyklad generowania sciezki plikow dla klienta
        List<String> path_list=db.generate_path_list();
        System.out.println("List of paths: ");
        for(String s: path_list)
            System.out.println(s);
        
        //przyklad pobierania id_pliku z bazy na podstawie nazwy
        int file_id;
        file_id=db.get_file_id_from_filename("/xyzc3");
        System.out.println(file_id);
        //jak nie ma takiej nazwy pliku w bazie to zwraca 0
        file_id=db.get_file_id_from_filename("/abc");
        System.out.println(file_id);
        
        //przyklad robienia backupu bazy
        db_container backup_db=new db_container(db.selectFile_info_storager(), db.selectFile_on_serverfile(), db.selectFile_server());
        System.out.println("List of files: ");
        for(file_info_storager c: backup_db.table1)
            System.out.println(c);
 
        System.out.println("List of files on servers:");
        for(file_on_serverfile k: backup_db.table2)
            System.out.println(k);
        
        System.out.println("List of servers:");
        for(file_server k: backup_db.table3)
            System.out.println(k);
        
        //wgranie backupu do nowej bazy
        //TODO jakis mechanizm generowania nazwy bazy i przelaczania na nowa
        db_operations db2 = new db_operations("jdbc:sqlite:test10.db");
        db2.fill_db_from_backup(backup_db);
        
        //wyswietlenie bazy uzyskanej z backupu dla sprawdzenia czy zadziałało
        backup_db=new db_container(db2.selectFile_info_storager(), db2.selectFile_on_serverfile(), db2.selectFile_server());
        System.out.println("List of files: ");
        for(file_info_storager c: backup_db.table1)
            System.out.println(c);
 
        System.out.println("List of files on servers:");
        for(file_on_serverfile k: backup_db.table2)
            System.out.println(k);
        
        System.out.println("List of servers:");
        for(file_server k: backup_db.table3)
            System.out.println(k);
 
        db.closeConnection();
        db2.closeConnection();
    }
}