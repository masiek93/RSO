package pl.edu.pw.elka.rso.manage.node;


import java.io.Serializable;

public class Node implements Serializable {

    private NodeType nodeType;
    private Long id;
    private boolean alive = true;
    private String address;
    private int port;

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
        return node.getPort() == this.getPort() && node.getAddress().equals(this.getAddress());
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
                '}';
    }

    public  boolean isDirectoryServer() {
        return getNodeType() == NodeType.DIRECTORY_NODE;
    }
}

