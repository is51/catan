package catan.domain.exception;

public class UserException extends Exception {
    private String errorCode;

    public UserException() {
        super();
    }

    public UserException(String errorCode) {
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
