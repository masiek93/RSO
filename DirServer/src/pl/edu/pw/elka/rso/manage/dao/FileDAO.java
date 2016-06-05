package pl.edu.pw.elka.rso.manage.dao;

import pl.edu.pw.elka.rso.manage.domain.FileDTO;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for FileDTO.
 * (only a stub for now)
 */
public class FileDAO {

    /**
     * Returns all files with servers they are on.
     */
    public static Collection<FileDTO> getAll() {
        // TODO, it's only a stub
        List<FileDTO> list = new ArrayList<>(5);
        list.add(new FileDTO("a.txt", 1024, new Date(), NodeRegister.getInstance().getAliveFileNodes()));
        list.add(new FileDTO("b.txt", 1024*1024, new Date(), NodeRegister.getInstance().getAliveFileNodes()));
        list.add(new FileDTO("c.txt", 1024*5, new Date(), NodeRegister.getInstance().getAliveFileNodes()));
        list.add(new FileDTO("d.txt", 1024*1024*3, new Date(), NodeRegister.getInstance().getAliveFileNodes()));
        return list;
    }
}
