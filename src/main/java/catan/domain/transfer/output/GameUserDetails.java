package catan.domain.transfer.output;

import catan.domain.model.game.GameUserBean;

public class GameUserDetails {
    private UserDetails user;
    private int colorId;

    public GameUserDetails() {
    }

    public GameUserDetails(GameUserBean userBean) {
        this.user = new UserDetails(userBean.getUser());
        this.colorId = userBean.getColorId();
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
