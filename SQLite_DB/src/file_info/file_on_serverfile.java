package file_info;

public class file_on_serverfile{
	private int id;
    private int file_id; //id pliku
    private int server_id; // id serwera plikowego
    private String save_time; // czas modyfikacji
    private String lock_timestamp; // czas modyfikacji
    public file_on_serverfile() {}
    public file_on_serverfile(int id, int file_id, int server_id, String save_time, String lock_timestamp) {
        this.id = id;
        this.file_id=file_id;
        this.server_id = server_id;
        this.save_time = save_time;
        this.lock_timestamp = lock_timestamp;

    }
    
    @Override
    public String toString() {
        return +id+" "+file_id+" "+server_id+" "+save_time+" "+lock_timestamp;
    }
 

}
