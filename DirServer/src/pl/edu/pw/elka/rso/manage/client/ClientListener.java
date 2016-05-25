package pl.edu.pw.elka.rso.manage.client;


import pl.edu.pw.elka.rso.manage.messages.Code;
import pl.edu.pw.elka.rso.manage.messages.Message;
import pl.edu.pw.elka.rso.manage.messages.MessageFactory;
import pl.edu.pw.elka.rso.manage.server.Config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientListener implements Runnable {

    Long id;
    Socket socket;
    boolean running;

    public ClientListener(Long id, Socket socket) {
        this.id = id;
        this.socket = socket;
    }

    public ClientListener(Socket socket) {
        this(null, socket);
    }


    public void start() {
        setRunning(true);
        new Thread(this).start();
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized Long getId() {
        return id;
    }

    public synchronized void setId(Long id) {
        this.id = id;
    }

    @Override
    public void run() {
        try(ObjectInputStream iStr = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oStr = new ObjectOutputStream(socket.getOutputStream())) {


            System.out.println("connected to server!!");
            // first the client
            clientInitMsg(iStr, oStr);

            // send the clients the initial list of servers
            serverInitMsg(iStr, oStr);

            // from now on, the server is responsible for different stuff

            while(isRunning()) {
                Message msg = (Message) iStr.readObject();
                switch (msg.getType()) {
                    case PING:
                        oStr.writeObject(MessageFactory.newPong());
                        System.out.println("server: ping");
                        System.out.println("client: pong");

                        break;

                }
            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        setRunning(false);

    }

    private void serverInitMsg(ObjectInputStream iStr, ObjectOutputStream oStr) {
        // TODO: for now there is nothing
    }

    private void clientInitMsg(ObjectInputStream iStr, ObjectOutputStream oStr) throws IOException, ClassNotFoundException {


        if(getId() == null) {
            oStr.writeObject(MessageFactory.newRequestId());
            Message msg = (Message) iStr.readObject();
            if(msg.isOk()) {
                setId((Long) msg.getData());
            } else {
                // handle error
            }
        } else {
            oStr.writeObject(MessageFactory.newShowId());
            Message msg = (Message) iStr.readObject();
            if(msg.getCode() == Code.YES) {
                // ok
            } else {
                id = null;
                clientInitMsg(iStr, oStr);
            }
        }

        oStr.writeObject(MessageFactory.newReady());
        System.out.println("client received an id: " + getId());
    }

}
