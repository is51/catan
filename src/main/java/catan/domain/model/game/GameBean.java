package catan.domain.model.game;

import catan.domain.model.user.UserBean;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "GAME")
public class GameBean {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int gameId;

    @ManyToOne
    @JoinColumn(name = "CREATOR_ID")
    private UserBean creator;

    @Column(name = "IS_PRIVATE", unique = false, nullable = false)
    private boolean privateGame;

    @Column(name = "DATE_CREATED", unique = false, nullable = false)
    private Date dateCreated;

    @Column(name = "GAME_STATUS", unique = false, nullable = false)
    private GameStatus status;

    @Column(name = "MIN_USERS", unique = false, nullable = false)
    private int minUsers;

    @Column(name = "MAX_USERS", unique = false, nullable = false)
    private int maxUsers;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "game")
    private Set<GameUserBean> gameUsers = new HashSet<GameUserBean>();

    public GameBean() {
    }

    public GameBean(UserBean creator, boolean privateGame, Date dateCreated, GameStatus status, int minUsers, int maxUsers) {
        this.creator = creator;
        this.privateGame = privateGame;
        this.dateCreated = dateCreated;
        this.status = status;
        this.minUsers = minUsers;
        this.maxUsers = maxUsers;
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
                '}';
    }
}
