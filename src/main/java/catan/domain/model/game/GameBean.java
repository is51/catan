package catan.domain.model.game;

import catan.domain.model.game.types.GameStatus;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.GameUserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "GAME")
public class GameBean {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "GAME_ID", unique = true, nullable = false)
    private int gameId;

    @ManyToOne
    @JoinColumn(name = "CREATOR_ID")
    private UserBean creator;

    @Column(name = "IS_PRIVATE", unique = false, nullable = false)
    private boolean privateGame;

    @Column(name = "PRIVATE_CODE", unique = false, nullable = true)
    private String privateCode;

    @Column(name = "DATE_CREATED", unique = false, nullable = false)
    private Date dateCreated;

    @Enumerated(EnumType.STRING)
    @Column(name = "GAME_STATUS", unique = false, nullable = false)
    private GameStatus status;

    @Column(name = "MIN_USERS", unique = false, nullable = false)
    private int minUsers;

    @Column(name = "MAX_USERS", unique = false, nullable = false)
    private int maxUsers;

    @Column(name = "TARGET_VICTORY_POINTS", unique = false, nullable = false)
    private int targetVictoryPoints;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game")
    private Set<GameUserBean> gameUsers = new HashSet<GameUserBean>();

    public GameBean() {
    }

    public GameBean(UserBean creator, Date dateCreated, GameStatus status, int minUsers, int maxUsers, int targetVictoryPoints) {
        this.creator = creator;
        this.privateGame = false;
        this.dateCreated = dateCreated;
        this.status = status;
        this.minUsers = minUsers;
        this.maxUsers = maxUsers;
        this.targetVictoryPoints = targetVictoryPoints;
    }

    public GameBean(UserBean creator, String privateCode, Date dateCreated, GameStatus status, int minUsers, int maxUsers, int targetVictoryPoints) {
        this.creator = creator;
        this.privateGame = true;
        this.privateCode = privateCode;
        this.dateCreated = dateCreated;
        this.status = status;
        this.minUsers = minUsers;
        this.maxUsers = maxUsers;
        this.targetVictoryPoints = targetVictoryPoints;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }


    public UserBean getCreator() {
        return creator;
    }

    public void setCreator(UserBean creator) {
        this.creator = creator;
    }

    public boolean isPrivateGame() {
        return privateGame;
    }

    public void setPrivateGame(boolean privateGame) {
        this.privateGame = privateGame;
    }

    public String getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(String privateCode) {
        this.privateCode = privateCode;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public int getMinUsers() {
        return minUsers;
    }

    public void setMinUsers(int minUsers) {
        this.minUsers = minUsers;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Set<GameUserBean> getGameUsers() {
        return gameUsers;
    }

    public void setGameUsers(Set<GameUserBean> gameUsers) {
        this.gameUsers = gameUsers;
    }

    public int getTargetVictoryPoints() {
        return targetVictoryPoints;
    }

    public void setTargetVictoryPoints(int targetVictoryPoints) {
        this.targetVictoryPoints = targetVictoryPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameBean)) return false;

        GameBean gameBean = (GameBean) o;

        if (gameId != gameBean.gameId) return false;
        if (maxUsers != gameBean.maxUsers) return false;
        if (minUsers != gameBean.minUsers) return false;
        if (privateGame != gameBean.privateGame) return false;
        if (creator != null ? !creator.equals(gameBean.creator) : gameBean.creator != null) return false;
        if (dateCreated != null ? !dateCreated.equals(gameBean.dateCreated) : gameBean.dateCreated != null)
            return false;
        if (gameUsers != null ? !gameUsers.equals(gameBean.gameUsers) : gameBean.gameUsers != null) return false;
        if (status != gameBean.status) return false;
        if (targetVictoryPoints != gameBean.targetVictoryPoints) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gameId;
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (privateGame ? 1 : 0);
        result = 31 * result + (dateCreated != null ? dateCreated.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + minUsers;
        result = 31 * result + maxUsers;
        result = 31 * result + (gameUsers != null ? gameUsers.hashCode() : 0);
        result = 31 * result + targetVictoryPoints;
        return result;
    }

    @Override
    public String toString() {
        return "GameBean{" +
                "gameId=" + gameId +
                ", creator=" + creator +
                ", privateGame=" + privateGame +
                ", dateCreated=" + dateCreated +
                ", status=" + status +
                ", minUsers=" + minUsers +
                ", maxUsers=" + maxUsers +
                ", gameUsers=" + gameUsers +
                ", targetVictoryPoints=" + targetVictoryPoints +
                '}';
    }

    public List<GameUserDetails> getGameUserDetails() {
        List<GameUserDetails> gameUsers = new ArrayList<GameUserDetails>();

        for (GameUserBean gameUser : this.gameUsers) {
            gameUsers.add(new GameUserDetails(gameUser));
        }

        return gameUsers;
    }
}
