package catan.domain.transfer.output;

import catan.domain.model.game.GameBean;
import catan.domain.transfer.output.dashboard.DashboardDetails;

import java.util.List;

//TODO: move to catan.domain.transfer.output.game after pull request
public class GameDetails {
    private int gameId;
    private int creatorId;
    private boolean privateGame;
    private String privateCode;
    private long dateCreated;
    private String status;
    private List<GameUserDetails> gameUsers;
    private int minPlayers;
    private int maxPlayers;
    private int targetVictoryPoints;
    private DashboardDetails dashboard;

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
        this.minPlayers = game.getMinUsers();
        this.maxPlayers = game.getMaxUsers();
        this.targetVictoryPoints = game.getTargetVictoryPoints();
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

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getTargetVictoryPoints() {
        return targetVictoryPoints;
    }

    public void setTargetVictoryPoints(int targetVictoryPoints) {
        this.targetVictoryPoints = targetVictoryPoints;
    }

    public DashboardDetails getDashboard() {
        return dashboard;
    }

    public void setDashboard(DashboardDetails dashboard) {
        this.dashboard = dashboard;
    }
}
