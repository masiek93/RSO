package pl.edu.pw.elka.rso.manage.messages;

public class MessageFactory {

    public static Message newPing() {
        return new Message(Type.PING, null);
    }

    public static Message newPong() {
        return new Message(Type.PONG);
    }

    public static Message newRequestId() {
        return new Message(Type.ID_REQ);
    }

    public static Message newResponseId(Long id) {
        return new Message(Type.ID_REQ, id);
    }

    public static Message newShowId() {
        return new Message(Type.ID_SHOW);
    }

    public static Message newChangeId(Long id) {
        return new Message(Type.ID_CHANGE_ID, id);
    }

    public static Message newYesMsg() {
        return new Message(Type.SIGNAL, null, Code.YES);
    }

    public static Message newNoMsg() {
        return new Message(Type.SIGNAL, null, Code.NO);
    }

    public static Message newReady() {
        return new Message(Type.READY);
    }


}
