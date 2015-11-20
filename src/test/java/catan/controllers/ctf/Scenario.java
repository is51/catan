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
    ValidatableResponse currentGameDetails = null;
    Response lastApiResponse = null;

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

    public BuildSettlementAction buildSettlement(int moveOrder) {
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new BuildSettlementAction(userToken, this);
    }

    public BuildCityAction buildCity(int moveOrder) {
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new BuildCityAction(userToken, this);
    }

    public BuildRoadAction buildRoad(int moveOrder) {
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new BuildRoadAction(userToken, this);
    }

    public Scenario endTurn(int moveOrder) {
        String userToken = tokensByMoveOrder.get(moveOrder);
        lastApiResponse = PlayTestUtil.endTurn(userToken, gameId);
        return this;
    }

    public Scenario throwDice(int moveOrder) {
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

    public HexBuilder setHex(HexType hexType, int diceValue) {
        return new HexBuilder(this, hexType, diceValue);
    }

}
