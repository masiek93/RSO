package pl.edu.pw.elka.rso.manage.events;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Broadcast event to all intereseted parties. It works in a publish-subscribeToAllEvents fashion.
 * Run on a separate thread. The listener subscribes to all events, from all senders.
 *
 */
public class EventBus implements Runnable {


    private static EventBus eventBroadcaster;


    private BlockingQueue<Event> eventsQueue = new LinkedBlockingDeque<>();
    private Map<EventType, Set<EventListener>> listenersMap = new ConcurrentHashMap<>();

    //private List<EventListener> eventListeners = new LinkedList<>();


    public static EventBus getInstance() {
        if(eventBroadcaster == null) {
            eventBroadcaster = new EventBus();
        }
        return eventBroadcaster;
    }

    public EventBus() {
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

                        if(event.getDstId() == null) {
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


}
