package pl.edu.pw.elka.rso.manage.synchro;


import pl.edu.pw.elka.rso.manage.events.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class AbstractEventListener implements EventListener, Runnable {

    protected EventBus eventBus;
    protected BlockingQueue<Event> eventQueue = new LinkedBlockingDeque<>();
    protected Map<EventType, Handler> handlers = new HashMap<>();

    protected abstract void subscribeToEvents();
    protected abstract void setUpEventHandlers();


    public AbstractEventListener() {
        eventBus = EventBus.getInstance();
        subscribeToEvents();
        setUpEventHandlers();
    }

    @Override
    public void notify(Event event) {
        eventQueue.add(event);
    }


    @Override
    public void run() {
        while(true) {
            try {
                Event event = eventQueue.take();
                Handler handler = handlers.get(event.getType());
                if(handler != null) {
                    handler.handleEvent(event);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEventListener)) return false;

        AbstractEventListener that = (AbstractEventListener) o;

        if (eventBus != null ? !eventBus.equals(that.eventBus) : that.eventBus != null) return false;
        if (eventQueue != null ? !eventQueue.equals(that.eventQueue) : that.eventQueue != null) return false;
        if (handlers != null ? !handlers.equals(that.handlers) : that.handlers != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = eventBus != null ? eventBus.hashCode() : 0;
        result = 31 * result + (eventQueue != null ? eventQueue.hashCode() : 0);
        result = 31 * result + (handlers != null ? handlers.hashCode() : 0);
        return result;
    }
}
