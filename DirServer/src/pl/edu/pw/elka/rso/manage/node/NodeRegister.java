package pl.edu.pw.elka.rso.manage.node;


import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NodeRegister implements Serializable {


    private static NodeRegister nodeRegistery;
    private Map<Long, Node> nodes;


    public static NodeRegister getInstance() {
        if(nodeRegistery == null) {
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
        nodes.put(node.getId(), node);
    }

    public synchronized void deregisterNode(Long id) {
        Node node = nodes.get(id);
        if(node != null) {
            node.setAlive(false);
        }
    }

    public void registerNode(Node node) {
        nodes.put(node.getId(), node);
    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }

    public Collection<Node> getAliveNodes() {
        return nodes.values().stream().filter(Node::isAlive).collect(Collectors.toList());
    }


}
