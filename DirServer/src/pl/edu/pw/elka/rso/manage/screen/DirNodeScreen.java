package pl.edu.pw.elka.rso.manage.screen;

import pl.edu.pw.elka.rso.manage.domain.FileDTO;
import pl.edu.pw.elka.rso.manage.node.Node;

import java.util.Optional;


/**
 * Prints DirNode status on the screen.
 */
public class DirNodeScreen extends NodeScreen {
    private final DirNodeScreenDataProvider dp;

    public static void start(DirNodeScreenDataProvider dataProvider) {
        if (instance == null) {
            instance = new DirNodeScreen(dataProvider);
            start(instance);
        }
    }

    private DirNodeScreen(DirNodeScreenDataProvider dataProvider) {
        dp = dataProvider;
    }

    @Override
    protected void printAllInfo() {
        printThisServerInfo();
        print(DASHED_LINE_THICK);
        printDirectoryServersInfo();
        print(DASHED_LINE_THICK);
        printFileServersInfo();
        print(DASHED_LINE_THICK);
        printFileList();
    }

    private void printThisServerInfo() {
        Node n = dp.getThisNode();
        print("%s Serwer Katalogowy, parametry serwera:\nID=[%d], adres=[%s], nasłuchuje na porcie: [%d]",
                // (n.isRedundant() ? "Redundantny" : "Główny"), TODO redundant or main?
                "?",
                n.getId(),
                n.getAddress(),
                n.getPort()
        );
    }

    private void printDirectoryServersInfo() {
        print("Lista aktywnych serwerów katalogowych:");
        print("%-15s | %3s | %20s | %10s |",
                "Typ",
                "ID",
                "Adres",
                "Port");
        for (Node n : dp.getOtherDirectoryServers()) {
            print("%-15s | %3d | %20s | %10d |",
                    // (n.isRedundant() ? "Redundantny" : "Główny"), TODO redundant or main?
                    "?",
                    n.getId(),
                    n.getAddress(),
                    n.getPort()
            );
        }
    }

    private void printFileServersInfo() {
        print("Lista aktywnych serwerów plikowych:");
        print("%3s | %20s | %10s |",
                "ID",
                "Adres",
                "Port");
        for (Node n : dp.getFileServers()) {
            print("%3d | %20s | %10d |",
                    n.getId(),
                    n.getAddress(),
                    n.getPort()
            );
        }
    }

    private void printFileList() {
        print("Wszystkie dostępne pliki:");
        print("%-20s | %10s | %-23s | %s",
                "Nazwa pliku",
                "Rozmiar",
                "Data utworzenia",
                "Lista serwerów plikowych");
        for (FileDTO f : dp.getAllFiles()) {
            Optional<String> serversListOptional = f.getNodes().stream()
                    .map(n -> String.valueOf(n.getId()))
                    .reduce((id1, id2) -> id1 + "," + id2);
            String serversListString = "";
            if (serversListOptional.isPresent()) {
                serversListString = serversListOptional.get();
            }
            print("%1$-20.20s | %2$10s | " + getTimestampFormat(3, true) + " | %4$s",
                    f.getFileName(),
                    humanReadableByteCount(f.getFileSize(), true),
                    f.getCreationDate(),
                    serversListString
            );
        }
    }
}
