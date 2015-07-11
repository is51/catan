package catan.domain.transfer.output;

public class GameUserDetails {
    private UserDetails user;
    private int colorId;

    public GameUserDetails() {
    }

    public GameUserDetails(UserDetails user, int colorId) {
        this.user = user;
        this.colorId = colorId;
    }

    public UserDetails getUser() {
        return user;
    }

    public void setUser(UserDetails user) {
        this.user = user;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }
}
