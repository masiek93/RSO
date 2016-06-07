package pl.edu.pw.elka.rso.ssl;


import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;


public class SSocketFactory {

    private static boolean configRead;

    static {
        configRead = false;
    }

    private static void readConfig() {

        // TODO: sciezka do certyfikatu powinna byc zapisana w pliku konfiguracyjnym
        System.setProperty("javax.net.ssl.trustStore", "clientkeystore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        System.setProperty("javax.net.debug", "true");

    }

    public static synchronized SSLSocket createSocket(String host, int port) throws IOException {
        if (!configRead) {
            readConfig();
            configRead = true;
        }

        SSLSocket sslSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port);
        return sslSocket;
    }

    public static synchronized SSLSocket createSocket(InetAddress hostAddress, int port) throws IOException {
        if (!configRead) {
            readConfig();
            configRead = true;
        }

        SSLSocket sslSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(hostAddress, port);
        return sslSocket;
    }


}
