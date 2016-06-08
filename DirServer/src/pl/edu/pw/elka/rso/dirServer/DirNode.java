package pl.edu.pw.elka.rso.dirServer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.manage.client.ClientListener;
import pl.edu.pw.elka.rso.manage.client.DirNodeListener;
import pl.edu.pw.elka.rso.manage.events.EventBus;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.screen.DirNodeScreen;
import pl.edu.pw.elka.rso.manage.screen.DirNodeScreenDataProvider;
import pl.edu.pw.elka.rso.manage.screen.NodeScreen;
import pl.edu.pw.elka.rso.manage.server.ConnectionListener;
import pl.edu.pw.elka.rso.manage.synchro.DirNodeSynchronizer;
import pl.edu.pw.elka.rso.repo.MetaDataRWRespository;
import pl.edu.pw.elka.rso.repo.MetaDataRepository;
import pl.edu.pw.elka.rso.repo.MetaDataRepositoryException;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * Dir node: check if there is a main dir node. If there isn't any one, run
 * server instance.
 */
public class DirNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirNode.class);

    static int nodeManagementPort;
    static int clientPort;
    static String idFilePath;


    public static void main(String[] args) throws InterruptedException, IOException {

        // setup java properties

        // run dir node client. If it wont connect then run server.
        if(args.length < 3) {
            System.out.println("Error: bad arguments");
            System.out.println("Usage: DirNode idFilePath nodeManagementPort clientPort");
            System.out.println("Example: DirNode resources/gen/id.txt 1234 4321");
            System.exit(1);
        } else {
            idFilePath = args[0];
            nodeManagementPort = Integer.valueOf(args[1]);
            clientPort = Integer.valueOf(args[2]);
        }

        tryRunClient();
        NodeScreen.addLogEntry("dir node  didnt find any dir servers");
        runServer();

    }

    private static void runServer() {
        ConnectionListener conList = null;
        try {

            // order of functions call is important. Dont change it
            startSubServices();

            // wylaczam tymczasowo, bo chcialbym ogladac czerwone logi. Potem bedzie mozna
            // przekierowac logowanie systemu na plik ustawiajac cos w konifugracji
            NodeScreen.setSilent(true);
            ServerConsole serverConsole = new ServerConsole();
            new Thread(serverConsole).start();



            conList = new ConnectionListener(idFilePath, nodeManagementPort);
            NodeScreen.addLogEntry("runnng a dir node server");
            conList.start();

            // clients handler
            new ClientService(clientPort).start();

        } catch (IOException | MetaDataRepositoryException | JAXBException e) {
            LOGGER.error("error", e);
        }


    }

    private static void startSubServices() throws IOException, JAXBException, MetaDataRepositoryException {

        //
        NodeRegister.getInstance().clear();
        // repository
        MetaDataRepository rep = MetaDataRWRespository.getInstance();
        // event bus
        EventBus eventBus = EventBus.getInstance();
        // dir node synchronizer
        DirNodeSynchronizer dns = DirNodeSynchronizer.getInstance();
        //

    }

    private static void tryRunClient() throws InterruptedException {
        ClientListener c = new DirNodeListener(idFilePath, nodeManagementPort);
        DirNodeScreen.start(new DirNodeScreenDataProvider(c));
        c.start();

        LOGGER.info("node screen is silent now");
        NodeScreen.setSilent(true);

        tryToReconnect(c);
    }

    private static void tryToReconnect(ClientListener c) throws InterruptedException {
        while (c.isTrying()) {
            while (c.isConnected()) {
         //       printAliveNodes();
                Thread.sleep(1000*5);
            }
        }
    }


}
