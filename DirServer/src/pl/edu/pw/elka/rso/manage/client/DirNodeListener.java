package pl.edu.pw.elka.rso.manage.client;

import pl.edu.pw.elka.rso.manage.events.Event;
import pl.edu.pw.elka.rso.manage.events.EventType;
import pl.edu.pw.elka.rso.manage.events.Handler;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.manage.screen.NodeScreen;
import pl.edu.pw.elka.rso.ssl.SSocketFactory;

import java.io.IOException;

public class DirNodeListener extends ClientListener {

    public static final int MAX_NRET = 2; // number of retries
    public static final long WAITING_PERIOD_MS = 1000; // period between retries


    public DirNodeListener(String idFilePath, int port) {
        super(idFilePath, NodeType.DIRECTORY_NODE);
        thisNode.setPort(port);
    }

    @Override
    public void run() {


        setTrying(true);


        int nret = 0;

        while(nret < MAX_NRET) {
            try {
                pickServer();
                runner();
                Thread.sleep(WAITING_PERIOD_MS);

                nret = 0;

            } catch (InterruptedException | IOException e) {
                nret++;
            } finally {
                if(socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        setTrying(false);
    }

    @Override
    protected void setUpSpecificHandlers() {
        handlers.put(EventType.DIR_NODE_SYNCHRO, new Handler() {
            @Override
            public void handleEvent(Event event) {
                // dummy handler for synchronization
                NodeScreen.addLogEntry("syncing with the master dir node");
                NodeScreen.addLogEntry("got " + event.getData() + " " + " from server");
            }
        });
    }



    protected void pickServer() throws InterruptedException, IOException {
        for(Node node: nodeRegister.getDirectoryNodes()) {
            try {
                // dont connect to its own server
                if(thisNode.equals(node))
                    continue;
                socket = SSocketFactory.createSocket(node.getAddress(), node.getPort());
                NodeScreen.addLogEntry("connected to " + node);
                otherNode = node;
                return;
            } catch (IOException e) {

            }
        }
        throw new IOException("Cannot establish connection with any server");
    }




}
