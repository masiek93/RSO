package pl.edu.pw.elka.rso.manage.client;


import pl.edu.pw.elka.rso.manage.events.Event;
import pl.edu.pw.elka.rso.manage.messages.Code;
import pl.edu.pw.elka.rso.manage.messages.Message;
import pl.edu.pw.elka.rso.manage.messages.Messages;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.manage.util.Config;
import pl.edu.pw.elka.rso.manage.util.LongIO;
import pl.edu.pw.elka.rso.manage.util.LongIOException;
import pl.edu.pw.elka.rso.ssl.SSocketFactory;

import javax.xml.bind.JAXBException;
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
    private NodeRegister nodeRegister = NodeRegister.getInstance();

    private String idFilePath; // where we are gonna store id

    private Node directoryServerNode;



    public ClientListener(String idFilePath, NodeType nodeType) {

        if(idFilePath != null) {
            this.idFilePath = idFilePath;
        } else {
            this.idFilePath = "resources/gen/id.txt";
        }

        try {
            node.setId(LongIO.readLong(idFilePath));
        } catch (LongIOException e) {

        }


        node.setNodeType(nodeType);
        try {
            nodeRegister.initFromConf(Config.getInstance().directoryServerList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    public ClientListener(String idFilePath, NodeType directoryNode, Node dirServer) {
        this(idFilePath, directoryNode);
        this.directoryServerNode = dirServer;
    }

    public static ClientListener FileServerListener(String idFilePath) {
        return new ClientListener(idFilePath, NodeType.FILE_NODE);
    }


    public static ClientListener FileServerListener() {
        return new ClientListener(null, NodeType.FILE_NODE);
    }

    public static ClientListener DirectoryServerListener(String idFilePath, Node dirServer) {
        return new ClientListener(null, NodeType.DIRECTORY_NODE, dirServer);
    }

    public static ClientListener DirectoryServerListener(String idFilePath) {
        return new ClientListener(null, NodeType.DIRECTORY_NODE, null);
    }



    public void start() {
        setRunning(true);
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
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

            pickServer();

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



        } catch (IOException | InterruptedException | ClassNotFoundException e) {
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


    private void pickServer() throws InterruptedException, IOException {
        for(Node node: nodeRegister.getDirectoryNodes()) {
            System.out.println("trying to connect with node: " +  node);
            if(directoryServerNode != null && directoryServerNode.equals(node)) {
                continue; // don't let client connect with its own server
            }
            try {
                socket = SSocketFactory.createSocket(node.getAddress(), node.getPort());
                System.out.println("connected to " + node);
                return;
            } catch (IOException e) {
                System.out.println("cannot establish connection " + e);
            }
        }
        System.out.println("Could not establish connection");
        throw new IOException("Cannot establish connection with any server");
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
        nodeRegister.clear();
        nodeRegister.update((NodeRegister) msg.getData());
    }

    private void clientInitMsg() throws IOException, ClassNotFoundException {

        // send id

        if(getId() == null) {
            oStr.writeObject(Messages.requestIdMsg());
            Message msg = (Message) iStr.readObject();
            setId((Long) msg.getData());
            idChanged = true;

        } else {
            oStr.writeObject(Messages.showIdMsg(getId()));
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
                LongIO.writeLong(idFilePath, getId());
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
