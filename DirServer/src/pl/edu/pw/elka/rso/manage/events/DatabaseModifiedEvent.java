package pl.edu.pw.elka.rso.manage.events;

/**
 * Created by ammar on 6/6/16.
 */
public class DatabaseModifiedEvent extends Event{

    public DatabaseModifiedEvent(Object data, Long sourceId, Long dstId) {
        super(data, sourceId, dstId);
    }

    @Override
    public EventType getType() {
        return EventType.DATABASE_MODIFIED;
    }

}
