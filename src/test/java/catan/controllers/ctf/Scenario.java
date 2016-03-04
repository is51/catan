package catan.controllers.ctf;

import catan.controllers.util.GameTestUtil;
import catan.controllers.util.PlayTestUtil;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.types.DevelopmentCard;
import catan.services.util.random.RandomUtilMock;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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

    /*
        DON'T FORGET TO ADD NEW FIELD TO THIS METHOD
     */
    public void cloneFrom(Scenario scenario) {
        this.randomUtil = scenario.randomUtil;
        this.gameId = scenario.gameId;
        this.userTokensByName = scenario.userTokensByName;
        this.tokensByMoveOrder = scenario.tokensByMoveOrder;
        this.gameUserIdsByMoveOrder = scenario.gameUserIdsByMoveOrder;
        this.usersResources = scenario.usersResources;
        this.usersCards = scenario.usersCards;
        this.currentGameDetails = scenario.currentGameDetails;
        this.lastApiResponse = scenario.lastApiResponse;
        this.trackResources = scenario.trackResources;
        this.trackCards = scenario.trackCards;
    }

    public Scenario() {

    }

    public Scenario(RandomUtilMock randomUtil) {
        randomUtil.resetMock();
        this.randomUtil = randomUtil;
    }


    public Set<Integer> getAllNodeIds() {
        getGameDetails(1).gameUser(1);

        Set<Integer> allNodeIds = new HashSet<Integer>();
        List<String> nodeNames = Arrays.asList("topLeft", "top", "topRight", "bottomRight", "bottom", "bottomLeft");
        List<int[]> possibleCoordinates = Arrays.asList(
                new int[]{0, -2}, new int[]{1, -2}, new int[]{2, -2},
                new int[]{-1, -1}, new int[]{0, -1}, new int[]{1, -1}, new int[]{2, -1},
                new int[]{-2, 0}, new int[]{-1, 0}, new int[]{0, 0}, new int[]{1, 0}, new int[]{2, 0},
                new int[]{-2, 1}, new int[]{-1, 1}, new int[]{0, 1}, new int[]{1, 1},
                new int[]{-2, 2}, new int[]{-1, 2}, new int[]{0, 2});

        for (int[] coordinates : possibleCoordinates) {
            for (String nodeName : nodeNames) {
                int x = coordinates[0];
                int y = coordinates[1];
                Integer nodeId = node(x, y, nodeName).getMapElementId();

                allNodeIds.add(nodeId);
            }
        }

        return allNodeIds;
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
        return createNewPublicGameByUser(userName, targetVictoryPoints, 1);
    }

    public Scenario createNewPublicGameByUser(String userName, int targetVictoryPoints, int initialBuildingSet) {
        String userToken = userTokensByName.get(userName);
        gameId = GameTestUtil.createNewGame(userToken, false, targetVictoryPoints, initialBuildingSet).then().statusCode(200).extract().path("gameId");
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

    public Scenario USE_CARD_MONOPOLY(int moveOrder, String resource) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        lastApiResponse = PlayTestUtil.useCardMonopoly(userToken, gameId, resource);
        return this;
    }

    public Scenario USE_CARD_YEAR_OF_PLENTY(int moveOrder, String firstResource, String secondResource) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        lastApiResponse = PlayTestUtil.useCardYearOfPlenty(userToken, gameId, firstResource, secondResource);
        return this;
    }

    public Scenario USE_CARD_ROAD_BUILDING(int moveOrder) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        lastApiResponse = PlayTestUtil.useCardRoadBuilding(userToken, gameId);
        return this;
    }

    public Scenario USE_CARD_KNIGHT(int moveOrder) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        lastApiResponse = PlayTestUtil.useCardKnight(userToken, gameId);
        return this;
    }

    public Scenario KICK_OFF_RESOURCES(int moveOrder, int brick, int wood, int sheep, int wheat, int stone) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        lastApiResponse = PlayTestUtil.kickOffResources(userToken, gameId, brick, wood, sheep, wheat, stone);
        return this;
    }

    public MoveRobberAction MOVE_ROBBER(int moveOrder) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new MoveRobberAction(userToken, this);
    }

    public ChoosePlayerToRobAction CHOOSE_PLAYER_TO_ROB(int moveOrder) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new ChoosePlayerToRobAction(userToken, this);
    }

    public TradeAction TRADE_PORT(int moveOrder) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new TradePortAction(userToken, this);
    }

    public TradeAction TRADE_PROPOSE(int moveOrder) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new TradeProposeAction(userToken, this);
    }

    public TradeReplyAction TRADE_ACCEPT(int moveOrder) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new TradeReplyAction(userToken, this, TradeReplyAction.ReplyType.ACCEPT);
    }

    public TradeReplyAction TRADE_DECLINE(int moveOrder) {
        saveUsersResourcesAndCardsValues();
        String userToken = tokensByMoveOrder.get(moveOrder);
        return new TradeReplyAction(userToken, this, TradeReplyAction.ReplyType.DECLINE);
    }

    public Scenario boughtCardIs(DevelopmentCard card) {
        lastApiResponse.then()
                .statusCode(200)
                .body("card", equalTo(card.name()));
        return this;
    }

    public Scenario takenResourcesQuantityIs(int takenResourcesQuantity) {
        lastApiResponse.then()
                .statusCode(200)
                .body("resourcesCount", is(takenResourcesQuantity));
        return this;
    }

    public Scenario roadsToBuildQuantityIs(int roadsCount) {
        lastApiResponse.then()
                .statusCode(200)
                .body("roadsCount", is(roadsCount));
        return this;
    }

    public MapValidator node(int x, int y, String nodePosition) {
        return new MapValidator(this, x, y, nodePosition, "node");
    }

    public MapValidator hex(int x, int y) {
        return new MapValidator(this, x, y, "hex");
    }

    public GameUserValidator gameUser(int moveOrder) {
        return new GameUserValidator(this, moveOrder);
    }

    public GameDetailsValidator game() {
        return new GameDetailsValidator(this);
    }

    public DiceValidator dice() {
        return new DiceValidator(this);
    }

    //if custom check is used several times - create a new method for it
    public Scenario customCheck(String path, Matcher matcher) {
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

    public Scenario nextRandomStolenResources(List<HexType> resources) {
        for (HexType resource : resources) {
            randomUtil.setNextStolenResource(resource);
        }

        return this;
    }

    public Scenario nextOfferIds(List<Integer> offerIds) {
        for (Integer offerId : offerIds) {
            randomUtil.setNextOfferId(offerId);
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
                int totalResources = gameUser(i).getValueOf("achievements.totalResources");

                usersResources.put("user" + i + "brick", brick);
                usersResources.put("user" + i + "wood", wood);
                usersResources.put("user" + i + "sheep", sheep);
                usersResources.put("user" + i + "wheat", wheat);
                usersResources.put("user" + i + "stone", stone);
                usersResources.put("user" + i + "totalResources", totalResources);
            }

            if (trackCards) {
                int knight = gameUser(i).getValueOf("developmentCards.knight");
                int victoryPoint = gameUser(i).getValueOf("developmentCards.victoryPoint");
                int roadBuilding = gameUser(i).getValueOf("developmentCards.roadBuilding");
                int monopoly = gameUser(i).getValueOf("developmentCards.monopoly");
                int yearOfPlenty = gameUser(i).getValueOf("developmentCards.yearOfPlenty");
                int totalCards = gameUser(i).getValueOf("achievements.totalCards");

                usersCards.put("user" + i + "knight", knight);
                usersCards.put("user" + i + "victoryPoint", victoryPoint);
                usersCards.put("user" + i + "roadBuilding", roadBuilding);
                usersCards.put("user" + i + "monopoly", monopoly);
                usersCards.put("user" + i + "yearOfPlenty", yearOfPlenty);
                usersCards.put("user" + i + "totalCards", totalCards);
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
