package pl.edu.pw.elka.rso;

import java.io.Serializable;

public class RegistrationMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1783408076417328979L;
	private int socket_port;
	private int file_socket_port;
	public int getSocket_port() {
		return socket_port;
	}
	public void setSocket_port(int socket_port) {
		this.socket_port = socket_port;
	}
	public int getFile_socket_port() {
		return file_socket_port;
	}
	public void setFile_socket_port(int file_socket_port) {
		this.file_socket_port = file_socket_port;
	}

}
