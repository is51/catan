package catan.domain.transfer.output;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SessionTokenDetails {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
