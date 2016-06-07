package pl.edu.pw.elka.rso.manage.synchro;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.manage.events.DirNodeSynchroEvent;
import pl.edu.pw.elka.rso.manage.events.Event;
import pl.edu.pw.elka.rso.manage.events.EventType;
import pl.edu.pw.elka.rso.manage.events.Handler;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.manage.screen.NodeScreen;
import pl.edu.pw.elka.rso.repo.MetaDataRWRespository;
import pl.edu.pw.elka.rso.repo.MetaDataRepositoryException;
import pl.edu.pw.elka.rso.repo.db.DBFacade;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class DirNodeSynchronizer extends AbstractEventListener {


    static final Logger LOGGER = LoggerFactory.getLogger(DirNodeSynchronizer.class);

    static DirNodeSynchronizer dirNodeSynchronizer;

    public static DirNodeSynchronizer getInstance() {
        if(dirNodeSynchronizer == null)
            dirNodeSynchronizer = new DirNodeSynchronizer();
        return dirNodeSynchronizer;
    }

    private DirNodeSynchronizer() {
        LOGGER.info("started");
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();

    }

    @Override
    protected void subscribeToEvents() {
        eventBus.subscribeToEvent(this, EventType.NODE_CONNECTED_EVENT);
        eventBus.subscribeToEvent(this, EventType.DATABASE_MODIFIED);

    }

    @Override
    protected void setUpEventHandlers() {

        handlers.put(EventType.NODE_CONNECTED_EVENT, new Handler() {
            @Override
            public void handleEvent(Event event) {
                // first time the node connects

                LOGGER.info("handling event {}", event);


                Long senderId = event.getSourceId();
                Node node = (Node) event.getData();
                try {
                    MetaDataRWRespository.getInstance().addNode(node);
                } catch (IOException | JAXBException | MetaDataRepositoryException e) {
                    LOGGER.error("error while writing to database", e);
                }

                if(node.getNodeType() == NodeType.DIRECTORY_NODE) {
                    NodeScreen.addLogEntry("syncing data with directory node");
                    // TODO: how to sync? data for syncing?
                    LOGGER.info("sending whole database to redundant dir node with id = {}", senderId);
                    eventBus.publish(new DirNodeSynchroEvent(DBFacade.getInstance().backup(), getId(), senderId));
                }
            }


        });

        handlers.put(EventType.DATABASE_MODIFIED, new Handler() {
            @Override
            public void handleEvent(Event event) {

                LOGGER.info("received database_modified_event");

                // wysylamy sql-ki
                persistData((String) event.getData());
                replicateData((String) event.getData());
            }
        });

    }

    private void persistData(String data) {

        LOGGER.info("persisting sql statments");

        DBFacade db = DBFacade.getInstance();
        db.executeSqlStmt(data);
        //db.closeConnection();

    }


    public void replicateData(String data) {
        LOGGER.info("replicating changes acrosss replicas....");
        // replication == sending DirNodeSynchro event to all replicas
        NodeRegister nodeRegister = NodeRegister.getInstance();
        for(Node replicas: nodeRegister.getAliveDirectoryNodes()) {

            eventBus.publish(new DirNodeSynchroEvent(data, getId(),
                            replicas.getId()));
        }
    }

    @Override
    public Long getId() {
        return -1l;
    }





}
