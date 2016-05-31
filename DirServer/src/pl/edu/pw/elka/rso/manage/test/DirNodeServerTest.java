package pl.edu.pw.elka.rso.manage.test;


import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.server.ConnectionListener;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class DirNodeServerTest {

    public static void main(String[] args) throws IOException, InterruptedException, JAXBException {

        ConnectionListener conList = new ConnectionListener(args[0], Integer.valueOf(args[1]));
        conList.start();


        while(conList.isRunning()) {
            NodeRegister nodeRegister = NodeRegister.getInstance();
            if(nodeRegister != null) {
                System.out.println("servers: ");
                nodeRegister.getAliveNodes().forEach(System.out::println);
            }
            Thread.sleep(2000);
        }


    }

}
