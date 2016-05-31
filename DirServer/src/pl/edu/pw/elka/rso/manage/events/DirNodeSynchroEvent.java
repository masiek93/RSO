package pl.edu.pw.elka.rso.manage.events;


public class DirNodeSynchroEvent extends Event {

    public DirNodeSynchroEvent(Object data, Long sourceId, Long dstId) {
        super(data, sourceId, dstId);
    }

    @Override
    public EventType getType() {
        return EventType.DIR_NODE_SYNCHRO;
    }
}
