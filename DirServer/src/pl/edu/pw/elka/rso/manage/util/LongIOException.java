package pl.edu.pw.elka.rso.manage.util;


public class LongIOException extends Exception {

    public String msg;
    public Exception parentExcetion;

    public LongIOException(String s) {
      this.msg = s;
    }

    public LongIOException(String s, Exception ex) {
        this.msg = s;
        this.parentExcetion= ex;
    }

}
