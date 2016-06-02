package pl.edu.pw.elka.rso.heartBeat;


import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerDetails {

    private final ServerType serverType;
    private final int serverId;
    private final InetAddress serverAddress;
    private final int port;
    private boolean isAlive;
    private boolean isRedundant;

    private long lastTimeSeen;
    /**
     * when this server was last seen?
     **/
    private int retries;

    /**
     * number of retries to reach this server.
     **/


    public ServerDetails(int serverId, ServerType serverType, InetAddress serverAddress, int port, boolean isAlive, boolean isRedundant) {
        this.isAlive = isAlive;
        this.port = port;
        this.serverAddress = serverAddress;
        this.serverId = serverId;
        this.serverType = serverType;
        this.isRedundant = isRedundant;
        retries = 0;
    }

    public ServerDetails(int serverId, ServerType serverType, String serverAddress, int port, boolean isAlive, boolean isRedundant) throws UnknownHostException {
        this.isAlive = isAlive;
        this.port = port;
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverId = serverId;
        this.serverType = serverType;
        this.isRedundant = isRedundant;
        retries = 0;
    }


    public void refreshConnection() {
        lastTimeSeen = System.nanoTime();
        retries = 0;
    }

    /**
     * Return whether the server is alive or not.
     *
     * @return True if the server is reachable, otherwise return false.
     */
    public boolean isAlive() {
        return isAlive && retries == 0;
    }


    public void setAlive(boolean isAlive) {
        if (isAlive) {
            retries = 0;
        }
        this.isAlive = isAlive;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public int getServerId() {
        return serverId;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public boolean isRedundant() {
        return isRedundant;
    }

    public void setRedundant(boolean isRedundant) {
        this.isRedundant = isRedundant;
    }

    public long getLastTimeSeen() {
        return lastTimeSeen;
    }

    public void setLastTimeSeen(long lastTimeSeen) {
        this.lastTimeSeen = lastTimeSeen;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}