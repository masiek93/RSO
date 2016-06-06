package pl.edu.pw.elka.rso.manage.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.manage.screen.NodeScreen;

import java.io.IOException;

public class FileNodeListener extends ClientListener {

    public static final Long RETRY_PERIOD_MS = 1000l;



    public FileNodeListener(String idFilePath) {
        super(idFilePath, NodeType.FILE_NODE);

        // tutaj serwer plikow powinnien wstawic jaki ma rozmiar
        thisNode.setSize(1000);

    }


    @Override
    public void run() {
        // File Node tries to connect to any directory node every RETRY_PERIOD_MS infinitely

        setTrying(true);

        while(true) {
            try {
                pickServer();
                runner();
            } catch (InterruptedException | IOException e) {
                try {
                    Thread.sleep(RETRY_PERIOD_MS);
                    NodeScreen.addLogEntry("waiting for any dir node server");
                } catch (InterruptedException e1) {

                }
            }

        }

    }

    @Override
    protected void setUpSpecificHandlers() {
        // nothing specific for now
        // in future we might send additional stuff like making file server send files to another file server

    }


}
