/**
 * 
 */
/**
 * @author wyq
 *
 */
package pl.edu.pw.elka.rso.repo.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.util.Config;
import pl.edu.pw.elka.rso.repo.domain.FileDTO;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



public class DBFacade {
	 
    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String DB_PREFIX = "jdbc:sqlite:";

    private String dbPath;
    //public static final String DB_URL = "jdbc:sqlite:test8.db";
 
    private Connection conn;

    static Logger LOGGER = LoggerFactory.getLogger(DBFacade.class);



    private static DBFacade instance;

    public static DBFacade getInstance() {
        if(instance == null) {
            try {
                instance = new DBFacade(Config.getInstance().dbProdPath);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static DBFacade getRedInstance() {
        if(instance == null) {
            try {
                instance = new DBFacade(Config.getInstance().backupDbProdPath);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

     private DBFacade(String dbPath) {
        this.dbPath = dbPath;
        try {
            SQLiteConfig sqLiteConfig = new SQLiteConfig();
            Class.forName(DBFacade.DRIVER);
            conn = DriverManager.getConnection(DB_PREFIX+dbPath);
        } catch (ClassNotFoundException e) {
            LOGGER.error("No JDBC Driver", e);
        } catch (SQLException e) {
            LOGGER.error("Problem with connection opening", e);
        }
        LOGGER.info("Persistent storage is up.");

        createTable();
    }

    public boolean createTable()  {
        String createFile_info_storager = "CREATE TABLE IF NOT EXISTS file_info_storager (file_id INTEGER PRIMARY KEY, name varchar(255) UNIQUE,size DOUBLE, creation_time DATETIME DEFAULT CURRENT_TIMESTAMP)";
        String createFile_on_serverfile = "CREATE TABLE IF NOT EXISTS file_on_serverfile (id INTEGER PRIMARY KEY AUTOINCREMENT, file_id INTEGER, server_id INTEGER, save_time DATETIME DEFAULT CURRENT_TIMESTAMP, lock_timestamp DATETIME DEFAULT NULL)";
        String createFile_server = "CREATE TABLE IF NOT EXISTS file_server (server_id INTEGER PRIMARY KEY, ip_address varchar(255), server_size DOUBLE)";
        try (Statement stat = conn.createStatement()) {

            stat.executeUpdate(createFile_info_storager);
            stat.executeUpdate(createFile_on_serverfile);
            stat.executeUpdate(createFile_server);

        } catch (SQLException e) {
            LOGGER.error("Error on creating table", e);
            return false;
        }
        LOGGER.info("Persistent storage is initialized.");

        return true;
    }



    public static String insertFileInfoStmt(FileDTO ndto) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("insert into file_info_storager(file_id, name, size) values (%d, \'%s\', %d);", ndto.getFileId(), ndto.getFileName(), ndto.getFileSize()));
        for(int i = 0; i < ndto.getNodes().size(); ++i) {
            Node nod = ndto.getNode(i);
            stringBuilder.append(String.format("insert into file_on_serverfile(file_id, server_id) values (%d, %d);", ndto.getFileId(), nod.getId()));
        }

        return stringBuilder.toString();
    }

    public static String insertFileServerStmt(Node node) {
        return String.format("insert into file_server(server_id, ip_address, server_size) values (%d, \'%s\', %d);", node.getId(), node.getAddress(), node.getSize());
    }

    public static String removeFileStmt(long fileId) {
        return String.format("delete from file_info_storager where file_id=%d; delete from file_on_serverfile where file_id = %d;", fileId, fileId);
    }


    public static String updateNodeSizeStmt(Node node) {
        return String.format("update file_server set server_size = %d where server_id = %d;", node.getSize(), node.getId());
    }


    public static String addFileToFileServerStmt(long fileId, Long id) {
        return String.format("insert into file_on_serverfile(file_id, server_id) values(%d, %d);", fileId, id);
    }


    public static String lockFileStmt(long fileId) {
        return String.format("update file_on_serverfile set lock_timestamp=datetime() where file_id = %d;", fileId);
    }

    public static String unlockFileStmt(long fileId) {
        return String.format("update file_on_serverfile set lock_timestamp=NULL where file_id = %d;", fileId);
    }



    public void executeSqlStmt(String stmt) {

        try {

            if(conn.isClosed()) {
                LOGGER.warn("connection is going to be renewed");
                conn = DriverManager.getConnection(DB_PREFIX + dbPath);
            }

            LOGGER.info("executing statement: " + stmt);
            conn.setAutoCommit(false);
            try (Statement exec = conn.createStatement()) {
                String [] updates = stmt.split(";");
                for(String update: updates) {
                        exec.addBatch(update);
                }
                //
                //conn.setAutoCommit(false);
                exec.executeBatch();
                //conn.setAutoCommit(true);
                conn.commit();
            } catch (SQLException e) {
                LOGGER.error("Error occured while executing statement: "+stmt, e);
            }

        //    conn.commit();
            LOGGER.info("successfully commited statement to database");


        } catch (SQLException e) {
            LOGGER.error("connection lost. Cannot reconnect", e);
        }



    }


    public boolean fillDbFromBackup(DbContainer backup_db){
    	try {

            // batching stuff
            PreparedStatement insertFileInfoStorage = conn.prepareStatement(
                    "insert into file_info_storager(file_id,name,size,creation_time) values (?, ?, ?, ?);");

            PreparedStatement insertFileInfoOnServerFile = conn.prepareStatement(
                    "insert into file_on_serverfile(id,file_id,server_id,save_time,lock_timestamp) values (?, ?, ?, ?, ?);");

            PreparedStatement insertFileServer = conn.prepareStatement(
                    "insert into file_server(server_id,ip_address, server_size) values (?, ?, ?);");

            conn.setAutoCommit(false);


            for(FileInfoStorager f: backup_db.fileInfoStoragerTable){


                insertFileInfoStorage.setInt(1, f.get_file_id());
                insertFileInfoStorage.setString(2, f.get_name());
                insertFileInfoStorage.setDouble(3, f.get_size());
                insertFileInfoStorage.setString(4, f.get_creation_time());
                insertFileInfoStorage.addBatch();

    		}
    		for(FileOnFileServer f: backup_db.fileOnServerFileTable){

                insertFileInfoOnServerFile.setInt(1, f.get_id());
                insertFileInfoOnServerFile.setInt(2, f.get_file_id());
                insertFileInfoOnServerFile.setInt(3, f.get_server_id());
                insertFileInfoOnServerFile.setString(4, f.get_save_time());
                insertFileInfoOnServerFile.setString(5, f.get_lock_timestamp());
                insertFileInfoOnServerFile.addBatch();

    		}

    		for(FileServer f: backup_db.fileServerTable){

                insertFileServer.setInt(1, f.get_server_id());
                insertFileServer.setString(2, f.get_ip_address());
                insertFileServer.setDouble(3, f.get_server_size());
                insertFileServer.addBatch();
    		}

            insertFileServer.executeBatch();
            insertFileInfoStorage.executeBatch();
            insertFileInfoOnServerFile.executeBatch();
            conn.commit();

            LOGGER.info("database has been successfully filled");
            
        } catch (SQLException e) {
            LOGGER.error("Error on filling db from backup", e);
            return false;
        }
        return true;
    	
    }
//
//    public boolean Lock_file(int file_id, int server_id) {
//        try {
//            PreparedStatement prepStmt = conn.prepareStatement(
//                    "UPDATE file_on_serverfile SET lock_timestamp=CURRENT_TIMESTAMP WHERE file_id=? AND server_id=?;");
//            prepStmt.setInt(1, file_id);
//            prepStmt.setInt(2, server_id);
//            prepStmt.execute();
//        } catch (SQLException e) {
//            System.err.println("Error on trying to lock_file");
//            return false;
//        }
//        return true;
//    }
//
//    public boolean Unlock_file(int file_id, int server_id) {
//        try {
//            PreparedStatement prepStmt = conn.prepareStatement(
//                    "UPDATE file_on_serverfile SET lock_timestamp=NULL WHERE file_id=? AND server_id=?;");
//            prepStmt.setInt(1, file_id);
//            prepStmt.setInt(2, server_id);
//            prepStmt.execute();
//        } catch (SQLException e) {
//            System.err.println("Error on trying to unlock_file");
//            return false;
//        }
//        return true;
//    }
//
//    public boolean modify_file_save_time(int file_id, int server_id) {
//        try {
//            PreparedStatement prepStmt = conn.prepareStatement(
//                    "UPDATE file_on_serverfile SET save_time=CURRENT_TIMESTAMP WHERE file_id=? AND server_id=?;");
//            prepStmt.setInt(1, file_id);
//            prepStmt.setInt(2, server_id);
//            prepStmt.execute();
//        } catch (SQLException e) {
//            System.err.println("Error on trying to modify_file_save_time");
//            return false;
//        }
//        return true;
//    }
//
//    public boolean UpdateFile_server_size(int server_id, double size) {
//        try {
//            PreparedStatement prepStmt = conn.prepareStatement(
//                    "UPDATE file_server SET server_size=? WHERE server_id=?;");
//            prepStmt.setInt(2, server_id);
//            prepStmt.setDouble(1, size);
//            prepStmt.execute();
//        } catch (SQLException e) {
//            System.err.println("Error on update in table file_server");
//            return false;
//        }
//        return true;
//    }
//
//    public List<Integer> removeFile_from_db(String name) {
//    		List<Integer> list_of_servers_to_del_file = new LinkedList<Integer>();
//    		try {
//    			PreparedStatement prepStmt = conn.prepareStatement(
//                        "SELECT file_id FROM file_info_storager WHERE name=?");
//    			prepStmt.setString(1, name);
//    			ResultSet result = prepStmt.executeQuery();
//    			int file_id;
//    			result.next();
//    			file_id = result.getInt("file_id");
//    			prepStmt = conn.prepareStatement(
//                        "SELECT server_id FROM file_on_serverfile WHERE file_id=?");
//    			prepStmt.setInt(1, file_id);
//    			result = prepStmt.executeQuery();
//    			int server_id;
//    			while(result.next()){
//    				server_id = result.getInt("server_id");
//    				list_of_servers_to_del_file.add(new Integer(server_id));
//    			}
//    			prepStmt = conn.prepareStatement(
//                        "DELETE FROM file_on_serverfile WHERE file_id=?");
//    			prepStmt.setInt(1, file_id);
//    			prepStmt.execute();
//    			prepStmt = conn.prepareStatement(
//                        "DELETE FROM file_info_storager WHERE file_id=?");
//    			prepStmt.setInt(1, file_id);
//    			prepStmt.execute();
//
//    		} catch (SQLException e) {
//                e.printStackTrace();
//                return new ArrayList<>();
//            }
//
//
//    		return list_of_servers_to_del_file;
//    }
//
//    public List<String> generate_path_list() {
//        List<String> path_list = new LinkedList<String>();
//        try {
//        	PreparedStatement prepStmt = conn.prepareStatement(
//        			"SELECT name FROM file_info_storager");
//			ResultSet result = prepStmt.executeQuery();
//            String path;
//            while(result.next()) {
//                path = result.getString("name");
//                path_list.add(new String(path));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//        return path_list;
//    }
//
//    public int get_file_id_from_filename(String name){
//    	int file_id;
//    	try {
//    		PreparedStatement prepStmt = conn.prepareStatement(
//                    "SELECT file_id FROM file_info_storager WHERE name=?");
//    		prepStmt.setString(1, name);
//    		ResultSet result = prepStmt.executeQuery();
//
//    		result.next();
//    		file_id = result.getInt("file_id");
//
//    	} catch (SQLException e) {
//            e.printStackTrace();
//            return 0;
//        }
//    	return file_id;
//
//
//    }


    public DbContainer backup() {
        return new DbContainer(selectFileInfoStorager(), selectFileOnServerfile(), selectFileServer());
    }
 
    private List<FileInfoStorager> selectFileInfoStorager() {
        List<FileInfoStorager> Result_list = new LinkedList<FileInfoStorager>();
        try (Statement stat = conn.createStatement()) {
            ResultSet result = stat.executeQuery("SELECT * FROM file_info_storager");
            int file_id;
            double size;
            String name,creation_time;
            while(result.next()) {
                file_id = result.getInt("file_id");
                name = result.getString("name");
                size = result.getDouble("size");                
                creation_time = result.getString("creation_time");
                Result_list.add(new FileInfoStorager(file_id, name, size, creation_time));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return Result_list;
    }
 
    private List<FileOnFileServer> selectFileOnServerfile() {
        List<FileOnFileServer> Result_list = new LinkedList<FileOnFileServer>();
        try (Statement stat = conn.createStatement()) {
            ResultSet result = stat.executeQuery("SELECT * FROM file_on_serverfile");
            int id, file_id, server_id;
            String save_time, lock_timestamp;
            while(result.next()) {
                id = result.getInt("id");
                file_id = result.getInt("file_id");
                server_id = result.getInt("server_id");
                save_time = result.getString("save_time");
                lock_timestamp = result.getString("lock_timestamp");
                Result_list.add(new FileOnFileServer(id, file_id, server_id, save_time, lock_timestamp));
            }
        } catch (SQLException e) {
            LOGGER.error("error while executing select statement ", e);
            return new ArrayList<>();
        }
        return Result_list;
    }
    
    private List<FileServer> selectFileServer() {
        List<FileServer> Result_list = new LinkedList<FileServer>();
        try (Statement stat = conn.createStatement()) {
            ResultSet result = stat.executeQuery("SELECT * FROM file_server");
            int server_id;
            String ip_address;
            double server_size;
            while(result.next()) {
                server_id = result.getInt("server_id");
                ip_address = result.getString("ip_address");
                server_size = result.getDouble("server_size");
                Result_list.add(new FileServer(server_id, ip_address, server_size));
            }
        } catch (SQLException e) {
           LOGGER.error("error while executing select statement ", e);
            return new ArrayList<>();
        }
        return Result_list;
    }
 
    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            LOGGER.error("Problem with closing connection", e);
        }
    }

}