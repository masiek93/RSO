package pl.edu.pw.elka.rso.manage.events;


/**
 *
 * Event listener is implemented by a class that is interested in receiving
 * events from the event bus. The implementing class must also implements equals and hashcode.
 */
public interface EventListener {

    /**
     * A short task (it can save the event in a queue for later processing)
     */
    public void notify(Event event);


    /**
     * A unique id for this listener. It is used to identify the receivers of the event.
     * @return
     */
    public Long getId();

}
