package catan.domain;

public class Session {
    private String token;
    private UserBean player;

    public Session(String token, UserBean player) {
        this.token = token;
        this.player = player;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserBean getPlayer() {
        return player;
    }

    public void setPlayer(UserBean player) {
        this.player = player;
    }
}
