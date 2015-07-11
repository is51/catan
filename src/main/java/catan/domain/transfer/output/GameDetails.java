package catan.domain.transfer.output;

public class GameDetails {
    private int gameId;
    private int creatorId;
    private boolean privateGame;
    private long dateCreated;
    private String status;

    public GameDetails() {
    }

    public GameDetails(int gameId, int creatorId, boolean privateGame, long dateCreated, String status) {
        this.gameId = gameId;
        this.creatorId = creatorId;
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
}
