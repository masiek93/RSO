package pl.edu.pw.elka.rso.repo.db;

import java.io.Serializable;

public class FileOnFileServer implements Serializable {
    private int id;
    private int file_id; //id pliku
    private int server_id; // id serwera plikowego
    private String save_time; // czas modyfikacji
    private String lock_timestamp; // czas blokady
    public FileOnFileServer() {}
    public FileOnFileServer(int id, int file_id, int server_id, String save_time, String lock_timestamp) {
        this.id = id;
        this.file_id=file_id;
        this.server_id = server_id;
        this.save_time = save_time;
        this.lock_timestamp = lock_timestamp;

    }
    public int get_id(){
    	return this.id;
    }
    
    public int get_file_id(){
    	return this.file_id;
    }
    
    public int get_server_id(){
    	return this.server_id;
    }
    
    public String get_save_time(){
    	return this.save_time;
    }
    public String get_lock_timestamp(){
    	return this.lock_timestamp;
    }
    
    @Override
    public String toString() {
        return +id+" "+file_id+" "+server_id+" "+save_time+" "+lock_timestamp;
    }
 

}