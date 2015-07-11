package catan.domain.transfer.output;

public class SessionTokenDetails {
    private String token;

    public SessionTokenDetails() {
    }

    public SessionTokenDetails(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
