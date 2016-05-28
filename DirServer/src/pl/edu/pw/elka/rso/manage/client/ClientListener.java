package pl.edu.pw.elka.rso.manage.client;


import pl.edu.pw.elka.rso.manage.events.Event;
import pl.edu.pw.elka.rso.manage.messages.Code;
import pl.edu.pw.elka.rso.manage.messages.Message;
import pl.edu.pw.elka.rso.manage.messages.Messages;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.manage.util.LongIO;
import pl.edu.pw.elka.rso.manage.util.LongIOException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientListener implements Runnable {

    private Socket socket;
    private ObjectOutputStream oStr;
    private ObjectInputStream iStr;

    private boolean running;
    private Node node = new Node();


    private boolean idChanged = false;
    private NodeRegister nodeRegister;

    private String filePath; // where we are gonna keep id

    public ClientListener(String filePath, Socket socket, NodeType nodeType) {

        if(filePath != null) {
            try {
                node.setId(LongIO.readLong(filePath));
            } catch (LongIOException e) {
            }
            this.filePath = filePath;
        } else {
            this.filePath = "id.txt";
        }

        this.socket = socket;
        node.setNodeType(nodeType);


    }

    public static ClientListener FileServerListener(String idFilePath, Socket socket) {
        return new ClientListener(idFilePath, socket, NodeType.FILE_NODE);
    }


    public static ClientListener FileServerListener(Socket socket) {
        return new ClientListener(null, socket, NodeType.FILE_NODE);
    }

    public static ClientListener DirectoryServerListener(String idFilePath, Socket socket) {
        return new ClientListener(idFilePath, socket, NodeType.DIRECTORY_NODE);
    }


    public static ClientListener DirectoryServerListener(Long id, Socket socket) {
        return new ClientListener(null, socket, NodeType.DIRECTORY_NODE);
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
        return node.getId();
    }

    public synchronized void setId(Long id) {
        node.setId(id);
    }

    @Override
    public void run() {
        try{
            iStr = new ObjectInputStream(socket.getInputStream());
            oStr = new ObjectOutputStream(socket.getOutputStream());


            System.out.println("connected to a server");
            // first the client
            clientInitMsg();

            System.out.println("client info sent");

            // send the clients the initial list of servers
            serverInitMsg();

            System.out.println("sever info recved");

            // from now on, the server is responsible for different stuff

            while(isRunning()) {
                Message msg = (Message) iStr.readObject();
                switch (msg.getType()) {
                    case PING:
                        oStr.writeObject(Messages.pongMsg());
                        break;
                    case INFO:
                        recvEvent((Event)msg.getData());
                        break;
                }
                System.out.println("client recved: " + msg);
            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            setRunning(false);
            try {
                if(!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void recvEvent(Event data) {

        System.out.println("event " + data + " has been received");
        switch (data.getType()) {
            case NODE_CONNECTED_EVENT:
                 Node node = (Node)data.getData();
                 nodeRegister.registerNode(node);
                 break;
            case NODE_DISCONNECTED_EVENT:
                 node = (Node)data.getData();
                 nodeRegister.deregisterNode(node.getId());
                break;

        }
    }

    private void serverInitMsg() throws IOException, ClassNotFoundException {
        Message msg = (Message) iStr.readObject();
        nodeRegister = (NodeRegister) msg.getData();
    }

    private void clientInitMsg() throws IOException, ClassNotFoundException {

        // send id

        if(getId() == null) {
            oStr.writeObject(Messages.requestIdMsg());
            Message msg = (Message) iStr.readObject();
            setId((Long) msg.getData());
            idChanged = true;

        } else {
            oStr.writeObject(Messages.showIdMsg());
            Message msg = (Message) iStr.readObject();
            if(msg.getCode() == Code.YES) {
                // ok
                idChanged = false;

            } else {
                // server didnt accept the message

                node.setId(null);
                clientInitMsg();
            }
        }

        // send type of node

        oStr.writeObject(Messages.nodeTypeMsg(node.getNodeType()));

        // signal to the server that this node is ready
        oStr.writeObject(Messages.readyMsg());
        // write it to file if it has changed
        if(idChanged) {
            try {
                LongIO.writeLong(filePath, getId());
            } catch (LongIOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("client received an id: " + getId());
    }

    public NodeRegister getNodeRegister() {
        return nodeRegister;
    }


}
