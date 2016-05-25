package pl.edu.pw.elka.rso.manage.test;


import pl.edu.pw.elka.rso.manage.client.ClientListener;
import pl.edu.pw.elka.rso.manage.server.Config;

import java.io.IOException;
import java.net.Socket;

public class ClientTest {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(Config.serverAddress, Config.port);
        ClientListener cli = new ClientListener(socket);
        cli.start();
    }
}
