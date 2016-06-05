package pl.edu.pw.elka.rso.manage.node;


import pl.edu.pw.elka.rso.manage.util.DirectoryServerConf;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Global and a single object for the whole application. It maintains the register of the nodes in the system.
 * Instead of removing dead server, it just mark them as dead.
 */
public class NodeRegister implements Serializable {


    private static NodeRegister nodeRegistery;
    private Map<Long, Node> nodes;


    private static Long tempId = 0l;

    /**
     * temporary id, in case when there is no id *
     */

    public static NodeRegister getInstance() {
        if (nodeRegistery == null) {
            nodeRegistery = new NodeRegister();
        }
        return nodeRegistery;
    }

    private NodeRegister() {
        nodes = new ConcurrentHashMap<>();
    }

    public void clear() {
        nodes.clear();
    }

    public int size() {
        return nodes.size();
    }

    public void addNode(Node node) {
        if (node.getId() == null) {
            nodes.put(tempId++, node);
        } else {
            nodes.put(node.getId(), node);
        }
    }

    public synchronized void deregisterNode(Long id) {
        Node node = nodes.get(id);
        if (node != null) {
            node.setAlive(false);
        }
    }

    public void registerNode(Node node) {
        if (nodes.containsKey(node.getId())) {
            nodes.get(node.getId()).setAlive(true);
        } else {
            nodes.put(node.getId(), node);
        }

    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }

    public Collection<Node> getAliveNodes() {
        return nodes.values().stream().filter(Node::isAlive).collect(Collectors.toList());
    }


    public void update(NodeRegister other) {
        for (Long k : other.nodes.keySet()) {
            if (!nodes.containsKey(k)) {
                nodes.put(k, other.nodes.get(k));
            } else if (!nodes.get(k).isAlive()) {
                nodes.get(k).setAlive(other.nodes.get(k).isAlive());
            }
        }
    }


    public void initFromConf(Collection<DirectoryServerConf> directoryServerList) {
        for (DirectoryServerConf directoryServerConf : directoryServerList) {
            Node node = new Node();
            node.setId(directoryServerConf.id);
            node.setAlive(true);
            node.setAddress(directoryServerConf.address);
            node.setPort(directoryServerConf.nodesManagementPort);
            node.setNodeType(NodeType.DIRECTORY_NODE);
            addNode(node);
        }
    }

    public Collection<Node> getAliveDirectoryNodes() {
        return getAliveNodes().stream().filter(n -> n.isDirectoryServer() && n.isAlive()).collect(Collectors.toList());
    }

    public Collection<Node> getDirectoryNodes() {
        return getNodes().stream().filter(Node::isDirectoryServer).collect(Collectors.toList());
    }

    public Collection<Node> getAliveFileNodes() {
        return getAliveNodes().stream().filter(n -> n.isAlive() && !n.isDirectoryServer()).collect(Collectors.toList());
    }

    public Collection<Node> getFileNodes() {
        return getNodes().stream().filter(n -> !n.isDirectoryServer()).collect(Collectors.toList());
    }
}
