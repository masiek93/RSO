package pl.edu.pw.elka.rso.manage.events;


/**
 * A multicast event that is sent to all interested listeners to inform
 * them of a node disconnection
 * in the system. The listener can do what is appropriate, such as: synchronization, replication, etc.
 */
public class NodeDisconnectedEvent extends Event{

    public NodeDisconnectedEvent(Object data, Long sourceId) {
        super(data, sourceId, null);
    }

    public NodeDisconnectedEvent(Object data, Long sourceId, Long dstId) {
        super(data, sourceId, dstId);
    }

    @Override
    public EventType getType() {
        return EventType.NODE_DISCONNECTED_EVENT;
    }

}
