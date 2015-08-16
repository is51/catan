package catan.domain.exception;

public class GameException extends Exception {
    private String errorCode;

    public GameException() {
        super();
    }

    public GameException(String errorCode) {
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
