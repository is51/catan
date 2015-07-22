package catan.domain.transfer.output;

import catan.domain.model.game.GameBean;

import java.util.List;

public class GameDetails {
    private int gameId;
    private int creatorId;
    private boolean privateGame;
    private String privateCode;
    private long dateCreated;
    private String status;
    private List<GameUserDetails> gameUsers;
    private int minUsers;
    private int maxUsers;

    public GameDetails() {
    }

    public GameDetails(GameBean game) {
        this.gameId = game.getGameId();
        this.creatorId = game.getCreator().getId();
        this.privateGame = game.isPrivateGame();
        this.privateCode = game.isPrivateGame() ? game.getPrivateCode() : null;
        this.dateCreated = game.getDateCreated().getTime();
        this.status = game.getStatus().toString();
        this.gameUsers = game.getGameUserDetails();
        this.minUsers = game.getMinUsers();
        this.maxUsers = game.getMaxUsers();
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
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

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<GameUserDetails> getGameUsers() {
        return gameUsers;
    }

    public void setGameUsers(List<GameUserDetails> gameUsers) {
        this.gameUsers = gameUsers;
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
}
