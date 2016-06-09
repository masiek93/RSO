package pl.edu.pw.elka.rso.manage.node;


import java.io.Serializable;

public class Node implements Serializable {

    private NodeType nodeType;
    private Long id;
    private boolean alive;
    private String address;
    private int port;
    private long size; // free space

    public Node() {
        this.address = System.getProperty("myIp");
    }


    public static Node createFileNode(String address, Long id, boolean alive, long size) {
        return new Node(address, 0, alive, id, NodeType.FILE_NODE, size);
    }

    public static Node createDirNode(String address, int port, boolean alive, Long id) {
        return new Node(address, port, alive, id, NodeType.DIRECTORY_NODE, 0);
    }

    private Node(String address, int port, boolean alive, Long id, NodeType nodeType, long size) {
        this.address = address;
        this.alive = alive;
        this.id = id;
        this.nodeType = nodeType;
        this.port = port;
        this.size = size;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;
        return node.getPort() == this.getPort() && getAddress().equals(getAddress());
    }

    @Override
    public int hashCode() {
        int result = nodeType != null ? nodeType.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Node{" +
                "address='" + address + '\'' +
                ", nodeType=" + nodeType +
                ", id=" + id +
                ", alive=" + alive +
                ", port=" + port +
                ", size=" + size +
                '}';
    }

    public boolean isDirectoryServer() {
        return getNodeType() == NodeType.DIRECTORY_NODE;
    }

    public boolean isFileServer() {
        return getNodeType() == NodeType.FILE_NODE;
    }

    public long getSize() {
        return size;
    }

    public void updateSize(long updateValue) {
        this.size += updateValue;
    }

    public void setSize(long value) {
        this.size = value;
    }

}


