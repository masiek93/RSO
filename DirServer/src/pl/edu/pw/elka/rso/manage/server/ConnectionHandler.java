package pl.edu.pw.elka.rso.manage.server;

import pl.edu.pw.elka.rso.manage.events.*;
import pl.edu.pw.elka.rso.manage.messages.Message;
import pl.edu.pw.elka.rso.manage.messages.Messages;
import pl.edu.pw.elka.rso.manage.messages.Type;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.node.NodeType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;


public class ConnectionHandler implements Runnable, EventListener {

    public static long SLEEP_PERIOD_MS = 1000;

    Socket socket;
    ObjectInputStream iStr;
    ObjectOutputStream oStr;

    boolean running;

    Queue<Event> eventQueue = new ConcurrentLinkedDeque<>();
    EventBus eventBus;
    NodeRegister nodeRegister = NodeRegister.getInstance();

    Node clientNode;


    public ConnectionHandler(Socket sock) {

        this.socket = sock;

        eventBus = EventBus.getInstance();
        eventBus.subscribeToEvent(this, EventType.NODE_CONNECTED_EVENT);
        eventBus.subscribeToEvent(this, EventType.NODE_DISCONNECTED_EVENT);

        clientNode = new Node();
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

    @Override
    public void run() {
        // client sends its stuff at the begining
       try {

           oStr = new ObjectOutputStream(socket.getOutputStream());
           iStr = new ObjectInputStream(socket.getInputStream());


           initialPhase();

           if(clientNode.getNodeType() == NodeType.DIRECTORY_NODE) {
               eventBus.subscribeToEvent(this, EventType.DIR_NODE_SYNCHRO);
           }

           while(isRunning()) {
               try {

                   if(eventQueue.isEmpty()) {

                        oStr.writeObject(Messages.pingMsg());
                        Message msg = (Message) iStr.readObject();
//                        if(msg.getType() == Type.PONG) {
//                            // ok
//                        } else {
//                            tryToSolveError(msg);
//                        }

                   } else {
                       Event ev = eventQueue.poll();
                       if(ev != null) {
                           System.out.println("new event " + ev + " is sent to id = " + clientNode.getId());
                           // process events
                           sendEvent(ev);
                       }
                   }

                   TimeUnit.MILLISECONDS.sleep(SLEEP_PERIOD_MS);

               } catch (InterruptedException e) {
                   e.printStackTrace();
               }

           }




       } catch (IOException e) {
           e.printStackTrace();

       } catch (ClassNotFoundException e) {
           e.printStackTrace();

       } finally {
           setRunning(false);
           eventBus.publish(new NodeDisconnectedEvent(clientNode, clientNode.getId()));

           if(clientNode.getId() !=null)
                nodeRegister.deregisterNode(clientNode.getId());


           if(!socket.isClosed()) {
               try {
                   socket.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }




    }

    private void initialPhase() throws IOException, ClassNotFoundException {
        System.out.println("client has connected!!");

        // first the client
        clientInitMsg();


        System.out.println("client info recved");
        // notify other nodes that there is a new connected thisNode

        eventBus.publish(new NodeConnectedEvent(clientNode, clientNode.getId()));
        System.out.println("thisNode = " + clientNode);
        nodeRegister.registerNode(clientNode);


        // send the clients the initial list of servers
        serverInitMsg();
        System.out.println("server info sent");
    }

    private void sendEvent(Event event) throws IOException {

        oStr.writeObject(Messages.eventMessage(event));
    }

    private void tryToSolveError(Message msg) throws IOException{
        // TODO: throws exeption when it can't solve the error. Change the exception signature to something else.
        System.out.println("client sent weired message " + msg);

    }



    private void serverInitMsg() throws IOException {
        oStr.writeObject(Messages.nodeRegisterMsg(nodeRegister));
    }


    /**
     *
     * Send the server basic information such as: type of server, id of the server, and so on..
     *
     * **/

     private void clientInitMsg() throws IOException, ClassNotFoundException {
        Message msg = null;
        do {

            msg = (Message) iStr.readObject();
            if(!msg.getType().equals(Type.READY))
                 handleMsg(msg);
        } while(!msg.getType().equals(Type.READY));
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
        System.out.println(clientNode + " is going to receive event " + event);
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

        if (running != that.running) return false;
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
        result = 31 * result + (running ? 1 : 0);
        result = 31 * result + (eventQueue != null ? eventQueue.hashCode() : 0);
        result = 31 * result + (eventBus != null ? eventBus.hashCode() : 0);
        result = 31 * result + (nodeRegister != null ? nodeRegister.hashCode() : 0);
        result = 31 * result + (clientNode != null ? clientNode.hashCode() : 0);
        return result;
    }
}
