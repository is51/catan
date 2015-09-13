package catan.domain.transfer.output.game;

import catan.domain.model.game.GameUserBean;
import catan.domain.transfer.output.user.UserDetails;

public class GameUserDetails {
    private UserDetails user;
    private int colorId;
    private boolean ready;
    private int moveOrder;
    private AchievementsDetails achievements;
    private ResourcesDetails resources;

    public GameUserDetails() {

    }

    public GameUserDetails(GameUserBean userBean, int detailsRequesterId) {
        this.user = new UserDetails(userBean.getUser());
        this.colorId = userBean.getColorId();
        this.ready = userBean.isReady();
        this.moveOrder = userBean.getMoveOrder();
        if (user.getId() == detailsRequesterId) {
            this.achievements = new AchievementsDetails(userBean.getAchievements());
            this.resources = new ResourcesDetails(userBean.getResources());
        }
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

    public int getMoveOrder() {
        return moveOrder;
    }

    public void setMoveOrder(int moveOrder) {
        this.moveOrder = moveOrder;
    }

    public AchievementsDetails getAchievements() {
        return achievements;
    }

    public void setAchievements(AchievementsDetails achievements) {
        this.achievements = achievements;
    }

    public ResourcesDetails getResources() {
        return resources;
    }

    public void setResources(ResourcesDetails resources) {
        this.resources = resources;
    }
}
