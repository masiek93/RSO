package pl.edu.pw.elka.rso.config;


public class DirectoryServerConf {

    public Long id;
    public String address;
    public int nodesManagementPort;
    public int synchronizationPort;
    public int clientPort;

    @Override
    public String toString() {
        return "DirectoryServerConf{" +
                "address='" + address + '\'' +
                ", nodesManagementPort=" + nodesManagementPort +
                ", synchronizationPort=" + synchronizationPort +
                ", clientPort=" + clientPort +
                '}';
    }
}
