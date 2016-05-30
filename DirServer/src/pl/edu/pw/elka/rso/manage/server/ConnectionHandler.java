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
    EventBroadcaster eventBroadcaster; // broadcast some events
    NodeRegister nodeRegister = NodeRegister.getInstance();
    Node node;


    public ConnectionHandler(Socket sock) {

        this.socket = sock;

        eventBroadcaster = EventBroadcaster.getInstance();
        eventBroadcaster.subscribe(this);
        node = new Node();
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


           while(isRunning()) {
               try {

                   if(eventQueue.isEmpty()) {

                        oStr.writeObject(Messages.pingMsg());
                        Message msg = (Message) iStr.readObject();
                        if(msg.getType() == Type.PONG) {
                            // ok
                        } else {
                            tryToSolveError(msg);
                        }

                   } else {
                       Event ev = eventQueue.poll();
                       System.out.println("new event " +  ev + " is sent to id = " + node.getId());
                       // process events
                       sendEvent(ev);
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
           eventBroadcaster.publish(new NodeDisconnectedEvent(node, getId()));

           if(node.getId() !=null)
            nodeRegister.deregisterNode(node.getId());


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
        // notify other nodes that there is a new connected node

        eventBroadcaster.publish(new NodeConnectedEvent(node, getId()));
        System.out.println("node = " + node);
        nodeRegister.registerNode(node);


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
                node.setId(idMng.newId());
                oStr.writeObject(Messages.responseIdMsg(node.getId()));
                break;
            case ID_SHOW:
                // client request id to be accepted by the server
                Long recvId = (Long) data;
                if (idMng.isOk(recvId)) {
                    node.setId(recvId);
                    oStr.writeObject(Messages.yesMsg());
                } else {
                    oStr.writeObject(Messages.noMsg());
                }
                break;
            case INFO:
                // client sends the type of server
                node = (Node) data;
                break;
        }
    }


    @Override
    public void notify(Event event) {
        System.out.println("publish/subscribe has notified client handler");
        eventQueue.offer(event);
    }

    @Override
    public Long getId() {
        return node.getId();
    }


}
