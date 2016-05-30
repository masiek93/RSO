//package pl.edu.pw.elka.rso.manage;
//
//import pl.edu.pw.elka.rso.manage.node.Node;
//import pl.edu.pw.elka.rso.manage.util.ConfigIO;
//import pl.edu.pw.elka.rso.manage.util.Config;
//import pl.edu.pw.elka.rso.manage.util.DirectoryServerConf;
//import pl.edu.pw.elka.rso.ssl.SServerSocketFactory;
//import pl.edu.pw.elka.rso.ssl.SSocketFactory;
//
//import javax.net.ssl.SSLServerSocket;
//import javax.net.ssl.SSLSocket;
//import javax.xml.bind.JAXBException;
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//public class Main {
//
//
//    static class Client implements Runnable{
//
//
//        SSLSocket s;
//
//        public synchronized boolean isConnected() {
//
//            return s != null && s.isConnected();
//        }
//
//        @Override
//        public void run() {
//            String[] lines = new String[]{"helo", "system", "quit"};
//            s = null;
//
//            try {
//
//                Thread.sleep(1000);
//                s = SSocketFactory.createSocket("localhost", 1234);
//
//                if (s == null) {
//                    System.out.println("could not establish server");
//                    return;
//                }
//
//                BufferedReader bR = new BufferedReader(new InputStreamReader(s.getInputStream()));
//                PrintWriter pW = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
//
//                for (String line : lines) {
//                    pW.println(line);
//                    String j = bR.readLine();
//                    System.out.println("from server: " + j);
//                }
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    static class EchoServerThread implements Runnable {
//
//        Socket client;
//        BufferedReader bR;
//        PrintWriter pW;
//
//
//        public EchoServerThread(Socket cli) {
//
//            client = cli;
//            try {
//                bR = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                pW = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//
//        @Override
//        public void run() {
//            while (true) {
//
//                try {
//
//                    String line = bR.readLine();
//
//                    if (line.equals("helo"))
//                        pW.println("ehlo");
//                    else if (line.equals("system"))
//                        pW.println(System.getenv().toString());
//
//                    if (line.equals("quit"))
//                        break;
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//            try {
//                client.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    public static void main(String[] args) throws IOException, JAXBException {
//
//
//        Client cl = new Client();
//        new Thread(cl).start();
//
//        if(cl.isConnected()) {
//            System.out.println("it is connected");
//        } else {
//
//            System.out.println("system started running and is waiting for new connections");
//            SSLServerSocket s = SServerSocketFactory.createServerSocket(1234);
//            new Thread(cl).start();
//            while (true) {
//
//                SSLSocket client = (SSLSocket) s.accept();
//                System.out.println(String.format("new server established with client (%s, %d)", client.getInetAddress().getHostAddress(), client.getPort()));
//                new Thread(new EchoServerThread(client)).start();
//            }
//        }
//    }
//
//}
