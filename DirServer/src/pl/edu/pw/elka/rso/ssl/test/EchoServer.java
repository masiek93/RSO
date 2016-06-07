package pl.edu.pw.elka.rso.ssl.test;


import pl.edu.pw.elka.rso.ssl.SServerSocketFactory;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Dla sprawdzenia czy wrapper na ssl-a dziala prawidlowo.
 */
public class EchoServer {


    static class EchoServerThread implements Runnable {

        Socket client;
        BufferedReader bR;
        PrintWriter pW;


        public EchoServerThread(Socket cli) {

            client = cli;
            try {
                bR = new BufferedReader(new InputStreamReader(client.getInputStream()));
                pW = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        @Override
        public void run() {
            while (true) {

                try {

                    String line = bR.readLine();

                    if (line.equals("helo"))
                        pW.println("ehlo");
                    else if (line.equals("system"))
                        pW.println(System.getenv().toString());

                    if (line.equals("quit"))
                        break;

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        ServerSocket s = null;

        try {

            s = SServerSocketFactory.createServerSocket(Config.PORT);

            System.out.println("system started connected and is waiting for new connections");

            while (true) {
                SSLSocket client = (SSLSocket) s.accept();
                System.out.println(String.format("new server established with client (%s, %d)", client.getInetAddress().getHostAddress(), client.getPort()));
                new Thread(new EchoServerThread(client)).start();
            }

        } catch (IOException e) {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            e.printStackTrace();
        }


    }


}
