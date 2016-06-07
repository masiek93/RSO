package pl.edu.pw.elka.rso.repo.db;

import java.io.Serializable;
import java.util.List;

public class DbContainer implements Serializable{
    public List<FileInfoStorager> fileInfoStoragerTable;
	public List<FileOnFileServer> fileOnServerFileTable;
	public List<FileServer> fileServerTable;
	
    public DbContainer() {}
    public DbContainer(List<FileInfoStorager> table1, List<FileOnFileServer> table2, List<FileServer> table3) {
        this.fileInfoStoragerTable = table1;
        this.fileOnServerFileTable = table2;
        this.fileServerTable = table3;

    }

    public long totalSize() {
        return fileInfoStoragerTable.size() + fileOnServerFileTable.size() + fileServerTable.size();
    }
}