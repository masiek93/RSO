package pl.edu.pw.elka.rso.manage.domain;

import pl.edu.pw.elka.rso.manage.node.Node;

import java.util.Collection;
import java.util.Date;

/**
 * Data Transfer Object for files that are stored on FileNodes.
 */
public class FileDTO {
    private String fileName;
    private long fileSize; // bytes
    private Date creationDate;
    private Collection<Node> nodes; // file nodes that contain this file

    public FileDTO(String fileName, long fileSize, Date creationDate, Collection<Node> nodes) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.creationDate = creationDate;
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

    public Collection<Node> getNodes() {
        return nodes;
    }
}
