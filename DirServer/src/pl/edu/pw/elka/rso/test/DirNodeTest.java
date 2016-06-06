package pl.edu.pw.elka.rso.test;


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
public class DirNodeTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirNodeTest.class);

    public static void main(String[] args) throws InterruptedException, IOException {

        // setup java properties

        // run dir node client. If it wont connect then run server.
        if(args == null || args.length == 0) {
            args = new String[]{null, "1234"};
        }

        tryRunClient(args);
        NodeScreen.addLogEntry("dir node  didnt find any dir servers");
        runServer(args);

    }

    private static void runServer(String[] args) {
        ConnectionListener conList = null;
        try {

            // order of functions call is important. Dont change it
            startSubServices();

            // wylaczam tymczasowo, bo chcialbym ogladac czerwone logi. Potem bedzie mozna
            // przekierowac logowanie systemu na plik ustawiajac cos w konifugracji
            NodeScreen.setSilent(true);

            conList = new ConnectionListener(args[0], Integer.valueOf(args[1]));
            NodeScreen.addLogEntry("runnng a dir node server");
            conList.start();

            startConsole();




        } catch (IOException | MetaDataRepositoryException | JAXBException e) {
            LOGGER.error("error", e);
        }


    }

    private static void startConsole() {
        new ServerConsole().run();
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

    private static void tryRunClient(String[] args) throws InterruptedException {
        ClientListener c = new DirNodeListener(args[0], new Integer(args[1]));
        DirNodeScreen.start(new DirNodeScreenDataProvider(c));
        c.start();

        LOGGER.info("node screen is silent now");


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
