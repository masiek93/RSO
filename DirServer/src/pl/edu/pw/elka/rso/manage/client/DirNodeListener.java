package pl.edu.pw.elka.rso.manage.client;

import pl.edu.pw.elka.rso.manage.events.Event;
import pl.edu.pw.elka.rso.manage.events.EventType;
import pl.edu.pw.elka.rso.manage.events.Handler;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.manage.screen.NodeScreen;
import pl.edu.pw.elka.rso.util.Config;
import pl.edu.pw.elka.rso.repo.db.DbContainer;
import pl.edu.pw.elka.rso.repo.db.DBFacade;
import pl.edu.pw.elka.rso.ssl.SSocketFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DirNodeListener extends ClientListener {

    public static final int MAX_NRET = 2; // number of retries
    public static final long WAITING_PERIOD_MS = 1000; // period between retries

    private boolean initialSynch = false;



    public DirNodeListener(String idFilePath, int port) {
        super(idFilePath, NodeType.DIRECTORY_NODE);
        thisNode.setPort(port);
    }

    @Override
    public void run() {


        setTrying(true);


        int nret = 0;

        while(nret < MAX_NRET) {
            try {

                initialSynch = false;

                pickServer();
                runner();
                Thread.sleep(WAITING_PERIOD_MS);

                nret = 0;

            } catch (InterruptedException | IOException e) {
                nret++;
            } finally {
                if(socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        setTrying(false);
    }

    @Override
    protected void setUpSpecificHandlers() {
        handlers.put(EventType.DIR_NODE_SYNCHRO, new Handler() {
            @Override
            public void handleEvent(Event event) {

                LOGGER.info("received event {}", event.getType());
                LOGGER.info("Dir node client is syncing with master.");


                if(!initialSynch) {
                    LOGGER.info("Initial syncing with master");

                    DbContainer container = (DbContainer) event.getData();

                    NodeScreen.addLogEntry("first time syncing with the master dir node");
                    NodeScreen.addLogEntry("got " + container.totalSize() + " elements from server");
                    try {
                        Files.deleteIfExists(Paths.get(Config.getInstance().backupDbProdPath));
                        LOGGER.info("deleted old database");
                        DBFacade.getRedInstance().fillDbFromBackup(container);

                    } catch (IOException | JAXBException e) {
                        LOGGER.info("error while synchronizing: ", e);
                    }

                    initialSynch = true;


                } else {
                    handleDataBaseUpdate(event);
                }
            }

            private void handleDataBaseUpdate(Event event) {
                LOGGER.info("updating databse to synchronise with the master: \n" + event.getData());
                DBFacade facade = DBFacade.getRedInstance();
                facade.executeSqlStmt((String) event.getData());
            }
        });
    }



    protected void pickServer() throws InterruptedException, IOException {
        for(Node node: nodeRegister.getDirectoryNodes()) {
            try {
                // dont connect to its own server
                if(thisNode.equals(node))
                    continue;
                socket = SSocketFactory.createSocket(node.getAddress(), node.getPort());
                NodeScreen.addLogEntry("connected to " + node);
                otherNode = node;
                return;
            } catch (IOException e) {

            }
        }
        throw new IOException("Cannot establish connection with any server");
    }




}
