package pl.edu.pw.elka.rso.manage.util;


import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.ArrayList;

@XmlRootElement
public class Config {

    // only objects  annotated with XmlRootElement can be serialized
    public ArrayList<DirectoryServerConf> directoryServerList = new ArrayList<>();


    // static fields, not serialized
    private static Config config;
    private static String configFile = "resources/config.xml";


    public void Config() {

    }

    public static Config getInstance() throws IOException, JAXBException {
        if(config == null) {
            config =  ConfigIO.getInstance().read(configFile);
        }
        return config;
    }


    public void updateFile() throws IOException, JAXBException {
        ConfigIO.getInstance().write(configFile, this);
    }

    public static Config load(String configFile) throws IOException, JAXBException {
        return ConfigIO.getInstance().read(configFile);
    }


    public static Config load() throws IOException, JAXBException {
        return ConfigIO.getInstance().read(configFile);
    }


    public boolean addDirectorySever(DirectoryServerConf directoryServerConf) {
        return directoryServerList.add(directoryServerConf);
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "directoryServerInfoList=" + directoryServerList +
                '}';
    }
}
