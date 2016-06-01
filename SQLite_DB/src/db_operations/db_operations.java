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
 
import file_info.file_info_storager;
import file_info.file_on_serverfile;
import file_info.file_server;

public class db_operations {
	 
    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String DB_URL = "jdbc:sqlite:test2.db";
 
    private Connection conn;
    private Statement stat;
 
    public db_operations() {
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
        String createFile_info_storager = "CREATE TABLE IF NOT EXISTS file_info_storager (file_id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(255),size DOUBLE, creation_time DATETIME)";
        String createFile_on_serverfile = "CREATE TABLE IF NOT EXISTS file_on_serverfile (id INTEGER PRIMARY KEY AUTOINCREMENT, file_id INTEGER, server_id INTEGER, save_time DATETIME, lock_timestamp DATETIME)";
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
 
    public boolean insertintoFile_info_storager(String name, double size, String creation_time) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into file_info_storager values (NULL, ?, ?, ?);"); 
            prepStmt.setString(1, name);
            prepStmt.setDouble(2, size);
            prepStmt.setString(3, creation_time);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Error on insert into table file_info_storager");
            e.printStackTrace();
            return false;
        }
        return true;
    }
 
    public boolean insertintoFile_on_serverfile(int file_id, int server_id, String save_time, String lock_timestamp) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into file_on_serverfile values (NULL,?, ?, ?, ?);");
            prepStmt.setInt(1, file_id);
            prepStmt.setInt(2, server_id);
            prepStmt.setString(3, save_time);
            prepStmt.setString(4, lock_timestamp);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Error on insert into table file_on_serverfile");
            return false;
        }
        return true;
    }
    
    public boolean insertFile_to_db(String name, double size, String creation_time, String save_time, int server_id, String lock_timestamp) {
        	if(insertintoFile_info_storager(name, size, creation_time)){
        		try {
        			ResultSet result = stat.executeQuery("SELECT last_insert_rowid() FROM file_info_storager");
        			int file_id;
        			result.next();
        			file_id = result.getInt("last_insert_rowid()");
        			insertintoFile_on_serverfile(file_id, server_id, save_time, lock_timestamp);
        			
        		} catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
        	}
        	else
        		return false;
        	
        return true;
    }
    
    public boolean removeFile_from_db(String path) {
    		try {
    			PreparedStatement prepStmt = conn.prepareStatement(
                        "SELECT id FROM file_on_server WHERE path=?");
    			prepStmt.setString(1, path);
    			ResultSet result = prepStmt.executeQuery();
    			int id;
    			result.next();
    			id = result.getInt("id");
    			prepStmt = conn.prepareStatement(
                        "DELETE FROM file_on_server WHERE path=?");
    			prepStmt.setString(1, path);
    			prepStmt.execute();
    			prepStmt = conn.prepareStatement(
                        "DELETE FROM file_info_storager WHERE id=?");
    			prepStmt.setInt(1, id);
    			prepStmt.execute();  			
    		} catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

    	
    return true;
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
