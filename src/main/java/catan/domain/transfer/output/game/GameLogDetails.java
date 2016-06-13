package catan.domain.transfer.output.game;

import catan.domain.model.game.GameLogBean;

public class GameLogDetails {
    private int id;
    private long date;
    private String code;
    private String message;
    private boolean displayedOnTop;

    public GameLogDetails() {
    }

    public GameLogDetails(GameLogBean gameLog) {
        this.id = gameLog.getGameLogId();
        this.date = gameLog.getDate().getTime();
        this.code = gameLog.getCode().name();
        this.message = gameLog.getMessage();
        this.displayedOnTop = gameLog.isDisplayedOnTop();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDisplayedOnTop() {
        return displayedOnTop;
    }

    public void setDisplayedOnTop(boolean displayedOnTop) {
        this.displayedOnTop = displayedOnTop;
    }

}
