package pl.edu.pw.elka.rso.manage.test;


import pl.edu.pw.elka.rso.manage.client.ClientListener;
import pl.edu.pw.elka.rso.manage.client.FileNodeListener;
import pl.edu.pw.elka.rso.manage.screen.FileNodeScreen;
import pl.edu.pw.elka.rso.manage.screen.FileNodeScreenDataProvider;

import java.io.IOException;

public class FileNodeTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        ClientListener cli = new FileNodeListener(args[0]);
        FileNodeScreen.start(new FileNodeScreenDataProvider(cli));
        cli.start();

        while(cli.isTrying()) {
            while(cli.isConnected()) {
//                NodeRegister nodeRegister = NodeRegister.getInstance();
//                if (nodeRegister != null) {
//                    System.out.println("servers: ");
//                    nodeRegister.getNodes().forEach(System.out::println);
//                }
                Thread.sleep(2000 * 5);
            }
            Thread.sleep(200);
        }
    }

}
