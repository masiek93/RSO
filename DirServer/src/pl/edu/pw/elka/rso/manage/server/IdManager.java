package pl.edu.pw.elka.rso.manage.server;

public class IdManager {

    private static IdManager idManager;

    private long start;

    private IdManager(long start) {
        this.start = start;
    }

    public synchronized static IdManager getInstance() {
        if(idManager == null) {
            idManager = new IdManager(0);
        }
        return idManager;
    }

    public synchronized Long newId() {
        return start++;
    }

    public synchronized boolean isOk(Long id) {
        return id < start;
    }

}
