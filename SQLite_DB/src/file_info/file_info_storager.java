/**
 * 
 */
/**
 * @author wyq
 *
 */
package file_info;

public class file_info_storager{
    private int id; //id pliku
    private double size; // rozmiar pliku
    private String owner; // kto utworzyl plik
    private String creation_time; // czas utworzenia
    private String modify_time; // czas modyfikacji
 
    public file_info_storager() {}
    public file_info_storager(int id, double size, String owner, String creation_time, String modify_time ) {
        this.id = id;
        this.size = size;
        this.owner = owner;
        this.creation_time = creation_time;
        this.modify_time = modify_time;
    }
    @Override
    public String toString() {
        return +id+" "+size+" "+owner+" "+creation_time+" "+modify_time;
    }

}

