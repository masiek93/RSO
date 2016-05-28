package pl.edu.pw.elka.rso.manage.events;


public class NodeConnectedEvent extends Event {
    public NodeConnectedEvent(Object data, Long sourceId) {
        super(data, sourceId);
    }

    @Override
    public EventType getType() {
        return EventType.NODE_CONNECTED_EVENT;
    }
}
