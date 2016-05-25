package directoryServer.db_operations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import directoryServer.file_info.file_info_storager;
import directoryServer.file_info.file_on_server;

public class db_operations {
	 
    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String DB_URL = "jdbc:sqlite:database.db";
 
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
        String createFile_info_storager = "CREATE TABLE IF NOT EXISTS file_info_storager (id INTEGER PRIMARY KEY AUTOINCREMENT, size DOUBLE, owner varchar(255), creation_time DATETIME, modify_time DATETIME)";
        String createFile_on_server = "CREATE TABLE IF NOT EXISTS file_on_server (id INTEGER, server_id INTEGER, path varchar(255))";
        try {
            stat.execute(createFile_info_storager);
            stat.execute(createFile_on_server);
        } catch (SQLException e) {
            System.err.println("Error on creating table");
            e.printStackTrace();
            return false;
        }
        return true;
    }
 
    public boolean insertFile_info_storager(double size, String owner, String creation_time, String modify_time) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into file_info_storager values (NULL, ?, ?, ?, ?);");
            prepStmt.setDouble(1, size);
            prepStmt.setString(2, owner);
            prepStmt.setString(3, creation_time);
            prepStmt.setString(4, modify_time);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Error on insert into table file_info_storager");
            e.printStackTrace();
            return false;
        }
        return true;
    }
 
    public boolean insertFile_on_server(int id, int server_id, String path) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into file_on_server values (?, ?, ?);");
            prepStmt.setInt(1, id);
            prepStmt.setInt(2, server_id);
            prepStmt.setString(3, path);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Error on insert into table file_on_server");
            return false;
        }
        return true;
    }
    
    public boolean insertFile_to_db(double size, String owner, String creation_time, String modify_time, int server_id, String path) {
        	if(insertFile_info_storager(size, owner, creation_time, modify_time)){
        		try {
        			ResultSet result = stat.executeQuery("SELECT last_insert_rowid() FROM file_info_storager");
        			int id;
        			result.next();
        			id = result.getInt("last_insert_rowid()");
        			insertFile_on_server(id, server_id, path);
        			
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
    
    public List<String> generate_path_list(int server_id) {
        List<String> path_list = new LinkedList<String>();
        try {
        	PreparedStatement prepStmt = conn.prepareStatement(
        			"SELECT path FROM file_on_server WHERE server_id=?");
        	prepStmt.setInt(1, server_id);
			ResultSet result = prepStmt.executeQuery();
            String path;
            while(result.next()) {
                path = result.getString("path");
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
            int id;
            double size;
            String owner, creation_time, modify_time;
            while(result.next()) {
                id = result.getInt("id");
                size = result.getDouble("size");
                owner = result.getString("owner");
                creation_time = result.getString("creation_time");
                modify_time = result.getString("modify_time");
                Result_list.add(new file_info_storager(id, size, owner, creation_time, modify_time));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return Result_list;
    }
 
    public List<file_on_server> selectFile_on_server() {
        List<file_on_server> Result_list = new LinkedList<file_on_server>();
        try {
            ResultSet result = stat.executeQuery("SELECT * FROM file_on_server");
            int id, server_id;
            String path;
            while(result.next()) {
                id = result.getInt("id");
                server_id = result.getInt("server_id");
                path = result.getString("path");
                Result_list.add(new file_on_server(id, server_id, path));
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