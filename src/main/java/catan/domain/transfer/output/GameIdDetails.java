package catan.domain.transfer.output;

public class GameIdDetails {
    private int gameId;

    public GameIdDetails() {
    }

    public GameIdDetails(int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
