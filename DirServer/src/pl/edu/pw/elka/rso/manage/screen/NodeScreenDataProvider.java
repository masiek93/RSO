package pl.edu.pw.elka.rso.manage.screen;

import pl.edu.pw.elka.rso.manage.client.ClientListener;
import pl.edu.pw.elka.rso.manage.node.Node;

/**
 * Abstract Data Provider that contains mutual data for DirNodeScreen and FileNodeScreen.
 */
public abstract class NodeScreenDataProvider {
    protected ClientListener listener;

    public NodeScreenDataProvider(ClientListener listener) {
        this.listener = listener;
    }

    public Node getThisNode() {
        return listener.getThisNode();
    }
}
