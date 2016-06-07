package pl.edu.pw.elka.rso.message.data;

import java.io.Serializable;

/**
 * File Server Registration Request
 */
public class FileSrvRegReq implements Serializable {
    private final int socketPort;
    private final int fileSocketPort;

    public FileSrvRegReq(int socketPort, int fileSocketPort) {
        this.socketPort = socketPort;
        this.fileSocketPort = fileSocketPort;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public int getFileSocketPort() {
        return fileSocketPort;
    }
}
