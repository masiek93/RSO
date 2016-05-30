package pl.edu.pw.elka.rso.manage;

import pl.edu.pw.elka.rso.manage.util.ConfigIO;
import pl.edu.pw.elka.rso.manage.util.Config;
import pl.edu.pw.elka.rso.manage.util.DirectoryServerConf;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, JAXBException {
        System.out.println(Config.getInstance());

    }
}
