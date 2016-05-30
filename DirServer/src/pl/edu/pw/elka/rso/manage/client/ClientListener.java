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

public abstract class ClientListener implements Runnable {

    public static final String DEFAULT_ID_FILE_PATH = "resources/gen/id.txt";

    protected Socket socket;
    protected ObjectOutputStream oStr;
    protected ObjectInputStream iStr;

    protected boolean running;
    protected Node thisNode = new Node(); // data describing this thisNode
    protected Node otherNode; // data describing the other thisNode (manager)

    protected boolean idChanged = false;  // if id changes then the client should persist this information

    protected NodeRegister nodeRegister = NodeRegister.getInstance();
    protected String idFilePath; // where we are gonna store id


    private boolean isConnected;

    boolean trying = true; // is it trying to connect

    public ClientListener(String idFilePath, NodeType nodeType) {

        if(idFilePath != null) {
            this.idFilePath = idFilePath;
        } else {
            this.idFilePath = DEFAULT_ID_FILE_PATH;
        }

        try {
            thisNode.setId(LongIO.readLong(idFilePath));
        } catch (LongIOException e) {
            // ignore it. Id will be null and it will be requested from the server.
        }

        thisNode.setNodeType(nodeType);
        try {
            // register the nodes from configuration
            nodeRegister.initFromConf(Config.getInstance().directoryServerList);

        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }

    }

    public synchronized void setTrying(boolean trying)
    {
        this.trying = trying;
    }


    public synchronized boolean isTrying() {
        return trying;
    }

    public void start() {
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
        return thisNode.getId();
    }

    public synchronized void setId(Long id) {
        thisNode.setId(id);
    }


    public void initialPhase() throws IOException, ClassNotFoundException {

        System.out.println("connected to directory node");
        // first the client its information (parameters, id requests, etc.)
        clientInitMsg();
        // server sends its information (nodes that it sees, etc)
        serverInitMsg();
        System.out.println("sever info recieved. Initial phase finished");
    }





    public void runner() {

        try{

            iStr = new ObjectInputStream(socket.getInputStream());
            oStr = new ObjectOutputStream(socket.getOutputStream());


            setRunning(true);

            // pick one of the servers from node register

            // initialize
            initialPhase();

            // from now on, the server is responsible for different stuff

            while(isRunning()) {
                Message msg = (Message) iStr.readObject();
                switch (msg.getType()) {
                    case PING:
                        oStr.writeObject(Messages.pongMsg());
                        break;
                    case EVENT:
                        recvEvent((Event)msg.getData());
                        break;
                }
                System.out.println("client recved: " + msg);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            setRunning(false);

            // unregister the other node
            if(otherNode.getId() != null) {
                nodeRegister.deregisterNode(otherNode.getId());
            }

            try {
                if(!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    protected void pickServer() throws InterruptedException, IOException {
        for(Node node: nodeRegister.getDirectoryNodes()) {
            System.out.println("trying to connect with node: " +  node);

            // TODO: handle the case when client connects to its own server
            try {
                socket = SSocketFactory.createSocket(node.getAddress(), node.getPort());
                otherNode = node;
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
        nodeRegister.clear(); // discard everything that we saw. Refresh this node's information
        nodeRegister.update((NodeRegister) msg.getData());
    }

    /**
     * request id, send information such as type of server etc.
     * @throws IOException
     * @throws ClassNotFoundException
     */
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
                thisNode.setId(null);
                clientInitMsg();
            }
        }

        // send type of thisNode

        oStr.writeObject(Messages.nodeTypeMsg(thisNode.getNodeType()));

        // signal to the server that this thisNode is ready
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
