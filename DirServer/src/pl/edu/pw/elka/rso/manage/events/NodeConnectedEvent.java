package pl.edu.pw.elka.rso.manage.events;


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
