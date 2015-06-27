package catan.domain.transfer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorDetails{
    private String errorCode;

    public ErrorDetails() {
        super();
    }

    public ErrorDetails(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
