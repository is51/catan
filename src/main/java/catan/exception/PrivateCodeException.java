package catan.exception;

public class PrivateCodeException extends Exception{
    private String errorCode;

    public PrivateCodeException() {
        super();
    }

    public PrivateCodeException(String errorCode) {
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
