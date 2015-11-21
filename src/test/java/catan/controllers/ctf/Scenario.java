package catan.controllers.ctf;

import catan.controllers.util.GameTestUtil;
import catan.controllers.util.PlayTestUtil;
import catan.domain.model.dashboard.types.HexType;
import catan.services.util.random.RandomUtilMock;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import org.hamcrest.Matcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class Scenario {

    RandomUtilMock randomUtil;

    int gameId = -1;
    Map<String, String> userTokensByName = new HashMap<String, String>();
    Map<Integer, String> tokensByMoveOrder = new HashMap<Integer, String>();
    Map<Integer, Integer> gameUserIdsByMoveOrder = new HashMap<Integer, Integer>();
    Map<String, Integer> usersResources = new HashMap<String, Integer>();
    ValidatableResponse currentGameDetails = null;
    Response lastApiResponse = null;
    boolean trackResources = false;

    public Scenario() {

    }

    public Scenario(RandomUtilMock randomUtil) {
        this.randomUtil = randomUtil;
    }

    public Scenario registerUser(String username, String password) {
        GameTestUtil.registerUser(username, password);
        return this;
    }

    public Scenario loginUser(String username, String password) {
        String userToken = GameTestUtil.loginUser(username, password);
        userTokensByName.put(username, userToken);
        return this;
    }

    public Scenario createNewPublicGameByUser(String userName) {
        return createNewPublicGameByUser(userName, 12);
    }

    public Scenario createNewPublicGameByUser(String userName, int targetVictoryPoints) {
        String userToken = userTokensByName.get(userName);
        gameId = GameTestUtil.createNewGame(userToken, false, targetVictoryPoints).then().statusCode(200).extract().path("gameId");
        return this;
    }

    public Scenario joinPublicGame(String userName) {
        String userToken = userTokensByName.get(userName);
        lastApiResponse = GameTestUtil.joinPublicGame(userToken, gameId);
        return this;
    }

    public Scenario setUserReady(String userName) {
        String userToken = userTokensByName.get(userName);
        lastApiResponse = GameTestUtil.setUserReady(userToken, gameId);

        if(lastApiResponse.getStatusCode() == 200){
            getGameDetails(userName);
            String status = currentGameDetails.extract().path("status");
            if("PLAYING".equals(status)){
                populateTokensByMoveOrder();
            }
        }

        return this;
    }

    private void populateTokensByMoveOrder() {
        for(int i = 0; i < (Integer) currentGameDetails.extract().path("gameUsers.size"); i++){
            Integer currentMoveOrder = currentGameDetails.extract().path("gameUsers[" + i + "].moveOrder");
            Integer gameUserIdOfPlayerWithCurrentMoveOrder = currentGameDetails.extract().path("gameUsers[" + i + "].id");
            String userNameWithCurrentMoveOrder = currentGameDetails.extract().path("gameUsers[" + i + "].user.username");
            String tokenOfUserWithCurrentMoveOrder = userTokensByName.get(userNameWithCurrentMoveOrder);
            tokensByMoveOrder.put(currentMoveOrder, tokenOfUserWithCurrentMoveOrder);
            gameUserIdsByMoveOrder.put(currentMoveOrder, gameUserIdOfPlayerWithCurrentMoveOrder);
        }
    }

    public Scenario getGameDetails(int moveOrder) {
        return getGameDetailsByToken(tokensByMoveOrder.get(moveOrder));
    }

    public Scenario getGameDetails(String userName) {
        return getGameDetailsByToken(userTokensByName.get(userName));
    }

    private Scenario getGameDetailsByToken(String userToken) {
        currentGameDetails = GameTestUtil.viewGame(userToken, gameId).then().statusCode(200);
        return this;
    }

    public BuildSettlementAction BUILD_SETTLEMENT(int moveOrder) {
        saveUsersResourcesValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new BuildSettlementAction(userToken, this);
    }

    public BuildCityAction BUILD_CITY(int moveOrder) {
        saveUsersResourcesValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new BuildCityAction(userToken, this);
    }

    public BuildRoadAction BUILD_ROAD(int moveOrder) {
        saveUsersResourcesValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new BuildRoadAction(userToken, this);
    }

    public Scenario END_TURN(int moveOrder) {
        //saveUsersResourcesValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        lastApiResponse = PlayTestUtil.endTurn(userToken, gameId);
        return this;
    }

    public Scenario THROW_DICE(int moveOrder) {
        saveUsersResourcesValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        lastApiResponse = PlayTestUtil.throwDice(userToken, gameId);
        return this;
    }

    public MapValidator node(int x, int y, String nodePosition) {
        return new MapValidator(this, x, y, nodePosition, "node");
    }

    public GameUserValidator gameUser(int moveOrder) {
        return new GameUserValidator(this, moveOrder);
    }

    public DiceValidator dice() {
        return new DiceValidator(this);
    }

    //if custom check is used several times - create a new method for it
    public Scenario check(String path, Matcher matcher) {
        currentGameDetails.body(path, matcher);
        return this;
    }

    public Scenario failsWithError(String error) {
        return failsWithError(400, error);
    }

    public Scenario failsWithError(int httpStatusCode, String error) {
        lastApiResponse.then()
                .statusCode(httpStatusCode)
                .body("errorCode", equalTo(error));
        return this;
    }

    public Scenario successfully() {
        lastApiResponse
                .then()
                .statusCode(200);
        return this;
    }

    public Scenario statusIsPlaying() {
        currentGameDetails.body("status", equalTo("PLAYING"));
        return this;
    }

    public Scenario statusIsFinished() {
        currentGameDetails.body("status", equalTo("FINISHED"));
        return this;
    }

    public Scenario and() {
        return this;
    }

    public Scenario nextRandomMoveOrderValues(List<Integer> nextRandomValues) {
        for (Integer nextRandomValue : nextRandomValues) {
            randomUtil.setNextMoveOrder(nextRandomValue);
        }

        return this;
    }

    public Scenario nextRandomDiceValues(List<Integer> nextRandomValues) {
        for (Integer nextRandomValue : nextRandomValues) {
            randomUtil.setNextDiceNumber(nextRandomValue);
        }

        return this;
    }

    public HexBuilder setHex(HexType hexType, Integer diceValue) {
        return new HexBuilder(this, hexType, diceValue);
    }

    private void saveUsersResourcesValues(){
        if(!trackResources){
            return;
        }

        getGameDetails(1);
        int p1Brick = gameUser(1).getValueOf("resources.brick");
        int p1Wood = gameUser(1).getValueOf("resources.wood");
        int p1Sheep = gameUser(1).getValueOf("resources.sheep");
        int p1Wheat = gameUser(1).getValueOf("resources.wheat");
        int p1Stone = gameUser(1).getValueOf("resources.stone");

        getGameDetails(2);
        int p2Brick = gameUser(2).getValueOf("resources.brick");
        int p2Wood = gameUser(2).getValueOf("resources.wood");
        int p2Sheep = gameUser(2).getValueOf("resources.sheep");
        int p2Wheat = gameUser(2).getValueOf("resources.wheat");
        int p2Stone = gameUser(2).getValueOf("resources.stone");

        getGameDetails(3);
        int p3Brick = gameUser(3).getValueOf("resources.brick");
        int p3Wood = gameUser(3).getValueOf("resources.wood");
        int p3Sheep = gameUser(3).getValueOf("resources.sheep");
        int p3Wheat = gameUser(3).getValueOf("resources.wheat");
        int p3Stone = gameUser(3).getValueOf("resources.stone");

        usersResources.put("p1Brick", p1Brick);
        usersResources.put("p1Wood", p1Wood);
        usersResources.put("p1Sheep", p1Sheep);
        usersResources.put("p1Wheat", p1Wheat);
        usersResources.put("p1Stone", p1Stone);
        usersResources.put("p2Brick", p2Brick);
        usersResources.put("p2Wood", p2Wood);
        usersResources.put("p2Sheep", p2Sheep);
        usersResources.put("p2Wheat", p2Wheat);
        usersResources.put("p2Stone", p2Stone);
        usersResources.put("p3Brick", p3Brick);
        usersResources.put("p3Wood", p3Wood);
        usersResources.put("p3Sheep", p3Sheep);
        usersResources.put("p3Wheat", p3Wheat);
        usersResources.put("p3Stone", p3Stone);
    }

    public Scenario startTrackResourcesQuantity() {
        trackResources = true;
        return this;
    }

    public Scenario stopTrackResourcesQuantity() {
        trackResources = false;
        return this;
    }
}
