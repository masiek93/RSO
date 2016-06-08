package pl.edu.pw.elka.rso.dirServer;


import pl.edu.pw.elka.rso.manage.client.ClientListener;
import pl.edu.pw.elka.rso.manage.client.FileNodeListener;
import pl.edu.pw.elka.rso.manage.screen.FileNodeScreen;
import pl.edu.pw.elka.rso.manage.screen.FileNodeScreenDataProvider;

import java.io.IOException;

public class FileNodeTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        ClientListener cli = new FileNodeListener(args[0], 10009, 13);
        FileNodeScreen.start(new FileNodeScreenDataProvider(cli));
        cli.start();

        runForEver(cli);

    }

    private static void runForEver(ClientListener cli) throws InterruptedException {
        while(cli.isTrying()) {
            while(cli.isConnected()) {
                Thread.sleep(2000 * 5);
            }
            Thread.sleep(200);
        }
    }

}
