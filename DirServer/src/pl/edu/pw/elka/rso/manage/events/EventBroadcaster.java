package pl.edu.pw.elka.rso.manage.events;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Broadcast event to all intereseted parties. It works in a publish-subscribe fashion.
 *
 */
public class EventBroadcaster implements Runnable {


    private static EventBroadcaster eventBroadcaster = new EventBroadcaster();


    public static EventBroadcaster getInstance() {
        return eventBroadcaster;
    }

    public EventBroadcaster() {
        new Thread(this).start();
    }

    private BlockingQueue<Event> eventsQueue = new LinkedBlockingDeque<>();
    private List<EventListener> eventListeners = new LinkedList<>();




    public synchronized List<EventListener> getEventListeners() {
        return eventListeners;
    }


    /** subscribe to event **/
    public synchronized void subscribe(EventListener eventListener) {
        eventListeners.add(eventListener);
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
                for(EventListener eventListener: getEventListeners()) {

                    // check if the event source is the same as event listener
                    if(!event.getSourceId().equals(eventListener.getId())) {
                        eventListener.notify(event);
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


}
