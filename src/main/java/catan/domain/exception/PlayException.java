package catan.domain.exception;

public class PlayException extends Exception {
    private String errorCode;

    public PlayException() {
        super();
    }

    public PlayException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
