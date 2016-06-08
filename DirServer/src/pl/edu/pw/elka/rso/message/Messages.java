package pl.edu.pw.elka.rso.message;

import pl.edu.pw.elka.rso.message.data.FileSrvRegReq;

public class Messages {

    public static Message pingMsg() {
        return new Message(Type.PING);
    }

    public static Message pongMsg() {
        return new Message(Type.PONG);
    }

    public static Message srvRegReqMsg(int listeningPort) {
        return new Message(Type.SRV_REG_REQ, listeningPort);
    }

    public static Message requestIdMsg() {
        return new Message(Type.ID_REQ);
    }

    public static Message respondIdMsg(Long newId) {
        return new Message(Type.ID_RES, newId);
    }

    public static Message fileSrvRegReqMsg(int listeningPort, int fileSocketPort) {
        return new Message(Type.FILE_SRV_REG_REQ, new FileSrvRegReq(listeningPort, fileSocketPort));
    }

    public static Message srvRegRespMsg(Long serverId) {
        return new Message(Type.SRV_REG_RESP, serverId);
    }

    public static Message freeSpaceMsg(Long freeSpace) {
        return new Message(Type.FREE_SPACE, freeSpace);
    }

    public static Message fileListMsg(String[] listOfFiles) {
        return new Message(Type.LIST_OF_FILES, listOfFiles);
    }

    public static Message showIdMsg(Long idMsg) {
        return new Message(Type.ID_SHOW, idMsg);
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

}
