package pl.edu.pw.elka.rso.manage.events;

import java.io.Serializable;

public abstract class Event implements Serializable {

    private Long sourceId;
    private Object data;



    public Event(Object data, Long sourceId) {
        this.data = data;
        this.sourceId = sourceId;
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

    public abstract EventType getType();

    @Override
    public String toString() {
        return "Event{" +
                "data=" + data +
                ", sourceId=" + sourceId +
                ", eventType=" + getType() +
                '}';
    }
}
