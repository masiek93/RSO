package pl.edu.pw.elka.rso.manage.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.manage.events.*;
import pl.edu.pw.elka.rso.manage.messages.Message;
import pl.edu.pw.elka.rso.manage.messages.Messages;
import pl.edu.pw.elka.rso.manage.messages.Type;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.manage.screen.NodeScreen;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;


/**
 * Maintain a connection between a manager and other node for the purpose of:
 * * nodes discovery
 * * nodes identification
 * * informing nodes of particular event:
 *  Common Events to all nodes: nodeConnectedEvent, nodeDisconnectedEvent
 *      Events particular to dir node: synchroEvent,
 *      Events particular to file node: // for no there is nothing
 */
public class ConnectionHandler implements Runnable, EventListener {

    public static long SLEEP_PERIOD_MS = 1000;

    // for comunication
    Socket socket;
    ObjectInputStream iStr;
    ObjectOutputStream oStr;

    // is connected
    boolean connected;

    // store event to be sent to the client
    Queue<Event> eventQueue = new ConcurrentLinkedDeque<>();
    // subscribe to events
    EventBus eventBus;


    NodeRegister nodeRegister = NodeRegister.getInstance();

    // the client node. contains information about the client.
    Node clientNode;

    Logger LOGGER = LoggerFactory.getLogger(ConnectionHandler.class);


    public ConnectionHandler(Socket sock) {

        this.socket = sock;

        eventBus = EventBus.getInstance();
        eventBus.subscribeToEvent(this, EventType.NODE_CONNECTED_EVENT);
        eventBus.subscribeToEvent(this, EventType.NODE_DISCONNECTED_EVENT);

        clientNode = new Node();
    }

    public void start() {
        new Thread(this).start();
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }


    public void subscribeToSpecificEvents() {
        if (clientNode.getNodeType() == NodeType.DIRECTORY_NODE) {
            eventBus.subscribeToEvent(this, EventType.DIR_NODE_SYNCHRO);
        } else if(clientNode.getNodeType() == NodeType.FILE_NODE) {

        }
    }

    @Override
    public void run() {
        // client sends its stuff at the begining
        try {

            oStr = new ObjectOutputStream(socket.getOutputStream());
            iStr = new ObjectInputStream(socket.getInputStream());

            LOGGER.info("started");

            setConnected(true);
            initialPhase();


            while (isConnected()) {
                try {

                    if (eventQueue.isEmpty()) {

                        oStr.writeObject(Messages.pingMsg());   // ping
                        Message msg = (Message) iStr.readObject(); // pong

                    } else {
                        Event ev = eventQueue.poll();
                        if (ev != null) {
                            NodeScreen.addLogEntry("new event " + ev + " is sent to id = " + clientNode.getId());
                            sendEvent(ev);
                        }
                    }

                    TimeUnit.MILLISECONDS.sleep(SLEEP_PERIOD_MS);

                } catch (InterruptedException e) {
                   LOGGER.error("unexpected error", e);
                }

            }


        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("lost connection with {}", clientNode, e);
        } finally {
            setConnected(false);
            eventBus.publish(new NodeDisconnectedEvent(clientNode, clientNode.getId()));

            if (clientNode.getId() != null) {
                // deregister this client node from node register & unsubscribe from eventBus,
                // so garbage collecter can remove this object
                nodeRegister.deregisterNode(clientNode.getId());
                eventBus.unsubscribeFromAllEvents(this);
            }

            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOGGER.error("could not close socket connection", e);
                }
            }
        }


    }

    private void initialPhase() throws IOException, ClassNotFoundException {

        NodeScreen.addLogEntry("A new client has just connected!");
        clientInit();
        // publish this event to the event bus

        subscribeToSpecificEvents();

        eventBus.publish(new NodeConnectedEvent(clientNode, clientNode.getId()));
        nodeRegister.registerNode(clientNode);

        serverInit();
        NodeScreen.addLogEntry("initial phase completed." + clientNode);
    }

    private void sendEvent(Event event) throws IOException {

        oStr.writeObject(Messages.eventMessage(event));
    }


    private void serverInit() throws IOException {
        oStr.writeObject(Messages.nodeRegisterMsg(nodeRegister));
    }


    /**
     * Looks ugly, but works...
     * <p>
     * *
     */

    private void clientInit() throws IOException, ClassNotFoundException {
        Message msg = null;
        do {

            msg = (Message) iStr.readObject();
            if (!msg.getType().equals(Type.READY))
                handleMsg(msg);
        } while (!msg.getType().equals(Type.READY));
    }

    private void handleMsg(Message msg) throws IOException {
        IdManager idMng = IdManager.getInstance();
        Object data = msg.getData();

        switch (msg.getType()) {
            case ID_REQ:
                clientNode.setId(idMng.newId());
                oStr.writeObject(Messages.responseIdMsg(clientNode.getId()));
                break;
            case ID_SHOW:
                // client request id to be accepted by the server
                Long recvId = (Long) data;
                if (idMng.isOk(recvId)) {
                    clientNode.setId(recvId);
                    oStr.writeObject(Messages.yesMsg());
                } else {
                    oStr.writeObject(Messages.noMsg());
                }
                break;
            case INFO:
                // client sends the type of server
                clientNode = (Node) data;
                break;
        }
    }


    @Override
    public void notify(Event event) {
        NodeScreen.addLogEntry(clientNode + " is going to receive event " + event);
        eventQueue.offer(event);
    }


    @Override
    public Long getId() {
        return clientNode.getId();
    }


    // auto generated

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectionHandler)) return false;

        ConnectionHandler that = (ConnectionHandler) o;

        if (connected != that.connected) return false;
        if (eventBus != null ? !eventBus.equals(that.eventBus) : that.eventBus != null) return false;
        if (eventQueue != null ? !eventQueue.equals(that.eventQueue) : that.eventQueue != null) return false;
        if (iStr != null ? !iStr.equals(that.iStr) : that.iStr != null) return false;
        if (clientNode != null ? !clientNode.equals(that.clientNode) : that.clientNode != null) return false;
        if (nodeRegister != null ? !nodeRegister.equals(that.nodeRegister) : that.nodeRegister != null) return false;
        if (oStr != null ? !oStr.equals(that.oStr) : that.oStr != null) return false;
        if (socket != null ? !socket.equals(that.socket) : that.socket != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = socket != null ? socket.hashCode() : 0;
        result = 31 * result + (iStr != null ? iStr.hashCode() : 0);
        result = 31 * result + (oStr != null ? oStr.hashCode() : 0);
        result = 31 * result + (connected ? 1 : 0);
        result = 31 * result + (eventQueue != null ? eventQueue.hashCode() : 0);
        result = 31 * result + (eventBus != null ? eventBus.hashCode() : 0);
        result = 31 * result + (nodeRegister != null ? nodeRegister.hashCode() : 0);
        result = 31 * result + (clientNode != null ? clientNode.hashCode() : 0);
        return result;
    }
}
