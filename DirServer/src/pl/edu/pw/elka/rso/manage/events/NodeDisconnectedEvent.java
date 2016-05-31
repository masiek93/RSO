package pl.edu.pw.elka.rso.manage.events;


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
