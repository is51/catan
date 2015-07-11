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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "GAME")
public class GameBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gameUserId", cascade = CascadeType.ALL)
    private List<GameUserBean> gameUsers = new ArrayList<GameUserBean>();

    public GameBean() {
    }

    public GameBean(UserBean creator, boolean privateGame, Date dateCreated, GameStatus status) {
        this.creator = creator;
        this.privateGame = privateGame;
        this.dateCreated = dateCreated;
        this.status = status;
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

    public List<GameUserBean> getGameUsers() {
        return gameUsers;
    }

    public void setGameUsers(List<GameUserBean> gameUsers) {
        this.gameUsers = gameUsers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + gameId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof GameBean))
            return false;
        GameBean other = (GameBean) obj;
        if (gameId != other.gameId)
            return false;
        if (privateGame != other.privateGame)
            return false;
        if (status != other.status)
            return false;
        if (dateCreated == null) {
            if (other.dateCreated != null)
                return false;
        } else if (!dateCreated.equals(other.dateCreated))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserBean [gameId=" + gameId + ", privateGame=" + privateGame + ", dateCreated=" + dateCreated +
                ", creator=" + creator + ", status=" + status + "]";
    }
}
