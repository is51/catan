package catan.domain.transfer.output;

import java.util.List;

public class GameDetails {
    private int gameId;
    private int creatorId;
    private boolean privateGame;
    private String privateCode;
    private long dateCreated;
    private String status;
    private List<GameUserDetails> gameUsers;

    public GameDetails() {
    }

    public GameDetails(int gameId, int creatorId, boolean privateGame, String privateCode, long dateCreated, String status, List<GameUserDetails> gameUsers) {
        this.gameId = gameId;
        this.creatorId = creatorId;
        this.privateGame = privateGame;
        this.privateCode = privateCode;
        this.dateCreated = dateCreated;
        this.status = status;
        this.gameUsers = gameUsers;
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
}
