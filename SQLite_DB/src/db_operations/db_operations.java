/**
 * 
 */
/**
 * @author wyq
 *
 */
package db_operations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import db_tables.file_info_storager;
import db_tables.file_on_serverfile;
import db_tables.file_server;
import db_tables.db_container;

public class db_operations {
	 
    public static final String DRIVER = "org.sqlite.JDBC";
    //public static final String DB_URL = "jdbc:sqlite:test8.db";
 
    private Connection conn;
    private Statement stat;
 
    public db_operations(String DB_URL) {
        try {
            Class.forName(db_operations.DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("No JDBC Driver");
            e.printStackTrace();
        }
 
        try {
            conn = DriverManager.getConnection(DB_URL);
            stat = conn.createStatement();
        } catch (SQLException e) {
            System.err.println("Problem with connection opening");
            e.printStackTrace();
        }
 
        createTable();
    }
 
    public boolean createTable()  {
        String createFile_info_storager = "CREATE TABLE IF NOT EXISTS file_info_storager (file_id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(255) UNIQUE,size DOUBLE, creation_time DATETIME DEFAULT CURRENT_TIMESTAMP)";
        String createFile_on_serverfile = "CREATE TABLE IF NOT EXISTS file_on_serverfile (id INTEGER PRIMARY KEY AUTOINCREMENT, file_id INTEGER, server_id INTEGER, save_time DATETIME DEFAULT CURRENT_TIMESTAMP, lock_timestamp DATETIME DEFAULT NULL)";
        String createFile_server = "CREATE TABLE IF NOT EXISTS file_server (server_id INTEGER PRIMARY KEY AUTOINCREMENT, ip_address varchar(255), server_size DOUBLE)";
        try {
            stat.execute(createFile_info_storager);
            stat.execute(createFile_on_serverfile);
            stat.execute(createFile_server);
        } catch (SQLException e) {
            System.err.println("Error on creating table");
            e.printStackTrace();
            return false;
        }
        return true;
    }
 
    public boolean insertintoFile_info_storager(String name, double size) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into file_info_storager(name,size) values (?, ?);"); 
            prepStmt.setString(1, name);
            prepStmt.setDouble(2, size);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Error on insert into table file_info_storager");
            e.printStackTrace();
            return false;
        }
        return true;
    }
 
    public boolean insertintoFile_on_serverfile(int file_id, int server_id) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into file_on_serverfile(file_id,server_id) values (?, ?);");
            prepStmt.setInt(1, file_id);
            prepStmt.setInt(2, server_id);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Error on insert into table file_on_serverfile");
            return false;
        }
        return true;
    }
    
    public boolean insertintoFile_server(String ip_address, double size) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into file_server values (NULL,?, ?);");
            prepStmt.setString(1, ip_address);
            prepStmt.setDouble(2, size);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Error on insert into table file_server");
            return false;
        }
        return true;
    }
    
    public boolean insertFile_to_db(String name, double size, List <Integer> list_of_server_ids) {
        	if(insertintoFile_info_storager(name, size)){
        		try {
        			ResultSet result = stat.executeQuery("SELECT last_insert_rowid() FROM file_info_storager");
        			int file_id;
        			result.next();
        			file_id = result.getInt("last_insert_rowid()");
        			for(int server_id: list_of_server_ids)
        			insertintoFile_on_serverfile(file_id, server_id);
        			
        		} catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
        	}
        	else
        		return false;
        	
        return true;
    }
    
    public boolean fill_db_from_backup(db_container backup_db){
    	try {
    		for(file_info_storager f: backup_db.table1){
    			PreparedStatement prepStmt = conn.prepareStatement(
                        "insert into file_info_storager(file_id,name,size,creation_time) values (?, ?, ?, ?);");
                prepStmt.setInt(1, f.get_file_id());
                prepStmt.setString(2, f.get_name());
                prepStmt.setDouble(3, f.get_size());
                prepStmt.setString(4, f.get_creation_time());
                prepStmt.execute();
    		}
    		for(file_on_serverfile f: backup_db.table2){
    			PreparedStatement prepStmt = conn.prepareStatement(
                        "insert into file_on_serverfile(id,file_id,server_id,save_time,lock_timestamp) values (?, ?, ?, ?, ?);");
                prepStmt.setInt(1, f.get_id());
                prepStmt.setInt(2, f.get_file_id());
                prepStmt.setInt(3, f.get_server_id());
                prepStmt.setString(4, f.get_save_time());
                prepStmt.setString(5, f.get_lock_timestamp());
                prepStmt.execute();
    		}
    		for(file_server f: backup_db.table3){
    			PreparedStatement prepStmt = conn.prepareStatement(
                        "insert into file_server(server_id,ip_address, server_size) values (?, ?, ?);");
                prepStmt.setInt(1, f.get_server_id());
                prepStmt.setString(2, f.get_ip_address());
                prepStmt.setDouble(3, f.get_server_size());
                prepStmt.execute();
    		}
            
        } catch (SQLException e) {
            System.err.println("Error on filling db from backup");
            return false;
        }
        return true;
    	
    }
    
    public boolean Lock_file(int file_id, int server_id) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "UPDATE file_on_serverfile SET lock_timestamp=CURRENT_TIMESTAMP WHERE file_id=? AND server_id=?;");
            prepStmt.setInt(1, file_id);
            prepStmt.setInt(2, server_id);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Error on trying to lock_file");
            return false;
        }
        return true;
    } 
    
    public boolean Unlock_file(int file_id, int server_id) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "UPDATE file_on_serverfile SET lock_timestamp=NULL WHERE file_id=? AND server_id=?;");
            prepStmt.setInt(1, file_id);
            prepStmt.setInt(2, server_id);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Error on trying to unlock_file");
            return false;
        }
        return true;
    }
    
    public boolean modify_file_save_time(int file_id, int server_id) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "UPDATE file_on_serverfile SET save_time=CURRENT_TIMESTAMP WHERE file_id=? AND server_id=?;");
            prepStmt.setInt(1, file_id);
            prepStmt.setInt(2, server_id);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Error on trying to modify_file_save_time");
            return false;
        }
        return true;
    }
    
    public boolean UpdateFile_server_size(int server_id, double size) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "UPDATE file_server SET server_size=? WHERE server_id=?;");
            prepStmt.setInt(2, server_id);
            prepStmt.setDouble(1, size);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Error on update in table file_server");
            return false;
        }
        return true;
    } 
    
    public List<Integer> removeFile_from_db(String name) {
    		List<Integer> list_of_servers_to_del_file = new LinkedList<Integer>();
    		try {
    			PreparedStatement prepStmt = conn.prepareStatement(
                        "SELECT file_id FROM file_info_storager WHERE name=?");
    			prepStmt.setString(1, name);
    			ResultSet result = prepStmt.executeQuery();
    			int file_id;
    			result.next();
    			file_id = result.getInt("file_id");
    			prepStmt = conn.prepareStatement(
                        "SELECT server_id FROM file_on_serverfile WHERE file_id=?");
    			prepStmt.setInt(1, file_id);
    			result = prepStmt.executeQuery();
    			int server_id;
    			while(result.next()){
    				server_id = result.getInt("server_id");
    				list_of_servers_to_del_file.add(new Integer(server_id));
    			}
    			prepStmt = conn.prepareStatement(
                        "DELETE FROM file_on_serverfile WHERE file_id=?");
    			prepStmt.setInt(1, file_id);
    			prepStmt.execute();
    			prepStmt = conn.prepareStatement(
                        "DELETE FROM file_info_storager WHERE file_id=?");
    			prepStmt.setInt(1, file_id);
    			prepStmt.execute();
    			
    		} catch (SQLException e) {
                e.printStackTrace();
                return null;
            }

    	
    		return list_of_servers_to_del_file;
}
    
    public List<String> generate_path_list() {
        List<String> path_list = new LinkedList<String>();
        try {
        	PreparedStatement prepStmt = conn.prepareStatement(
        			"SELECT name FROM file_info_storager");
			ResultSet result = prepStmt.executeQuery();
            String path;
            while(result.next()) {
                path = result.getString("name");
                path_list.add(new String(path));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return path_list;
}
    public int get_file_id_from_filename(String name){
    	int file_id;
    	try {
    		PreparedStatement prepStmt = conn.prepareStatement(
                    "SELECT file_id FROM file_info_storager WHERE name=?");
    		prepStmt.setString(1, name);
    		ResultSet result = prepStmt.executeQuery();
    		
    		result.next();
    		file_id = result.getInt("file_id");
    		
    	} catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    	return file_id;
    	
    	
    }
 
 
    public List<file_info_storager> selectFile_info_storager() {
        List<file_info_storager> Result_list = new LinkedList<file_info_storager>();
        try {
            ResultSet result = stat.executeQuery("SELECT * FROM file_info_storager");
            int file_id;
            double size;
            String name,creation_time;
            while(result.next()) {
                file_id = result.getInt("file_id");
                name = result.getString("name");
                size = result.getDouble("size");                
                creation_time = result.getString("creation_time");
                Result_list.add(new file_info_storager(file_id, name, size, creation_time));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return Result_list;
    }
 
    public List<file_on_serverfile> selectFile_on_serverfile() {
        List<file_on_serverfile> Result_list = new LinkedList<file_on_serverfile>();
        try {
            ResultSet result = stat.executeQuery("SELECT * FROM file_on_serverfile");
            int id, file_id, server_id;
            String save_time, lock_timestamp;
            while(result.next()) {
                id = result.getInt("id");
                file_id = result.getInt("file_id");
                server_id = result.getInt("server_id");
                save_time = result.getString("save_time");
                lock_timestamp = result.getString("lock_timestamp");
                Result_list.add(new file_on_serverfile(id, file_id, server_id, save_time, lock_timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return Result_list;
    }
    
    public List<file_server> selectFile_server() {
        List<file_server> Result_list = new LinkedList<file_server>();
        try {
            ResultSet result = stat.executeQuery("SELECT * FROM file_server");
            int server_id;
            String ip_address;
            double server_size;
            while(result.next()) {
                server_id = result.getInt("server_id");
                ip_address = result.getString("ip_address");
                server_size = result.getDouble("server_size");
                Result_list.add(new file_server(server_id, ip_address, server_size));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return Result_list;
    }
 
    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Problem with closing connection");
            e.printStackTrace();
        }
    }
}
