package pl.edu.pw.elka.rso.manage.client;


import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeType;

import java.io.IOException;

public class FileNodeListener extends ClientListener {

    public static final Long RETRY_PERIOD_MS = 1000l;

    public FileNodeListener(String idFilePath) {
        super(idFilePath, NodeType.FILE_NODE);
    }


    @Override
    public void run() {
        // File Node tries to connect to any directory node every RETRY_PERIOD_MS
        // while the application server is running.
        setTrying(true);
        while(true) {
            try {

                pickServer();
                runner();

            } catch (InterruptedException | IOException e) {
                try {
                    Thread.sleep(RETRY_PERIOD_MS);
                    System.out.println("waiting for any dir node server");
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }


        }

    }

    @Override
    protected void setUpSpecificHandlers() {
        // nothing specific
    }


}
