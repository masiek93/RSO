package pl.edu.pw.elka.rso.manage.screen;

import pl.edu.pw.elka.rso.manage.client.ClientListener;
import pl.edu.pw.elka.rso.manage.dao.FileDAO;
import pl.edu.pw.elka.rso.repo.domain.FileDTO;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;

import java.util.Collection;

/**
 * Data Provider for DirNodeScreen.
 */
public class DirNodeScreenDataProvider extends NodeScreenDataProvider {

    public DirNodeScreenDataProvider(ClientListener listener) {
        super(listener);
    }

    public Collection<Node> getOtherDirectoryServers() {
        return NodeRegister.getInstance().getDirectoryNodes();
    }

    public Collection<Node> getFileServers() {
        return NodeRegister.getInstance().getAliveFileNodes();
    }

    public Collection<FileDTO> getAllFiles() {
        return FileDAO.getAll();
    }
}
