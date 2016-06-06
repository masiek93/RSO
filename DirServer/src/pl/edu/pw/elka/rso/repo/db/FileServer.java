package pl.edu.pw.elka.rso.repo.db;

import java.io.Serializable;

public class FileServer implements Serializable {
    private int server_id;
    private String ip_address;
    private double server_size;
    
    public FileServer() {}
    public FileServer(int server_id, String ip_address, double server_size) {
        this.server_id = server_id;
        this.ip_address=ip_address;
        this.server_size = server_size;

    }
    
    
    public int get_server_id(){
    	return this.server_id;
    }
    
    public String get_ip_address(){
    	return this.ip_address;
    }
    public double get_server_size(){
    	return this.server_size;
    }
    
    @Override
    public String toString() {
        return +server_id+" "+ip_address+" "+server_size;
    }

}