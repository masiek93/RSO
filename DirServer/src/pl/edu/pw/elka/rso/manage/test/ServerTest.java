package pl.edu.pw.elka.rso.manage.test;


import pl.edu.pw.elka.rso.manage.server.ConnectionListener;

public class ServerTest {

    public static void main(String[] args) {
        ConnectionListener conList = new ConnectionListener();
        conList.start();
    }

}
