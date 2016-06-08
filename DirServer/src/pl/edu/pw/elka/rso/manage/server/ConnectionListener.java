package pl.edu.pw.elka.rso.manage.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.manage.screen.NodeScreen;
import pl.edu.pw.elka.rso.util.LongIO;
import pl.edu.pw.elka.rso.util.LongIOException;
import pl.edu.pw.elka.rso.ssl.SServerSocketFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class listens for new connections and dispatch new worker threads
 * for server handling.
 */
public class ConnectionListener implements Runnable {

    boolean running;
    ServerSocket serverSocket;
    Node thisNode = new Node();

    String idFilePath;

    Logger LOGGER = LoggerFactory.getLogger(ConnectionListener.class);

    public ConnectionListener(String idFilePath, int port) throws IOException {


        if(idFilePath == null) {
            this.idFilePath = "id.txt";
        } else {
            this.idFilePath = idFilePath;
        }
        setupNodeConfiguration(port);
        this.serverSocket = SServerSocketFactory.createServerSocket(thisNode.getPort());

    }

    private void setupNodeConfiguration(int port) {
        // connection listener register itself

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
        thisNode.setId(id);
        thisNode.setNodeType(NodeType.DIRECTORY_NODE);
        thisNode.setPort(port);
        thisNode.setAlive(true);
        NodeRegister.getInstance().registerNode(thisNode);
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
            NodeScreen.addLogEntry("server " + thisNode + "  is ready for new connections");

            LOGGER.info("node coordinator service started on port {}", thisNode.getPort());
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

    public Node getThisNode() {
        return thisNode;
    }
}
