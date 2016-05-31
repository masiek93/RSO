package pl.edu.pw.elka.rso.manage.client;

import pl.edu.pw.elka.rso.manage.events.Event;
import pl.edu.pw.elka.rso.manage.events.EventType;
import pl.edu.pw.elka.rso.manage.events.Handler;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.ssl.SSocketFactory;

import java.io.IOException;

public class DirNodeListener extends ClientListener {

    public static final int MAX_NRET = 2; // number of retries
    public static final long WAIT_PERIOD = 1000; // period between retries


    public DirNodeListener(String idFilePath, int port) {
        super(idFilePath, NodeType.DIRECTORY_NODE);
        thisNode.setPort(port);
    }

    @Override
    public void run() {


        setTrying(true);
        // if the main directory server fails, we should check if there are servers with id < ours id

        int nret = 0;

        while(nret < MAX_NRET) {
            try {
                pickServer();
                runner();
                Thread.sleep(WAIT_PERIOD);
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
                System.out.println("syncing with master dir node");
                System.out.println("got " + event.getData() + " " + " from server");
            }
        });
    }



    protected void pickServer() throws InterruptedException, IOException {
        for(Node node: nodeRegister.getDirectoryNodes()) {
            try {
                if(thisNode.equals(node))
                    continue;
                socket = SSocketFactory.createSocket(node.getAddress(), node.getPort());
                System.out.println("connected to " + node);
                otherNode = node;
                return;
            } catch (IOException e) {

            }
        }
        throw new IOException("Cannot establish connection with any server");
    }




}
