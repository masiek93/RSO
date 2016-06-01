/**
 * 
 */
/**
 * @author wyq
 *
 */
package file_info;

public class file_info_storager{
    private int file_id; //id pliku
    private String name; 
    private double size; // rozmiar pliku
    private String creation_time; // czas utworzenia
    
 
    public file_info_storager() {}
    public file_info_storager(int file_id, String name, double size,  String creation_time) {
        this.file_id = file_id;
        this.name = name;
        this.size = size;        
        this.creation_time = creation_time;
    }
    @Override
    public String toString() {
        return +file_id+" "+name+" "+size+" "+creation_time;
    }

}

