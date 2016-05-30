package pl.edu.pw.elka.rso.manage.test;


import pl.edu.pw.elka.rso.manage.client.ClientListener;
import pl.edu.pw.elka.rso.manage.client.DirNodeListener;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.server.ConnectionListener;

import java.io.IOException;

public class DirNodeClientTest {
    public static void main(String[] args) throws InterruptedException, IOException {


        // run dir node client. If it wont connect then run server.

        tryRunClient(args);

        System.out.println("dir node didnt find any servers nodes");


        ConnectionListener conList = new ConnectionListener(args[0], Integer.valueOf(args[1]));
        conList.start();

        System.out.println("runnng server in a new process");

        while(conList.isRunning()) {
            NodeRegister nodeRegister = NodeRegister.getInstance();
            if(nodeRegister != null) {
                System.out.println("servers: ");
                nodeRegister.getAliveNodes().forEach(System.out::println);
            }
            Thread.sleep(2000);
        }


    }

    private static void tryRunClient(String[] args) throws InterruptedException {
        ClientListener c = new DirNodeListener(args[0], new Integer(args[1]));
        c.start();

        while (c.isTrying()) {
            while (c.isRunning()) {
                NodeRegister nodeRegister = c.getNodeRegister();
                if (nodeRegister != null) {
                    System.out.println("servers: ");
                    nodeRegister.getAliveNodes().forEach(System.out::println);
                }
                Thread.sleep(2000);
            }
        }
    }


}
