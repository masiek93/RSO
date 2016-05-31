package pl.edu.pw.elka.rso.manage.synchro;


import pl.edu.pw.elka.rso.manage.events.DirNodeSynchroEvent;
import pl.edu.pw.elka.rso.manage.events.Event;
import pl.edu.pw.elka.rso.manage.events.EventType;
import pl.edu.pw.elka.rso.manage.events.Handler;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.node.NodeType;

public class DirNodeSynchronizer extends AbstractEventListener {

    @Override
    protected void subscribeToEvents() {
        eventBus.subscribeToEvent(this, EventType.NODE_CONNECTED_EVENT);

    }

    @Override
    protected void setUpEventHandlers() {

        handlers.put(EventType.NODE_CONNECTED_EVENT, new Handler() {
            @Override
            public void handleEvent(Event event) {
                // first time the node connects
                Long senderId = event.getSourceId();
                Node node = (Node) event.getData();
                if(node.getNodeType() == NodeType.DIRECTORY_NODE) {
                    System.out.println("syncing data with directory node");
                    // TODO: how to sync? data for syncing?
                    eventBus.publish(new DirNodeSynchroEvent("dummy synchro", getId(), senderId));
                }
            }
        });

    }


    public void replicateData(Object data) {

        // replication == sending DirNodeSynchro event to all replicas
        NodeRegister nodeRegister = NodeRegister.getInstance();
        for(Node replicas: nodeRegister.getDirectoryNodes()) {
            eventBus.publish(new DirNodeSynchroEvent(data, getId(),
                            replicas.getId()));
        }
    }

    @Override
    public Long getId() {
        return -1l;
    }





}
