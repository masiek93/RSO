package pl.edu.pw.elka.rso.manage.messages;

import pl.edu.pw.elka.rso.manage.events.Event;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.message.Message;
import pl.edu.pw.elka.rso.message.Type;

public class DirSrvMessages {

    public static Message nodeTypeMsg(NodeType nodeType) {
        return new Message(Type.NODE_TYPE, nodeType);
    }

    public static Message nodeRegisterMsg(NodeRegister nodeRegister) {
        return new Message(Type.INFO, nodeRegister);
    }

    public static Message eventMessage(Event event) {
        return new Message(Type.EVENT, event);
    }

    public static Message nodeInfo(Node node) {
        return new Message(Type.INFO, node);
    }

}
