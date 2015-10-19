package catan.domain.model.game;

import catan.domain.model.user.UserBean;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "GAME_USER")
public class GameUserBean {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "GAME_USER_ID", unique = true, nullable = false)
    private int gameUserId;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false, updatable = false)
    private UserBean user;

    @Column(name = "COLOR_ID", nullable = false, updatable = false)
    private int colorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GAME_ID", nullable = false)
    private GameBean game;

    @Column(name = "READY", nullable = false)
    private boolean ready;

    @Column(name = "MOVE_ORDER", unique = false, nullable = false)
    private int moveOrder;

    @Column(name = "AVAILABLE_ACTIONS", unique = false, nullable = true)
    private String actions;

    @Embedded
    private AchievementsBean achievements;

    @Embedded
    private ResourcesBean resources;

    @Embedded
    private DevelopmentCardsBean developmentCards;

    public GameUserBean() {
    }

    public GameUserBean(UserBean user, int colorId, GameBean game) {
        this.user = user;
        this.colorId = colorId;
        this.game = game;
        this.achievements = new AchievementsBean(0, 0, 0, 0, 0);
        this.resources = new ResourcesBean(0, 0, 0, 0, 0);
        this.developmentCards = new DevelopmentCardsBean(0, 0, 0, 0, 0);
    }

    public int getGameUserId() {
        return gameUserId;
    }

    public void setGameUserId(int gameUserId) {
        this.gameUserId = gameUserId;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public GameBean getGame() {
        return game;
    }

    public void setGame(GameBean game) {
        this.game = game;
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

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public AchievementsBean getAchievements() {
        return achievements;
    }

    public void setAchievements(AchievementsBean achievements) {
        this.achievements = achievements;
    }

    public ResourcesBean getResources() {
        return resources;
    }

    public void setResources(ResourcesBean resources) {
        this.resources = resources;
    }

    public DevelopmentCardsBean getDevelopmentCards() {
        return developmentCards;
    }

    public void setDevelopmentCards(DevelopmentCardsBean developmentCards) {
        this.developmentCards = developmentCards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameUserBean)) return false;

        GameUserBean that = (GameUserBean) o;

        if (colorId != that.colorId) return false;
        if (!user.equals(that.user)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + colorId;

        return result;
    }

    @Override
    public String toString() {
        return "GameUser [" +
                "gameUserId:" + gameUserId +
                ", user:" + user.getUsername() +
                ", colorId: " + colorId +
                ", gameId: " + game.getGameId() +
                ", ready: " + ready +
                ", moveOrder: " + moveOrder +
                ", actions: " + actions +
               "]";
    }
}
