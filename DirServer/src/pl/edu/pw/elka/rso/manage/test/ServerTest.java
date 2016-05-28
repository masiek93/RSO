package pl.edu.pw.elka.rso.manage.test;


import pl.edu.pw.elka.rso.manage.events.EventBroadcaster;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.server.ConnectionListener;
import pl.edu.pw.elka.rso.ssl.SServerSocketFactory;

import java.io.IOException;

public class ServerTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        EventBroadcaster eventBroadcaster = EventBroadcaster.getInstance();

        ConnectionListener conList = new ConnectionListener(args[0], SServerSocketFactory.createServerSocket(Config.port));
        conList.start();

        while(true) {
            NodeRegister nodeRegister = NodeRegister.getInstance();
            if(nodeRegister != null) {
                System.out.println("servers: ");
                nodeRegister.getAliveNodes().forEach(System.out::println);
            }
            Thread.sleep(2000);
        }


    }

}
