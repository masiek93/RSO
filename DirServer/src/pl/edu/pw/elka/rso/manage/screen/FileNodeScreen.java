package pl.edu.pw.elka.rso.manage.screen;

import pl.edu.pw.elka.rso.manage.node.Node;


/**
 * Prints FileNode status on the screen.
 */
public class FileNodeScreen extends NodeScreen {
    private final FileNodeScreenDataProvider dp;

    public static void start(FileNodeScreenDataProvider dataProvider) {
        if (instance == null) {
            instance = new FileNodeScreen(dataProvider);
            start(instance);
        }
    }

    private FileNodeScreen(FileNodeScreenDataProvider dataProvider) {
        dp = dataProvider;
    }

    @Override
    protected void printAllInfo() {
        printThisServerInfo();
        // TODO
    }

    private void printThisServerInfo() {
        Node n = dp.getThisNode();
        print("%s Serwer Plikowy, parametry serwera:\nID=[%d], adres=[%s], nasłuchuje na porcie: [%d]",
                // (n.isRedundant() ? "Redundantny" : "Główny"), TODO redundant or main?
                "?",
                n.getId(),
                n.getAddress(),
                n.getPort()
        );
    }
}
