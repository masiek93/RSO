package pl.edu.pw.elka.rso.dirServer;


public class SystemException extends Exception {
    ErrorCode errorCode;
    String message;

    public SystemException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public SystemException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}