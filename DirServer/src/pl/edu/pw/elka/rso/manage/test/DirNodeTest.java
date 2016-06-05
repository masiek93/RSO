package pl.edu.pw.elka.rso.manage.test;


import pl.edu.pw.elka.rso.manage.client.ClientListener;
import pl.edu.pw.elka.rso.manage.client.DirNodeListener;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.screen.DirNodeScreen;
import pl.edu.pw.elka.rso.manage.screen.DirNodeScreenDataProvider;
import pl.edu.pw.elka.rso.manage.screen.NodeScreen;
import pl.edu.pw.elka.rso.manage.server.ConnectionListener;
import pl.edu.pw.elka.rso.manage.synchro.DirNodeSynchronizer;

import java.io.IOException;

/**
 * Dir node: check if there is a main dir node. If there isn't any one, run
 * server instance.
 */
public class DirNodeTest {
    public static void main(String[] args) throws InterruptedException, IOException {


        // run dir node client. If it wont connect then run server.

        tryRunClient(args);
        NodeScreen.addLogEntry("dir node  didnt find any dir servers");
        runServer(args);
    }

    private static void runServer(String[] args) throws IOException, InterruptedException {
        ConnectionListener conList = new ConnectionListener(args[0], Integer.valueOf(args[1]));

        NodeScreen.addLogEntry("runnng a dir node server");
        conList.start();

        DirNodeSynchronizer dns = new DirNodeSynchronizer();

        new Thread(dns).start();


        while(conList.isRunning()) {
        //    printAliveNodes();
            dns.replicateData("Hello world");
            Thread.sleep(2000);
        }
    }

    private static void tryRunClient(String[] args) throws InterruptedException {
        ClientListener c = new DirNodeListener(args[0], new Integer(args[1]));
        DirNodeScreen.start(new DirNodeScreenDataProvider(c));
        c.start();

        while (c.isTrying()) {
            while (c.isConnected()) {
         //       printAliveNodes();
                Thread.sleep(1000*5);
            }
        }
    }

    private static void printAliveNodes() throws InterruptedException {
        NodeRegister nodeRegister = NodeRegister.getInstance();
        if (nodeRegister != null) {
            NodeScreen.addLogEntry("nodes: ");
            nodeRegister.getAliveNodes().forEach(NodeScreen::addLogEntry);
        }
    }


}
