package pl.edu.pw.elka.rso.manage.server;


import pl.edu.pw.elka.rso.manage.util.LongIO;
import pl.edu.pw.elka.rso.manage.util.LongIOException;

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
            idManager = new IdManager("resources/gen/serial_id.txt");
        }

        return idManager;
    }

    public synchronized Long newId() {
        Long ret = start++;
        try {
            LongIO.writeLong(serialIdFilePath, start);
        } catch (LongIOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public synchronized boolean isOk(Long id) {
        return id < start;
    }

}
