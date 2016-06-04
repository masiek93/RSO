package pl.edu.pw.elka.rso.manage.events;

import pl.edu.pw.elka.rso.manage.server.ConnectionHandler;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Event bus allows for communication between listeners and event sources. It runs on a separate thread
 * and notifies the listeners whenever it receives a new event.
 *
 * To listen for a particular event:
 * 1. Implement EventListener
 *      * getId() has to return a unique id within the application
 *      * notify(Event ev) - will be called whenever a new event is generated and the listener is
 *                          the recipient of this event. If it is a time-consuming method, then it
 *                          should run on a separate thread.
 *
 * 2. Register the listener with:
 *      subscribeToEvent or subscribeToAllEvents
 *
 *
 *
 * To generate an event:
 *
 * EventBus evBus = EventBus.getInstance()
 * evBus.publish(new Event(...))
 *
 */
public class EventBus implements Runnable {


    private static EventBus eventBroadcaster;


    private BlockingQueue<Event> eventsQueue = new LinkedBlockingDeque<>();
    private Map<EventType, Set<EventListener>> listenersMap = new ConcurrentHashMap<>();



    public static EventBus getInstance() {
        if(eventBroadcaster == null) {
            eventBroadcaster = new EventBus();
        }
        return eventBroadcaster;
    }

    public EventBus() {
        // stub listeners
        for(EventType eventType: EventType.values()) {
            eventBroadcaster.listenersMap.put(eventType, new HashSet<>());
        }

        new Thread(this).start();
    }


    /** subscribeToAllEvents to all event **/
    public  void subscribeToAllEvents(EventListener eventListener) {
        for(EventType eventType: EventType.values()) {
            subscribeToEvent(eventListener, eventType);
        }
    }

    public void subscribeToEvent(EventListener eventListener, EventType eventType) {
        if(!listenersMap.containsKey(eventType)) {
            listenersMap.put(eventType, new HashSet<>());
        }
        listenersMap.get(eventType).add(eventListener);
    }

    /** publish event **/
    public void publish(Event event) {
        eventsQueue.add(event);
    }


    @Override
    public void run() {
        while(true) {
            try {
                Event event = eventsQueue.take();



                for(EventListener eventListener: listenersMap.get(event.getType())) {

                    // check if the event source is the same as event listener
                    if(!event.getSourceId().equals(eventListener.getId())) { // dont send to its source

                        if(event.isBroadCastEvent()) {
                            eventListener.notify(event);
                        } else if (event.getDstId().equals(eventListener.getId())){
                            eventListener.notify(event);
                        }

                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    public void unsubscribeFromAllEvents(EventListener eventListener) {
        for(EventType eventType: EventType.values()) {
            unsubscribeFromEvent(eventListener, eventType);
        }
    }

    private void unsubscribeFromEvent(EventListener eventListener, EventType eventType) {
        listenersMap.get(eventType).remove(eventListener);
    }

}
