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
    private DevelopmentCardsDetails developmentCards;

    public GameUserDetails() {

    }

    public GameUserDetails(GameUserBean userBean, int detailsRequesterId) {
        this.user = new UserDetails(userBean.getUser());
        this.colorId = userBean.getColorId();
        this.ready = userBean.isReady();
        this.moveOrder = userBean.getMoveOrder();
        this.achievements = new AchievementsDetails(userBean.getAchievements());

        if (user.getId() == detailsRequesterId) {
            this.resources = new ResourcesDetails(userBean.getResources());
            this.developmentCards = new DevelopmentCardsDetails(userBean.getDevelopmentCards());
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

    public DevelopmentCardsDetails getDevelopmentCards() {
        return developmentCards;
    }

    public void setDevelopmentCards(DevelopmentCardsDetails developmentCards) {
        this.developmentCards = developmentCards;
    }
}
