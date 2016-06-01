package file_info;

public class file_server {
	private int server_id;
    private String ip_address;
    private double server_size;
    
    public file_server() {}
    public file_server(int server_id, String ip_address, double server_size) {
        this.server_id = server_id;
        this.ip_address=ip_address;
        this.server_size = server_size;

    }
    
    @Override
    public String toString() {
        return +server_id+" "+ip_address+" "+server_size;
    }

}