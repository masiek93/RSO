package pl.edu.pw.elka.rso.manage.events;


/**
 * A multicast event that is sent to all interested listeners to inform
 * them of a new node in the system. The listener can do what is appropriate, such as: synchronization, replication, etc.
 */
public class NodeConnectedEvent extends Event {

    public NodeConnectedEvent(Object data, Long sourceId) {
        super(data, sourceId, null);
    }

    public NodeConnectedEvent(Object data, Long sourceId, Long dstId) {
        super(data, sourceId, dstId);
    }


    @Override
    public EventType getType() {
        return EventType.NODE_CONNECTED_EVENT;
    }

}
