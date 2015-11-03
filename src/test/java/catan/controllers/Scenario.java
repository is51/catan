package catan.controllers;

import catan.controllers.game.GameTestUtil;
import catan.controllers.play.PlayTestUtil;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import org.hamcrest.Matcher;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class Scenario {

    int gameId = -1;
    Map<String, String> userTokensByName = new HashMap<String, String>();
    Map<Integer, String> userTokensByMoveOrder = new HashMap<Integer, String>();
    Map<Integer, Integer> userIdsByMoveOrder = new HashMap<Integer, Integer>();
    ValidatableResponse currentGameDetails = null;
    Response lastApiResponse = null;

    public Scenario loginUser(String username, String password) {
        String userToken = GameTestUtil.loginUser(username, password);
        userTokensByName.put(username, userToken);
        return this;
    }

    public Scenario createNewPublicGameByUser(String userName) {
        String userToken = userTokensByName.get(userName);
        gameId = GameTestUtil.createNewGame(userToken, false).then().statusCode(200).extract().path("gameId");
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
            Integer moveOrder = currentGameDetails.extract().path("gameUsers[" + i + "].moveOrder");
            Integer userIdWithCurrentMoveOrder = currentGameDetails.extract().path("gameUsers[" + i + "].user.id");
            String userNameWithCurrentMoveOrder = currentGameDetails.extract().path("gameUsers[" + i + "].user.username");
            String tokenOfPlayerWithCurrentMoveOrder = userTokensByName.get(userNameWithCurrentMoveOrder);
            userTokensByMoveOrder.put(moveOrder, tokenOfPlayerWithCurrentMoveOrder);
            userIdsByMoveOrder.put(moveOrder, userIdWithCurrentMoveOrder);
        }
    }

    public Scenario getGameDetails(int moveOrder) {
        String userToken = userTokensByMoveOrder.get(moveOrder);
        currentGameDetails = GameTestUtil.viewGame(userToken, gameId).then().statusCode(200);
        return this;
    }

    public Scenario getGameDetails(String userName) {
        String userToken = userTokensByName.get(userName);
        currentGameDetails = GameTestUtil.viewGame(userToken, gameId).then().statusCode(200);
        return this;
    }

    public Scenario buildSettlement(int moveOrder, String nodePath) {
        String userToken = userTokensByMoveOrder.get(moveOrder);
        int nodeIdToBuild = GameTestUtil.viewGame(userToken, gameId).then().statusCode(200).extract().path(nodePath + ".nodeId");

        lastApiResponse = PlayTestUtil.buildSettlement(userToken, gameId, nodeIdToBuild);
        return this;
    }

    public Scenario buildSettlement(String userName, String nodePath) {
        String userToken = userTokensByName.get(userName);
        int nodeIdToBuild = GameTestUtil.viewGame(userToken, gameId).then().statusCode(200).extract().path(nodePath + ".nodeId");

        lastApiResponse = PlayTestUtil.buildSettlement(userToken, gameId, nodeIdToBuild);
        return this;
    }

    public MapValidator node(String nodePath) {
        return new MapValidator(this, currentGameDetails, nodePath);
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

    public Scenario statusIsPlaying() {
        currentGameDetails.body("status", equalTo("PLAYING"));
        return this;
    }

}
