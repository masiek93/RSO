package pl.edu.pw.elka.rso.manage.server;


import pl.edu.pw.elka.rso.util.LongIO;
import pl.edu.pw.elka.rso.util.LongIOException;

/**
 * Manage id in the system.
 */
public class IdManager {

    private static IdManager idManager;

    private String serialIdFilePath;
    private Long start;

    private IdManager(String serialIdFilePath) {

        start = null;
        this.serialIdFilePath = serialIdFilePath;

        try {
            start = LongIO.readLong(serialIdFilePath);
        } catch (LongIOException e) {
            start = 0l;
        }

    }

    public synchronized static IdManager getInstance() {
        if(idManager == null) {
            idManager = new IdManager("resources/gen/IdSerial.txt");
        }

        return idManager;
    }

    public synchronized Long newId() {
        Long ret = start++;
        try {
            LongIO.writeLong(serialIdFilePath, start);
            // TODO: generate synchro event
        } catch (LongIOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public synchronized boolean isOk(Long id) {
        // whether this id is ok. simple method.
        return id < start;
    }

}
