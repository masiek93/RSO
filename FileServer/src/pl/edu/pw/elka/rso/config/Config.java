package pl.edu.pw.elka.rso.config;

import pl.edu.pw.elka.rso.heartBeat.ServerDetails;
import pl.edu.pw.elka.rso.heartBeat.ServerType;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Config {
    private static final String DIRECTORY_SERVERS_LIST_PROPERTY = "directory_servers_list";
    private static final String FILE_SERVERS_LIST_PROPERTY = "file_servers_list";
    private static final String SERVERS_LIST_SPLIT_CHAR = "\\|"; // regexp special character 'or'
    private static final String SERVER_DETAILS_SPLIT_CHAR = ";";
    private static final int INIT_MAIN_DIRECTORY_SERVER_ID = 1;

    private ServerDetails thisServerDetails;

    private List<ServerDetails> directoryServers;
    private List<ServerDetails> fileServers;

    public static Config INSTANCE;

    public static void init(String[] args) throws IOException {
        if (INSTANCE != null) {
            throw new IllegalStateException("Config can be initialized only once!");
        }
        INSTANCE = new Config(args);
    }

    private Config(String[] args) throws IOException {
        if (args.length != 1 && args.length != 2) {
            System.err.println("Usage: 'program' config_full_path [this_server_id]");
            System.err.println("   If 'this_server_id' is not there, a client instance will be created instead.");
            System.exit(-1);
        }

        String configFilePath = args[0];

        Properties properties = new Properties();
        properties.load(new FileInputStream(configFilePath));
        directoryServers = getServersDetails(properties.getProperty(DIRECTORY_SERVERS_LIST_PROPERTY), ServerType.DIRECTORY_SERVER);
        fileServers = getServersDetails(properties.getProperty(FILE_SERVERS_LIST_PROPERTY), ServerType.FILE_SERVER);

        if (args.length == 2) {
            int thisServerId = Integer.valueOf(args[1]);
            tryToSetThisServer(thisServerId, directoryServers);
            tryToSetThisServer(thisServerId, fileServers);
            if (!isServer()) {
                System.err.println("Given serverId is not on the servers list!");
                System.exit(-1);
            }
        }
    }

    private void tryToSetThisServer(int thisServerId, List<ServerDetails> servers) {
        for (ServerDetails server : servers) {
            if (server.getServerId() == thisServerId) {
                thisServerDetails = server;
                servers.remove(server);
                break;
            }
        }
    }

    public boolean isClient() {
        return !isServer();
    }

    public boolean isServer() {
        return thisServerDetails != null;
    }

    public boolean isDirectoryServer() {
        return isServer() && thisServerDetails.getServerType() == ServerType.DIRECTORY_SERVER;
    }

    public boolean isTestFileServer() {
        return isServer() && thisServerDetails.getServerType() == ServerType.FILE_SERVER;
    }

    private List<ServerDetails> getServersDetails(String serversString, ServerType serverType) throws UnknownHostException {
        List<ServerDetails> serversList = new ArrayList<>();
        String[] serversStrings = serversString.split(SERVERS_LIST_SPLIT_CHAR);
        for (String serverString : serversStrings) {
            serversList.add(getServerDetails(serverString, serverType));
        }
        serversList.sort((s1, s2) -> Integer.compare(s1.getServerId(), s2.getServerId())); // important! less ids are taken first in loops
        return serversList;
    }

    private ServerDetails getServerDetails(String serverString, ServerType serverType) throws UnknownHostException {
        String[] serverDetailsStrings = serverString.split(SERVER_DETAILS_SPLIT_CHAR);

        int serverId = Integer.valueOf(serverDetailsStrings[0]);
        String serverAddress = serverDetailsStrings[1];
        int serverPort = Integer.valueOf(serverDetailsStrings[2]);

        boolean isAlive = false; // assuming nobody is alive at the moment
        boolean isRedundant = serverId != INIT_MAIN_DIRECTORY_SERVER_ID; // TODO check if main directory server is already running and possibly elect new one proportional to ID

        return new ServerDetails(serverId, serverType, serverAddress, serverPort, isAlive, isRedundant);
    }

    public ServerDetails getThisServerDetails() {
        return thisServerDetails;
    }

    public List<ServerDetails> getDirectoryServers() {
        return directoryServers;
    }


    public List<ServerDetails> getFileServers() {
        return fileServers;
    }

}