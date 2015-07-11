package catan.domain.model.game;

import catan.domain.model.user.UserBean;

import javax.persistence.Column;
import javax.persistence.Entity;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gameUserId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserBean user;

    @Column(name = "COLOR_ID", unique = false, nullable = false)
    private int colorId;

    public GameUserBean() {
    }

    public GameUserBean(UserBean user, int colorId) {
        this.user = user;
        this.colorId = colorId;
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
}
