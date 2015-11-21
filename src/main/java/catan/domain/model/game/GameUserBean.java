package catan.domain.model.game;

import catan.domain.model.user.UserBean;

import javax.persistence.*;

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
    private String availableActions;

    @Embedded
    private Achievements achievements;

    @Embedded
    private BuildingsCount buildingsCount;

    @Embedded
    private Resources resources;

    @Embedded
    private DevelopmentCards developmentCards;

    public GameUserBean() {
    }

    public GameUserBean(UserBean user, int colorId, GameBean game) {
        this.user = user;
        this.colorId = colorId;
        this.game = game;
        this.buildingsCount = new BuildingsCount(0, 0);
        this.achievements = new Achievements(0, 0, 0, 0, 0);
        this.resources = new Resources(0, 0, 0, 0, 0);
        this.developmentCards = new DevelopmentCards(0, 0, 0, 0, 0);
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

    public String getAvailableActions() {
        return availableActions;
    }

    public void setAvailableActions(String availableActions) {
        this.availableActions = availableActions;
    }

    public Achievements getAchievements() {
        return achievements;
    }

    public void setAchievements(Achievements achievements) {
        this.achievements = achievements;
    }

    public BuildingsCount getBuildingsCount() {
        return buildingsCount;
    }

    public void setBuildingsCount(BuildingsCount buildingsCount) {
        this.buildingsCount = buildingsCount;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public DevelopmentCards getDevelopmentCards() {
        return developmentCards;
    }

    public void setDevelopmentCards(DevelopmentCards developmentCards) {
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
                ", userId:" + user.getId() +
                ", userName:" + user.getUsername() +
                ", colorId: " + colorId +
                ", gameId: " + game.getGameId() +
                ", ready: " + ready +
                ", moveOrder: " + moveOrder +
                ", availableActions: " + availableActions +
               "]";
    }
}
