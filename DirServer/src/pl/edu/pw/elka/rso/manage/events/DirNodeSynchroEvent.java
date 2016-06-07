package pl.edu.pw.elka.rso.manage.events;


/**
 * Event for synchronizing dir node replicas with the master. The data field contains the details
 * and it is sent to the replicas.
 */
public class DirNodeSynchroEvent extends Event {

    public DirNodeSynchroEvent(Object data, Long sourceId, Long dstId) {
        super(data, sourceId, dstId);
    }

    @Override
    public EventType getType() {
        return EventType.DIR_NODE_SYNCHRO;
    }
}
