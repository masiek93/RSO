package pl.edu.pw.elka.rso.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class MessageInputStream {
    private final ObjectInputStream ois;

    public MessageInputStream(InputStream in) throws IOException {
        ois = new ObjectInputStream(in);
    }

    public Message readMessage() throws IOException, ClassNotFoundException {
        return (Message) ois.readObject();
    }
}
