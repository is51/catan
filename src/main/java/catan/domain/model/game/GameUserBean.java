package catan.domain.model.game;

import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.user.UserBean;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CT_GAME_USER")
public class GameUserBean {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "GAME_USER_ID", unique = true, nullable = false)
    private Integer gameUserId;

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

    @Column(name = "AVAILABLE_ACTIONS", unique = false, nullable = true, length = 1000)
    private String availableActions;

    @Column(name = "MANDATORY_KICK_OFF_RESOURCES", unique = false, nullable = true)
    private Boolean kickingOffResourcesMandatory;

    @Column(name = "AVAILABLE_TRADE_REPLY", unique = false)
    private Boolean availableTradeReply;

    @Column(name = "DISPLAYED_MESSAGE")
    private String displayedMessage;

    @Embedded
    private Achievements achievements;

    @Embedded
    private BuildingsCount buildingsCount;

    @Embedded
    private Resources resources;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "knight", column = @Column(name = "DEV_CARD_ALL_KNIGHT")),
            @AttributeOverride(name = "victoryPoint", column = @Column(name = "DEV_CARD_ALL_VICTORY_POINT")),
            @AttributeOverride(name = "roadBuilding", column = @Column(name = "DEV_CARD_ALL_ROAD_BUILDING")),
            @AttributeOverride(name = "monopoly", column = @Column(name = "DEV_CARD_ALL_MONOPOLY")),
            @AttributeOverride(name = "yearOfPlenty", column = @Column(name = "DEV_CARD_ALL_YEAR_OF_PLENTY"))
    })
    private DevelopmentCards developmentCards;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "knight", column = @Column(name = "DEV_CARD_READY_KNIGHT")),
            @AttributeOverride(name = "victoryPoint", column = @Column(name = "DEV_CARD_READY_VICTORY_POINT")),
            @AttributeOverride(name = "roadBuilding", column = @Column(name = "DEV_CARD_READY_ROAD_BUILDING")),
            @AttributeOverride(name = "monopoly", column = @Column(name = "DEV_CARD_READY_MONOPOLY")),
            @AttributeOverride(name = "yearOfPlenty", column = @Column(name = "DEV_CARD_READY_YEAR_OF_PLENTY"))
    })
    private DevelopmentCards developmentCardsReadyForUsing;

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
        this.developmentCardsReadyForUsing = new DevelopmentCards(0, 0, 0, 0, 0);
    }

    public Integer getGameUserId() {
        return gameUserId;
    }

    public void setGameUserId(Integer gameUserId) {
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

    public Boolean isKickingOffResourcesMandatory() {
        return kickingOffResourcesMandatory;
    }

    public void setKickingOffResourcesMandatory(Boolean kickingOffResourcesMandatory) {
        this.kickingOffResourcesMandatory = kickingOffResourcesMandatory;
    }

    public Boolean isAvailableTradeReply() {
        return availableTradeReply;
    }

    public void setAvailableTradeReply(Boolean availableTradeReply) {
        this.availableTradeReply = availableTradeReply;
    }

    public String getDisplayedMessage() {
        return displayedMessage;
    }

    public void setDisplayedMessage(String displayedMessage) {
        this.displayedMessage = displayedMessage;
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

    public DevelopmentCards getDevelopmentCardsReadyForUsing() {
        return developmentCardsReadyForUsing;
    }

    public void setDevelopmentCardsReadyForUsing(DevelopmentCards developmentCardsReadyForUsing) {
        this.developmentCardsReadyForUsing = developmentCardsReadyForUsing;
    }

    public Set<NodePortType> fetchAvailablePorts() {
        Set<NodePortType> portsAvailableForGameUser = new HashSet<NodePortType>();
        for (NodeBean node : this.getGame().getNodes()) {
            if (!node.getPort().equals(NodePortType.NONE) && node.getBuilding() != null && this.equals(node.getBuilding().getBuildingOwner())) {
                portsAvailableForGameUser.add(node.getPort());
            }
        }

        return portsAvailableForGameUser;
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
        return "GameUser: \n\t\t" +
                "[gameUserId:" + gameUserId +
                ", userId:" + user.getId() +
                ", userName:" + user.getUsername() +
                ", colorId: " + colorId +
                ", gameId: " + game.getGameId() +
                ", ready: " + ready +
                ", moveOrder: " + moveOrder +
                ",\n\t\tresources: " + resources +
                ",\n\t\tavailableActions: " + availableActions +
               "]";
    }
}
