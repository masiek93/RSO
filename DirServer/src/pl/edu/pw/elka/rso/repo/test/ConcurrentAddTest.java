package pl.edu.pw.elka.rso.repo.test;


import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.repo.MetaDataRWRespository;
import pl.edu.pw.elka.rso.repo.MetaDataRepository;
import pl.edu.pw.elka.rso.repo.MetaDataRepositoryException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConcurrentAddTest {


    private static class ConcAdder implements Runnable {

        private final List<String> filesToAdd;
        private MetaDataRepository repo;
        private int threadId;

        public ConcAdder(int id, List<String> filesToAdd) {
            this.threadId = id;
            this.filesToAdd = filesToAdd;
            try {
                this.repo = MetaDataRWRespository.getInstance();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JAXBException e) {
                e.printStackTrace();
            } catch (MetaDataRepositoryException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            for(String fileToAdd: filesToAdd) {
                try {
                    repo.addFile(fileToAdd, 10, NodeRegister.getInstance().getFileNodes());
                } catch (MetaDataRepositoryException e) {
                    System.out.printf("Thread %d could not add file because: %s\n", threadId, e.getMessage());
                }
            }
        }

    }


    public static void main(String[] args) {

        NodeRegister nodeRegister = NodeRegister.getInstance();
        for(int i = 0;i < 10; ++i) {
            nodeRegister.addNode(Node.createFileNode("localhost", (long)i, true, 1000));
        }
        List<Thread> runs = new ArrayList<>();
        List<String> filesToAdd = Arrays.asList("abcdef", "ads", "dsaf", "dsf", "qoiefj", "osdfha;l");
        for (int i = 0; i < 10; i++) {
            runs.add(new Thread(new ConcAdder(i, filesToAdd)));
            runs.get(i).start();
        }
        for(Thread t: runs) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
