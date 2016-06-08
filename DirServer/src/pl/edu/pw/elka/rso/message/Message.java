package pl.edu.pw.elka.rso.message;

import java.io.Serializable;

public class Message implements Serializable {

    private Type type;
    private Object data; // must be serializable
    private Code code;

    public Message(Type type, Object data, Code code) {
        this.code = code;
        this.data = data;
        this.type = type;
    }

    public Message() {
    }

    public Message(Type type, Object data) {
        this(type, data, Code.OK);
    }

    public Message(Type type) {
        this(type, null, Code.OK);
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isOk() {
        return code == Code.OK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;

        Message message = (Message) o;

        if (code != message.code) return false;
        if (data != null ? !data.equals(message.data) : message.data != null) return false;
        if (type != message.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "Message{" +
                "code=" + code +
                ", type=" + type +
                ", data=" + data +
                '}';
    }
}
