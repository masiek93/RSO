package pl.edu.pw.elka.rso.manage.server;

import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.manage.util.LongIO;
import pl.edu.pw.elka.rso.manage.util.LongIOException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class listens for new connectio and dispatch new worker threads
 * for server handling.
 */
public class ConnectionListener implements Runnable {

    boolean running;
    ServerSocket serverSocket;
    Node node = new Node();

    String idFilePath;

    public ConnectionListener(String idFilePath, ServerSocket serverSocket) {
        this.serverSocket = serverSocket;

        // connection listener register itself
        if(idFilePath == null) {
            this.idFilePath = "id.txt";
        } else {
            this.idFilePath = idFilePath;
        }
        Long id = null;
        try {
            id = LongIO.readLong(this.idFilePath);
        } catch (LongIOException e) {
            id = IdManager.getInstance().newId();
            try {
                LongIO.writeLong(idFilePath, id);
            } catch (LongIOException e1) {
                e1.printStackTrace();
            }
        }
        node.setId(id);
        node.setNodeType(NodeType.DIRECTORY_NODE);

        NodeRegister.getInstance().registerNode(node);

    }


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
        try {
            System.out.println("server is ready for new connections");
            while (isRunning()) {

                Socket sock = serverSocket.accept();
                new ConnectionHandler(sock).start();
            }
        } catch (IOException e) {
            e.printStackTrace();


        } finally {
            setRunning(false); // in case there was an exception
            if(!serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
