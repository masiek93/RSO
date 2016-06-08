package pl.edu.pw.elka.rso.repo.domain;

import pl.edu.pw.elka.rso.manage.node.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for files that are stored on FileNodes.
 */
public class FileDTO implements Serializable{
    private String fileName;
    private long fileSize; // bytes
    private Date creationDate;
    private List<Node> nodes; // file nodes that contain this file
    private long fileId;
    private Date date;

    public FileDTO(String fileName, long fileSize, Date creationDate, List<Node> nodes) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.creationDate = creationDate;
        this.nodes = nodes;
    }

    public FileDTO() {
        nodes = new ArrayList<>();
    }

    public boolean add(Node node) {
        if(nodes == null) {
            nodes = new ArrayList<>();
        }
        return nodes.add(node);
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public long getFileId() {
        return fileId;
    }

    @Override
    public String toString() {
        return "FileDTO{" +
                "creationDate=" + creationDate +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", nodes=" + nodes.stream().map(node -> "\t" + node.toString() + "\n").collect(Collectors.toList()).toString() +
                ", fileId=" + fileId +
                '}';
    }

    public Node getNode(int i) {
        return nodes.get(i);
    }

    public void lock() {
        date = new Date();
    }

    public void unlock() {
        date = null;
    }

    public boolean isLocked() {
        return date != null;
    }
}
