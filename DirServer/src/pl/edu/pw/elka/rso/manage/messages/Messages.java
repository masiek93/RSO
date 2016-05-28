package pl.edu.pw.elka.rso.manage.messages;

import pl.edu.pw.elka.rso.manage.events.Event;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.node.NodeType;

public class Messages {

    public static Message pingMsg() {
        return new Message(Type.PING, null);
    }

    public static Message pongMsg() {
        return new Message(Type.PONG);
    }

    public static Message requestIdMsg() {
        return new Message(Type.ID_REQ);
    }

    public static Message responseIdMsg(Long id) {
        return new Message(Type.ID_RES, id);
    }

    public static Message showIdMsg() {
        return new Message(Type.ID_SHOW);
    }

    public static Message yesMsg() {
        return new Message(Type.SIGNAL, null, Code.YES);
    }

    public static Message noMsg() {
        return new Message(Type.SIGNAL, null, Code.NO);
    }

    public static Message readyMsg() {
        return new Message(Type.READY);
    }

    public static Message okMsg() {
        return new Message(Type.SIGNAL, Code.OK);
    }


    public static Message errorMsg() {
        return new Message(Type.SIGNAL, Code.ERROR);
    }

    public static Message nodeTypeMsg(NodeType nodeType) {
        return new Message(Type.NODE_TYPE, nodeType);
    }

    public static Message nodeRegisterMsg(NodeRegister nodeRegister) {
        return new Message(Type.INFO, nodeRegister);
    }

    public static Message eventMessage(Event event) {
        return new Message(Type.INFO, event);
    }



}
