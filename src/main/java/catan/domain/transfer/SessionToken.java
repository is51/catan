package catan.domain.transfer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SessionToken{
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
