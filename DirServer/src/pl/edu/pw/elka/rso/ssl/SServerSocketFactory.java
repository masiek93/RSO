package pl.edu.pw.elka.rso.ssl;


import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Secure server socket factory.
 */
public class SServerSocketFactory {

    private static boolean configRead;

    static {
        configRead = false;
    }

    private static void readConfig() {

        // TODO: sciezka do certyfikatu powinna byc zapisana w pliku konfiguracyjnym
        System.setProperty("javax.net.ssl.keyStore", "serverkeystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "q1w2e3r4t5");

        System.setProperty("javax.net.debug", "true");

    }

    public static synchronized ServerSocket createServerSocket(int port) throws IOException {
        // TODO: stub it temporarily

        return new ServerSocket(port);
//
//        if (!configRead) {
//            configRead = true;
//            readConfig();
//        }
//
//        SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
//        SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
//
//        return serverSocket;
    }

    public static synchronized SSLServerSocket createServerSocket(int port, int backlog) throws IOException {

        if (!configRead) {
            configRead = true;
            readConfig();
        }

        SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port, backlog);

        return serverSocket;
    }

}
