package catan.exception;

public class UserException extends Exception{
    private String errorCode;

    public UserException(String description, String errorCode) {
        super(description);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
