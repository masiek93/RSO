package pl.edu.pw.elka.rso.repo.db;

import java.io.Serializable;

public class FileInfoStorager implements Serializable{
    private int file_id; //id pliku
    private String name; 
    private double size; // rozmiar pliku
    private String creation_time; // czas utworzenia
    
 
    public FileInfoStorager() {}
    public FileInfoStorager(int file_id, String name, double size, String creation_time) {
        this.file_id = file_id;
        this.name = name;
        this.size = size;        
        this.creation_time = creation_time;
    }
    
    
    public int get_file_id(){
        return this.file_id;
    }
    
    public double get_size(){
    	return this.size;
    }
    
    public String get_name(){
    	return this.name;
    }
    public String get_creation_time(){
    	return this.creation_time;
    }
    
    @Override
    public String toString() {
        return +file_id+" "+name+" "+size+" "+creation_time;
    }

}
