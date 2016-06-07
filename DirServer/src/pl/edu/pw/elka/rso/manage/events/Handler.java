package pl.edu.pw.elka.rso.manage.events;


/**
 * Handle event. It is different from the listener: the listener just listen if there was an even, whereas
 * this interface react to an event.
 */
public interface Handler {
    /** Contain the method for event handling **/
    void handleEvent(Event event);
}
    