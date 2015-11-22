package catan.controllers.ctf;

import catan.controllers.util.GameTestUtil;
import catan.controllers.util.PlayTestUtil;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.types.DevelopmentCard;
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
    Map<String, Integer> usersCards = new HashMap<String, Integer>();
    ValidatableResponse currentGameDetails = null;
    Response lastApiResponse = null;
    boolean trackResources = false;
    boolean trackCards = false;

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

        if (lastApiResponse.getStatusCode() == 200) {
            getGameDetails(userName);
            String status = currentGameDetails.extract().path("status");
            if ("PLAYING".equals(status)) {
                populateTokensByMoveOrder();
            }
        }

        return this;
    }

    private void populateTokensByMoveOrder() {
        for (int i = 0; i < (Integer) currentGameDetails.extract().path("gameUsers.size"); i++) {
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
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new BuildSettlementAction(userToken, this);
    }

    public BuildCityAction BUILD_CITY(int moveOrder) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new BuildCityAction(userToken, this);
    }

    public BuildRoadAction BUILD_ROAD(int moveOrder) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new BuildRoadAction(userToken, this);
    }

    public Scenario END_TURN(int moveOrder) {
        //saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        lastApiResponse = PlayTestUtil.endTurn(userToken, gameId);
        return this;
    }

    public Scenario THROW_DICE(int moveOrder) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        lastApiResponse = PlayTestUtil.throwDice(userToken, gameId);
        return this;
    }

    public Scenario BUY_CARD(int moveOrder) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        lastApiResponse = PlayTestUtil.buyCard(userToken, gameId);
        return this;
    }

    public Scenario boughtCardIs(DevelopmentCard card) {
        lastApiResponse.then()
                .statusCode(200)
                .body("card", equalTo(card.name()));
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

    public Scenario nextRandomDevelopmentCards(List<DevelopmentCard> nextCards) {
        for (DevelopmentCard nextCard : nextCards) {
            randomUtil.setNextDevelopmentCard(nextCard);
        }

        return this;
    }

    public HexBuilder setHex(HexType hexType, Integer diceValue) {
        return new HexBuilder(this, hexType, diceValue);
    }

    private void saveUsersResourcesAndCardsValues() {
        if (!trackResources && !trackCards) {
            return;
        }

        for(int i = 1; i <= tokensByMoveOrder.size(); i++){
            getGameDetails(i);

            if (trackResources) {
                int brick = gameUser(i).getValueOf("resources.brick");
                int wood = gameUser(i).getValueOf("resources.wood");
                int sheep = gameUser(i).getValueOf("resources.sheep");
                int wheat = gameUser(i).getValueOf("resources.wheat");
                int stone = gameUser(i).getValueOf("resources.stone");

                usersResources.put("user" + i + "brick", brick);
                usersResources.put("user" + i + "wood", wood);
                usersResources.put("user" + i + "sheep", sheep);
                usersResources.put("user" + i + "wheat", wheat);
                usersResources.put("user" + i + "stone", stone);
            }

            if (trackCards) {
                int knight = gameUser(i).getValueOf("developmentCards.knight");
                int victoryPoint = gameUser(i).getValueOf("developmentCards.victoryPoint");
                int roadBuilding = gameUser(i).getValueOf("developmentCards.roadBuilding");
                int monopoly = gameUser(i).getValueOf("developmentCards.monopoly");
                int yearOfPlenty = gameUser(i).getValueOf("developmentCards.yearOfPlenty");

                usersCards.put("user" + i + "knight", knight);
                usersCards.put("user" + i + "victoryPoint", victoryPoint);
                usersCards.put("user" + i + "roadBuilding", roadBuilding);
                usersCards.put("user" + i + "monopoly", monopoly);
                usersCards.put("user" + i + "yearOfPlenty", yearOfPlenty);
            }
        }
    }

    public Scenario startTrackResourcesQuantity() {
        trackResources = true;
        return this;
    }

    public Scenario stopTrackResourcesQuantity() {
        trackResources = false;
        return this;
    }

    public Scenario startTrackDevCardsQuantity() {
        trackCards = true;
        return this;
    }

    public Scenario stopTrackDevCardsQuantity() {
        trackCards = false;
        return this;
    }
}
