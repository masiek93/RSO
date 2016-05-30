package pl.edu.pw.elka.rso.manage.test;


import pl.edu.pw.elka.rso.manage.client.ClientListener;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;

public class DirNodeClientTest {
    public static void main(String[] args) throws InterruptedException {

        ClientListener cli = ClientListener.DirectoryServerListener(args[0]);
        cli.start();


        if(!cli.isRunning()) {

        } else {

        }

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
