package catan.domain.transfer.output.game;

import catan.domain.model.game.GameUserBean;
import catan.domain.transfer.output.game.actions.AvailableActionsDetails;
import catan.domain.transfer.output.user.UserDetails;
import com.google.gson.Gson;

public class GameUserDetails {
    private int id;
    private UserDetails user;
    private int colorId;
    private boolean ready;
    private int moveOrder;
    private AchievementsDetails achievements;
    private AvailableActionsDetails availableActions;
    private ResourcesDetails resources;
    private DevelopmentCardsDetails developmentCards;

    //TODO: move all GSON methods to util class
    private static final Gson GSON = new Gson();

    public GameUserDetails() {

    }

    public GameUserDetails(GameUserBean gameUserBean, int detailsRequesterId) {
        this.id = gameUserBean.getGameUserId();
        this.user = new UserDetails(gameUserBean.getUser());
        this.colorId = gameUserBean.getColorId();
        this.ready = gameUserBean.isReady();
        this.moveOrder = gameUserBean.getMoveOrder();
        this.achievements = new AchievementsDetails(gameUserBean.getAchievements());

        if (user.getId() == detailsRequesterId) {
            this.availableActions = GSON.fromJson(gameUserBean.getAvailableActions(), AvailableActionsDetails.class);
            this.resources = new ResourcesDetails(gameUserBean.getResources());
            this.developmentCards = new DevelopmentCardsDetails(gameUserBean.getDevelopmentCards());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public AvailableActionsDetails getAvailableActions() {
        return  availableActions;
    }

    public void setAvailableActions(AvailableActionsDetails availableActions) {
        this.availableActions = availableActions;
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
