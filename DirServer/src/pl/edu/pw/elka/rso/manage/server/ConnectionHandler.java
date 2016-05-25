package pl.edu.pw.elka.rso.manage.server;

import pl.edu.pw.elka.rso.manage.messages.Message;
import pl.edu.pw.elka.rso.manage.messages.MessageFactory;
import pl.edu.pw.elka.rso.manage.messages.Type;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;


public class ConnectionHandler extends Observable implements Runnable {

    Socket socket;
    boolean running;

    Queue<Object> eventQueue = new ConcurrentLinkedDeque<>();

    public static long SLEEP_PERIOD_MS = 1000;


    public ConnectionHandler(Socket sock) {
        this.socket = sock;
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
       try(ObjectOutputStream oStr = new ObjectOutputStream(socket.getOutputStream());
               ObjectInputStream iStr = new ObjectInputStream(socket.getInputStream());
            ) {

           System.out.println("new client!!");
           // first the client
           clientInitMsg(iStr, oStr);

           // notify server that there is a new server of
           updateServerState();

           // send the clients the initial list of servers
           serverInitMsg(iStr, oStr);


           while(isRunning()) {
               try {

                   if(eventQueue.isEmpty()) {

                        oStr.writeObject(MessageFactory.newPing());
                        Message msg = (Message) iStr.readObject();
                        if(msg.getType() == Type.PONG) {
                            // ok
                        } else {
                            // something is wrong
                            tryToSolveError(iStr, oStr);
                        }

                   } else {
                       // process events
                       sendEvent(eventQueue.poll());
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
           if(!socket.isClosed()) {
               try {
                   socket.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
        setRunning(false);
        updateServerState();
    }

    private void sendEvent(Object poll) {
        // TODO: implement this event
    }

    private void tryToSolveError(ObjectInputStream iStr, ObjectOutputStream oStr) throws IOException{
        // TODO: throws exeption when it can't solve the error. Change the exception signature to something else.
    }


    private void updateServerState() {
        // send to listener what happened

    }

    private void serverInitMsg(ObjectInputStream iStr, ObjectOutputStream oStr) {


        // TODO: send the list of all servers

    }

    private void clientInitMsg(ObjectInputStream iStr, ObjectOutputStream oStr) throws IOException, ClassNotFoundException {
        Message msg = null;
        do {

            msg = (Message) iStr.readObject();
            if(!msg.getType().equals(Type.READY))
                 handleMsg(oStr, msg);
        } while(msg.getType().equals(Type.READY));
    }

    private void handleMsg(ObjectOutputStream oStr, Message msg) throws IOException {
        IdManager idMng = IdManager.getInstance();
        switch (msg.getType()) {
            case ID_REQ:
                oStr.writeObject(MessageFactory.newResponseId(idMng.newId()));
                break;
            case ID_SHOW:
                // client request id to be accepted by the server
                if (idMng.isOk((Long)msg.getData())) {
                    oStr.writeObject(MessageFactory.newYesMsg());
                } else {
                    oStr.writeObject(MessageFactory.newNoMsg());
                }
                break;
        }
    }




}
