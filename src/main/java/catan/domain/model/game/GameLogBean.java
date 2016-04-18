package catan.domain.model.game;

import catan.domain.model.dashboard.types.LogCodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "CT_GAME_LOG")
public class GameLogBean {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "GAME_LOG_ID", unique = true, nullable = false)
    private Integer gameLogId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADDRESSEE", nullable = false)
    private GameUserBean gameUser;

    @Column(name = "DATE", nullable = false)
    private Date date;

    @Column(name = "CODE", nullable = false)
    private LogCodeType code;

    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @Column(name = "DISPLAYED_ON_TOP", nullable = false)
    private Boolean displayedOnTop;

    public GameLogBean() {
    }

    public GameLogBean(GameUserBean gameUser, Date date, LogCodeType code, String message, boolean displayedOnTop) {
        this.gameUser = gameUser;
        this.date = date;
        this.code = code;
        this.message = message;
        this.displayedOnTop = displayedOnTop;
    }

    public Integer getGameLogId() {
        return gameLogId;
    }

    public void setGameLogId(Integer gameLogId) {
        this.gameLogId = gameLogId;
    }

    public GameUserBean getGameUser() {
        return gameUser;
    }

    public void setGameUser(GameUserBean gameUser) {
        this.gameUser = gameUser;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LogCodeType getCode() {
        return code;
    }

    public void setCode(LogCodeType code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean isDisplayedOnTop() {
        return displayedOnTop;
    }

    public void setDisplayedOnTop(Boolean displayedOnTop) {
        this.displayedOnTop = displayedOnTop;
    }

    @Override
    public String toString() {
        return "GameUser: \n\t\t" +
                "[gameLogId:" + gameLogId +
                ", code:" + code +
                ", message:" + message +
                ", displayedOnTop: " + displayedOnTop +
               "]";
    }
}
