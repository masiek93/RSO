package pl.edu.pw.elka.rso.manage.messages;


public enum Type {

    SIGNAL,       // signal for server or client that something happend
    INFO,         // info has no answer

    PING,       // ping
    PONG,       // pong

    ID_REQ,     // request id
    NODE_TYPE, // type of server

    READY, ID_SHOW, ID_CHANGE_ID, ID_RES

}
