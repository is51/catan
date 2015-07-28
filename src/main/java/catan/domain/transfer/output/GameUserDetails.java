package catan.domain.transfer.output;

import catan.domain.model.game.GameUserBean;

//TODO: move to catan.domain.transfer.output.game after pull request
public class GameUserDetails {
    private UserDetails user;
    private int colorId;
    private boolean ready;

    public GameUserDetails() {
    }

    public GameUserDetails(GameUserBean userBean) {
        this.user = new UserDetails(userBean.getUser());
        this.colorId = userBean.getColorId();
        this.ready = userBean.isReady();
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

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
