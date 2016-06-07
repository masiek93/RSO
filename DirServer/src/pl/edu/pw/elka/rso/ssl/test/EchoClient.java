package pl.edu.pw.elka.rso.ssl.test;


import pl.edu.pw.elka.rso.ssl.SSocketFactory;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;

public class EchoClient {


    public static void main(String[] args) {

        String[] lines = new String[]{"helo", "system", "quit"};
        Socket s = null;

        try {
            s = SSocketFactory.createSocket("localhost", Config.PORT);

            if (s == null) {
                System.out.println("could not establish server");
                return;
            }

            BufferedReader bR = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter pW = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);

            for (String line : lines) {
                pW.println(line);
                String j = bR.readLine();
                System.out.println("from server: " + j);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}