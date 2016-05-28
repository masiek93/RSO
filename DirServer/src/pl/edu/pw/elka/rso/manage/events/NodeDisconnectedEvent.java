package pl.edu.pw.elka.rso.manage.events;


public class NodeDisconnectedEvent extends Event{
    public NodeDisconnectedEvent(Object data, Long sourceId) {
        super(data, sourceId);
    }

    @Override
    public EventType getType() {
        return EventType.NODE_DISCONNECTED_EVENT;
    }
}
