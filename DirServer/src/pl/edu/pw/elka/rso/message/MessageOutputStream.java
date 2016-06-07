package pl.edu.pw.elka.rso.message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class MessageOutputStream {
    private final ObjectOutputStream oos;

    public MessageOutputStream(OutputStream out) throws IOException {
        oos = new ObjectOutputStream(out);
    }

    public void writeMessage(Message message) throws IOException {
        oos.writeObject(message);
    }
}
