package pl.edu.pw.elka.rso.manage.client;

import pl.edu.pw.elka.rso.manage.events.EventBroadcaster;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.manage.server.ConnectionListener;
import pl.edu.pw.elka.rso.ssl.SSocketFactory;

import java.io.IOException;
import java.util.Random;

public class DirNodeListener extends ClientListener {



    public DirNodeListener(String idFilePath, int port) {
        super(idFilePath, NodeType.DIRECTORY_NODE);
        thisNode.setPort(port);
    }

    @Override
    public void run() {


        setTrying(true);
        // if the main directory server fails, we should check if there are servers with id < ours id
        int nret = 0;
        int period = 1000;
        while(nret < 2) {
            try {
                pickServer();
                runner();
                Thread.sleep(period);
                nret = 0;
            } catch (InterruptedException | IOException e) {
                nret++;
            }
        }

        setTrying(false);
    }

    protected void pickServer() throws InterruptedException, IOException {
        for(Node node: nodeRegister.getDirectoryNodes()) {
            System.out.println("trying to connect with node: " +  node);
            try {
                if(thisNode.equals(node))
                    continue;
                socket = SSocketFactory.createSocket(node.getAddress(), node.getPort());
                System.out.println("connected to " + node);
                return;
            } catch (IOException e) {
                System.out.println("cannot establish connection " + e);
            }
        }
        System.out.println("Could not establish connection");
        throw new IOException("Cannot establish connection with any server");
    }




}
