package catan.domain.transfer.output.game;

import catan.domain.model.game.GameBean;
import catan.domain.transfer.output.dashboard.MapDetails;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameDetails {
    private int gameId;
    private int creatorId;
    private boolean privateGame;
    private String privateCode;
    private long dateCreated;
    private long dateStarted;
    private String status;
    private List<GameUserDetails> gameUsers;
    private int minPlayers;
    private int maxPlayers;
    private int targetVictoryPoints;
    private Integer currentMove;
    private Integer biggestArmyOwner;
    private DiceDetails dice;
    private MapDetails map;

    public GameDetails() {
    }

    public GameDetails(GameBean game, int detailsRequesterId) {
        this.gameId = game.getGameId();
        this.creatorId = game.getCreator().getId();
        this.privateGame = game.isPrivateGame();
        this.privateCode = game.isPrivateGame() ? game.getPrivateCode() : null;
        this.dateCreated = game.getDateCreated().getTime();
        this.dateStarted = game.getDateStarted() != null ? game.getDateStarted().getTime() : 0;
        this.status = game.getStatus().name();
        this.gameUsers = game.getGameUserDetails(detailsRequesterId);
        this.minPlayers = game.getMinPlayers();
        this.maxPlayers = game.getMaxPlayers();
        this.targetVictoryPoints = game.getTargetVictoryPoints();
        this.currentMove = game.getCurrentMove();
        this.biggestArmyOwner = game.getBiggestArmyOwner();
        this.dice = game.isDiceThrown() == null
                ? null
                : new DiceDetails(game.isDiceThrown(), game.calculateDiceSumValue(), game.getDiceFirstValue(), game.getDiceSecondValue());
        this.map = new MapDetails(game.getEdgeDetails(), game.getHexDetails(), game.getNodeDetails());
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

    public long getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(long dateStarted) {
        this.dateStarted = dateStarted;
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

    public Integer getCurrentMove() {
        return currentMove;
    }

    public void setCurrentMove(Integer currentMove) {
        this.currentMove = currentMove;
    }

    public Integer getBiggestArmyOwner() {
        return biggestArmyOwner;
    }

    public void setBiggestArmyOwner(Integer biggestArmyOwner) {
        this.biggestArmyOwner = biggestArmyOwner;
    }

    public DiceDetails getDice() {
        return dice;
    }

    public void setDice(DiceDetails dice) {
        this.dice = dice;
    }

    public MapDetails getMap() {
        return map;
    }

    public void setMap(MapDetails map) {
        this.map = map;
    }
}
