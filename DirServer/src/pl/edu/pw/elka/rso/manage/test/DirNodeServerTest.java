package pl.edu.pw.elka.rso.manage.test;


import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.screen.NodeScreen;
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
                NodeScreen.addLogEntry("servers: ");
                nodeRegister.getAliveNodes().forEach(NodeScreen::addLogEntry);
            }
            Thread.sleep(2000);
        }


    }

}
