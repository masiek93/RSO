package pl.edu.pw.elka.rso.manage.events;


public interface EventListener {
    public void notify(Event event);
    public Long getId();
}
