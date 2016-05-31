package pl.edu.pw.elka.rso.manage.events;


/**
 * Class implementing this must also implements equals and hashcode.
 */
public interface EventListener {
    /**
     * It must last for a short time. If it is a long running task it should dispatch a new thread for
     * service.
     * @param event event
     */
    public void notify(Event event);

    /**
     * Id of event listener, so it wont get an event that was sent by itself.
     * @return id of this listener. For a node handler it will be its id, for non-node listener it
     * should be less than zero.
     */
    public Long getId();

}
