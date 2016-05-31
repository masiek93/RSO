package pl.edu.pw.elka.rso.manage.client;


import pl.edu.pw.elka.rso.manage.events.Event;
import pl.edu.pw.elka.rso.manage.events.EventType;
import pl.edu.pw.elka.rso.manage.events.Handler;
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
import java.util.HashMap;
import java.util.Map;

/**
 * A client listener is an object reponsible for maintaining the communication
 * with a node server (node directory server).
 *
 * The client listener can be in several states: trying to connect (trying = true, connected = false),
 *  connected (trying = true, connected = true), not trying (trying = false, connected = false).
 */
public abstract class ClientListener implements Runnable {

    public static final String DEFAULT_ID_FILE_PATH = "resources/gen/id.txt";

    // where is the id file stored?
    protected String idFilePath;

    // for sending information
    protected Socket socket;
    protected ObjectOutputStream oStr;
    protected ObjectInputStream iStr;

    // information about connected parties
    protected Node thisNode = new Node(); // data describing this thisNode
    protected Node otherNode; // data describing the other thisNode (manager)

    // whether id has changed after connecting to the server.
    private boolean idChanged = false;

    // the node register
    protected NodeRegister nodeRegister = NodeRegister.getInstance();

    // is it trying to connect.
    boolean trying = true;

    boolean connected = false;

    // for event handling
    // bind event with handler and it should work out of the box
    protected Map<EventType, Handler> handlers = new HashMap<>();


    public ClientListener(String idFilePath, NodeType nodeType) {
        this.idFilePath = idFilePath == null ? DEFAULT_ID_FILE_PATH : idFilePath;
        configureThisNode(nodeType);
        setUpHandlers();
    }

    private void configureThisNode(NodeType nodeType) {
        try {
            thisNode.setId(LongIO.readLong(idFilePath));
        } catch (LongIOException e) {
            // ignore it. It will send a request for a new id.
        }
        thisNode.setNodeType(nodeType);
        thisNode.setAlive(true);

        try {
            // register the nodes from configuration
            nodeRegister.initFromConf(Config.getInstance().directoryServerList);
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }


    private void setUpHandlers() {
        setUpBasicHandlers();
        setUpSpecificHandlers();
    }

    /**
     * Global handlers that are common to dir node client and file node client.
     * Handles: node connected event, node disconnected event
     */
    protected void setUpBasicHandlers() {
        handlers.put(EventType.NODE_CONNECTED_EVENT, new Handler() {
            @Override
            public void handleEvent(Event event) {
                Node node = (Node)event.getData();
                nodeRegister.registerNode(node);
            }
        });

        handlers.put(EventType.NODE_DISCONNECTED_EVENT, new Handler() {
            @Override
            public void handleEvent(Event event) {
                Node node = (Node)event.getData();
                nodeRegister.deregisterNode(node.getId());
            }
        });

    }


    /**
     * Abstract method for setting up specific handlers for subclasses.
     */
    protected abstract void setUpSpecificHandlers();


    public synchronized void setTrying(boolean trying) {
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

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public synchronized Long getId() {
        return thisNode.getId();
    }

    public synchronized void setId(Long id) {
        thisNode.setId(id);
    }


    /**
     * initial step of the protocol - exchange informations between node manager and this node.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void initialPhase() throws IOException, ClassNotFoundException {

        // first the client sends its information (parameters, id requests, etc.)
        clientInit();
        // server sends its information (nodes that it sees, etc)
        serverInit();

        System.out.println("initial phase completed with " + otherNode);
    }

    /**
     * request id, send information such as type of server etc.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void clientInit() throws IOException, ClassNotFoundException {

        // send id

        if(getId() == null) {
            oStr.writeObject(Messages.requestIdMsg());
            Message msg = (Message) iStr.readObject();
            setId((Long) msg.getData());
            idChanged = true;

        } else {
            // show id to the server for confirmation.
            oStr.writeObject(Messages.showIdMsg(getId()));
            Message msg = (Message) iStr.readObject();
            if(msg.getCode() == Code.YES) {
                idChanged = false;
            } else {
                // server didnt accept the message. retry.
                thisNode.setId(null);
                clientInit();
            }
        }

        // send information about this node
        oStr.writeObject(Messages.nodeInfo(thisNode));

        // signal to the server that this thisNode is ready
        oStr.writeObject(Messages.readyMsg());

        // write id to file if it has changed
        if(idChanged) {
            try {
                LongIO.writeLong(idFilePath, getId());
            } catch (LongIOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("client has sent its parameters: " + thisNode);
    }

    private void serverInit() throws IOException, ClassNotFoundException {
        Message msg = (Message) iStr.readObject();
        nodeRegister.update((NodeRegister) msg.getData());
    }




    public void runner() {

        try{

            iStr = new ObjectInputStream(socket.getInputStream());
            oStr = new ObjectOutputStream(socket.getOutputStream());


            setConnected(true);

            initialPhase();

            // from now on, the server is responsible for different stuff

            while(isConnected()) {
                Message msg = (Message) iStr.readObject();
                switch (msg.getType()) {
                    case PING:
                        oStr.writeObject(Messages.pongMsg());
                        break;
                    case EVENT:
                        handleEvent((Event) msg.getData());
                        break;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            setConnected(false);

            // unregister the other node
            if(otherNode != null && otherNode.getId() != null) {
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

            // TODO: handle the case when client connects to its own server
            try {
                socket = SSocketFactory.createSocket(node.getAddress(), node.getPort());
                otherNode = node;
                System.out.println("connected to " + node);
                return;
            } catch (IOException e) {

            }
        }
        throw new IOException("Cannot establish connection with any server");
    }




    private void handleEvent(Event data) {
        System.out.println("received new event " + data);
        Handler h = handlers.get(data.getType());
        if(h != null) {
            h.handleEvent(data);
        }
    }







}
