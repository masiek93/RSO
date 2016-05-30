package pl.edu.pw.elka.rso.manage.test;


import pl.edu.pw.elka.rso.manage.client.ClientListener;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.ssl.SSocketFactory;

import java.io.IOException;
import java.net.Socket;

public class FileNodeClientTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        ClientListener cli = ClientListener.FileServerListener(args[0]);
        cli.start();

        while(cli.isRunning()) {
            NodeRegister nodeRegister = cli.getNodeRegister();
            if(nodeRegister != null) {
                System.out.println("servers: ");
                nodeRegister.getAliveNodes().forEach(System.out::println);
            }
            Thread.sleep(2000*5);
        }

    }
}
