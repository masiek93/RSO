package file_info;

public class file_on_server{
    private int id; //id pliku
    private int server_id; // id serwera plikowego
    private String path;// pelna sciezka do pliku na serw. plikowym
    public file_on_server() {}
    public file_on_server(int id, int server_id, String path) {
        this.id = id;
        this.server_id = server_id;
        this.path = path;

    }
    
    @Override
    public String toString() {
        return +id+" "+server_id+" "+path;
    }
 

}
