package pl.edu.pw.elka.rso.manage.util;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

public class ConfigIO {

    private static ConfigIO configValues;
    private Marshaller marshaller;
    private Unmarshaller unMarshaller;

    public static ConfigIO getInstance() throws IOException, JAXBException {
        if(configValues == null) {
            configValues = new ConfigIO();
        }
        return configValues;
    }

    private ConfigIO() throws IOException, JAXBException {
        // create JAXB context and instantiate marshaller
        JAXBContext context = JAXBContext.newInstance(Config.class);
        marshaller = context.createMarshaller();
        unMarshaller = context.createUnmarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    }

    public void write(String filePath, Config confs) throws JAXBException {
        marshaller.marshal(confs, new File(filePath));
    }

    public Config read(String filePath) throws JAXBException {
        return (Config) unMarshaller.unmarshal(new File(filePath));
    }



}
