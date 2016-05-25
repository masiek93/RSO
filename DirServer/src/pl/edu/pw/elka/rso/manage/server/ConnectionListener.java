package pl.edu.pw.elka.rso.manage.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class listens for new connectio and dispatch new worker threads
 * for server handling.
 */
public class ConnectionListener implements Runnable {

boolean running;


    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void start() {
        setRunning(true);
        new Thread(this).start();
    }

    @Override
    public void run() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(Config.port);

            while (isRunning()) {

                Socket sock = socket.accept();
                new ConnectionHandler(sock).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setRunning(false); // in case there was an exception

    }
}
