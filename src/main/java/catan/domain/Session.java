package catan.domain;

public class Session {
    private String token;
    private UserBean user;

    public Session(String token, UserBean player) {
        this.token = token;
        this.user = player;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }
}
