package pl.edu.pw.elka.rso.manage.events;

import java.io.Serializable;


/**
 * Object containing information about an event.
 */
public abstract class Event implements Serializable {

    private Long sourceId; // who generated the event. The source of an event.
    private Long dstId; // if null then it is a multicast event, otherwise it has a destination listener.
    private Object data; // data must be serializable


    public Event(Object data, Long sourceId, Long dstId) {
        this.data = data;
        this.sourceId = sourceId;
        this.dstId = dstId;
    }

    public Long getDstId() {
        return dstId;
    }

    public void setDstId(Long dstId) {
        this.dstId = dstId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isBroadCastEvent() {
        return getDstId() == null;
    }

    public abstract EventType getType();

    @Override
    public String toString() {
        return "Event{" +
                "data=" + data +
                ", sourceId=" + sourceId +
                ", dstId=" + dstId +
                ", eventType=" + getType() +
                '}';
    }
}
