package pl.edu.pw.elka.rso.message;


public enum Type {
    PING, PONG, FILE_SRV_REG_REQ, SRV_REG_RESP, FREE_SPACE, LIST_OF_FILES, SRV_REG_REQ,
    SIGNAL,       // signal for server or client that something happend
    INFO,         // info has no answer

    ID_REQ,     // request id
    NODE_TYPE, // type of server

    READY, ID_SHOW, ID_CHANGE_ID, EVENT, ID_RES
}
