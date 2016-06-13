package catan.controllers.testcases.play;

import catan.config.ApplicationConfig;
import catan.controllers.ctf.Scenario;
import catan.controllers.util.FunctionalTestUtil;
import catan.controllers.util.PlayTestUtil;
import catan.controllers.util.RandomValueTestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class PreparationStageTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_PreparationStageTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_PreparationStageTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_PreparationStageTest";
    public static final String USER_PASSWORD_3 = "password3";

    private static boolean initialized = false;

    @Before
    public void setup() {
        if (!initialized) {
            new Scenario();
            registerUser(USER_NAME_1, USER_PASSWORD_1);
            registerUser(USER_NAME_2, USER_PASSWORD_2);
            registerUser(USER_NAME_3, USER_PASSWORD_3);
            initialized = true;
        }
    }

    private void checkAvailableForUserAction(String userToken, int gameId, int gameUserNumber, String actionCode) {
        if (actionCode.equals("")) {
            viewGame(userToken, gameId)
                    .then()
                    .statusCode(200)
                    .body("gameUsers[" + gameUserNumber + "].availableActions.list", hasSize(0));
        } else {
            viewGame(userToken, gameId)
                    .then()
                    .statusCode(200)
                    .body("gameUsers[" + gameUserNumber + "].availableActions.isMandatory", equalTo(true))
                    .body("gameUsers[" + gameUserNumber + "].availableActions.list", hasSize(1))
                    .body("gameUsers[" + gameUserNumber + "].availableActions.list.find {it.code == '" + actionCode + "'}", notNullValue());
        }
    }

    private void checkAvailableForUserActions(String userToken, int gameId, int gameUserNumber, List<String> actionCodes) {
        ValidatableResponse game = viewGame(userToken, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + gameUserNumber + "].availableActions.isMandatory", equalTo(false))
                .body("gameUsers[" + gameUserNumber + "].availableActions.list", hasSize(actionCodes.size()));

        for (String actionCode : actionCodes) {
            game.body("gameUsers[" + gameUserNumber + "].availableActions.list.find {it.code == '" + actionCode + "'}", notNullValue());
        }
    }

    private void checkDisplayedMessage(String[] userTokens, int gameId, int activeUserNumber, int notActiveUserNumber1, int notActiveUserNumber2, String message) {
        viewGame(userTokens[activeUserNumber], gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + activeUserNumber + "].displayedMessage", equalTo(message));

        viewGame(userTokens[notActiveUserNumber1], gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + notActiveUserNumber1 + "].displayedMessage", nullValue());

        viewGame(userTokens[notActiveUserNumber2], gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + notActiveUserNumber2 + "].displayedMessage", nullValue());
    }

    private void checkIfThereIsNoDisplayedMessage(String[] userTokens, int gameId) {
        for (int i = 0; i < 3; i++) {
            viewGame(userTokens[i], gameId)
                    .then()
                    .statusCode(200)
                    .body("gameUsers[" + i + "].displayedMessage", nullValue());
        }
    }

    private void checkAvailableActionsAndBuildDuringOneMove(String[] userTokens, String activeUserName, int numberOfBuilding, int gameId, int activeUserNumber, int notActiveUserNumber1, int notActiveUserNumber2, int nodeIdToBuild, String nodeBuildingAction, int edgeIdToBuild) {
        checkAvailableForUserAction(userTokens[activeUserNumber], gameId, activeUserNumber, nodeBuildingAction);
        checkAvailableForUserAction(userTokens[notActiveUserNumber1], gameId, notActiveUserNumber1, "");
        checkAvailableForUserAction(userTokens[notActiveUserNumber2], gameId, notActiveUserNumber2, "");
        String message = activeUserName + FunctionalTestUtil.GLOBAL_UNIQUE_USERNAME_SUFFIX + ", build "
                + (numberOfBuilding == 1 ? "your first " : (numberOfBuilding == 2 ? "your second " : ""))
                + (nodeBuildingAction.equals("BUILD_SETTLEMENT") ? "office" : "business centre");
        checkDisplayedMessage(userTokens, gameId, activeUserNumber, notActiveUserNumber1, notActiveUserNumber2, message);

        if (nodeBuildingAction.equals("BUILD_SETTLEMENT")) {
            buildSettlement(userTokens[activeUserNumber], gameId, nodeIdToBuild);
        } else if (nodeBuildingAction.equals("BUILD_CITY")) {
            buildCity(userTokens[activeUserNumber], gameId, nodeIdToBuild);
        }

        checkAvailableForUserAction(userTokens[activeUserNumber], gameId, activeUserNumber, "BUILD_ROAD");
        checkAvailableForUserAction(userTokens[notActiveUserNumber1], gameId, notActiveUserNumber1, "");
        checkAvailableForUserAction(userTokens[notActiveUserNumber2], gameId, notActiveUserNumber2, "");
        message = activeUserName + FunctionalTestUtil.GLOBAL_UNIQUE_USERNAME_SUFFIX  + ", build network near your "
                + (nodeBuildingAction.equals("BUILD_SETTLEMENT") ? "office" : "business centre");
        checkDisplayedMessage(userTokens, gameId, activeUserNumber, notActiveUserNumber1, notActiveUserNumber2, message);

        buildRoad(userTokens[activeUserNumber], gameId, edgeIdToBuild);

        checkAvailableForUserAction(userTokens[activeUserNumber], gameId, activeUserNumber, "END_TURN");
        checkAvailableForUserAction(userTokens[notActiveUserNumber1], gameId, notActiveUserNumber1, "");
        checkAvailableForUserAction(userTokens[notActiveUserNumber2], gameId, notActiveUserNumber2, "");
        checkIfThereIsNoDisplayedMessage(userTokens, gameId);

        endTurn(userTokens[activeUserNumber], gameId);
    }

    private void checkAvailableActionsMainStageMove(String[] userTokens, int gameId, int activeUserNumber, int notActiveUserNumber1, int notActiveUserNumber2, int nodeIdToBuild, int edgeIdToBuild) {
        List<String> availableActionsThrowDice = new ArrayList<String>(singletonList("THROW_DICE"));
        checkAvailableForUserActions(userTokens[activeUserNumber], gameId, activeUserNumber, availableActionsThrowDice);

        nextRandomDiceValues(asList(1,1));
        throwDice(userTokens[activeUserNumber], gameId);

        List<String> availableActions = new ArrayList<String>(Arrays.asList("END_TURN", "TRADE_PORT", "TRADE_PROPOSE"));

        int brickQuantity = viewGame(userTokens[activeUserNumber], gameId).path("gameUsers[" + activeUserNumber + "].resources.brick");
        int woodQuantity = viewGame(userTokens[activeUserNumber], gameId).path("gameUsers[" + activeUserNumber + "].resources.wood");
        int sheepQuantity = viewGame(userTokens[activeUserNumber], gameId).path("gameUsers[" + activeUserNumber + "].resources.sheep");
        int wheatQuantity = viewGame(userTokens[activeUserNumber], gameId).path("gameUsers[" + activeUserNumber + "].resources.wheat");
        int stoneQuantity = viewGame(userTokens[activeUserNumber], gameId).path("gameUsers[" + activeUserNumber + "].resources.stone");

        if (brickQuantity >= 1 && woodQuantity >= 1) {
            availableActions.add("BUILD_ROAD");
            if (sheepQuantity >= 1 && wheatQuantity >= 1) {
                availableActions.add("BUILD_SETTLEMENT");
            }
        }
        if (wheatQuantity >= 2 && stoneQuantity >= 3) {
            availableActions.add("BUILD_CITY");
        }
        if (sheepQuantity >= 1 && wheatQuantity >= 1 && stoneQuantity >= 1) {
            availableActions.add("BUY_CARD");
        }

        checkAvailableForUserActions(userTokens[activeUserNumber], gameId, activeUserNumber, availableActions);
        checkAvailableForUserAction(userTokens[notActiveUserNumber1], gameId, notActiveUserNumber1, "");
        checkAvailableForUserAction(userTokens[notActiveUserNumber2], gameId, notActiveUserNumber2, "");

        buildSettlement(userTokens[activeUserNumber], gameId, nodeIdToBuild);
        buildCity(userTokens[activeUserNumber], gameId, nodeIdToBuild);
        buildRoad(userTokens[activeUserNumber], gameId, edgeIdToBuild);

        checkAvailableForUserActions(userTokens[activeUserNumber], gameId, activeUserNumber, availableActions);
        checkAvailableForUserAction(userTokens[notActiveUserNumber1], gameId, notActiveUserNumber1, "");
        checkAvailableForUserAction(userTokens[notActiveUserNumber2], gameId, notActiveUserNumber2, "");

        endTurn(userTokens[activeUserNumber], gameId);
    }

    @Test
    public void should_provide_correct_available_actions_and_build_correctly() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);
        String[] userTokens = {userToken1, userToken2, userToken3};
        String[] userNames = {USER_NAME_1, USER_NAME_2, USER_NAME_3};

        int gameId = createNewGame(userToken1, false, 12, 1).path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        int gameUserNumber0MoveOrder = viewGame(userToken1, gameId).path("gameUsers[0].moveOrder");
        int gameUserNumber1MoveOrder = viewGame(userToken1, gameId).path("gameUsers[1].moveOrder");

        int firstGameUserNumber = (gameUserNumber0MoveOrder == 1) ? 0 : ((gameUserNumber1MoveOrder == 1) ? 1 : 2);
        int secondGameUserNumber = (gameUserNumber0MoveOrder == 2) ? 0 : ((gameUserNumber1MoveOrder == 2) ? 1 : 2);
        int thirdGameUserNumber = (gameUserNumber0MoveOrder == 3) ? 0 : ((gameUserNumber1MoveOrder == 3) ? 1 : 2);

        // Users shouldn't get access to other users' actions
        viewGame(userTokens[firstGameUserNumber], gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + secondGameUserNumber + "].availableActions", nullValue())
                .body("gameUsers[" + thirdGameUserNumber + "].availableActions", nullValue());
        viewGame(userTokens[secondGameUserNumber], gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + firstGameUserNumber + "].availableActions", nullValue())
                .body("gameUsers[" + thirdGameUserNumber + "].availableActions", nullValue());

        /* Achtung! Nodes and edges are correct only for currently generated map
        *     --------------
        *     | 7 |12 | 16 |
        *   -------------------
        *   | 3 | 8 | 13 | 17 |
        * -----------------------
        * | 0 | 4 | 9 | 14 | 18 |
        * -----------------------
        *   | 1 | 5 | 10 | 15 |
        *   -------------------
        *     | 2 | 6 | 11 |
        *     --------------
        */
        int nodeId1ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int edgeId1ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");
        int nodeId2ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topRightId");
        int edgeId2ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.rightId");

        int nodeId1ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].nodesIds.topLeftId");
        int edgeId1ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].edgesIds.topLeftId");
        int nodeId2ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].nodesIds.topRightId");
        int edgeId2ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].edgesIds.rightId");

        int nodeId1ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].nodesIds.topLeftId");
        int edgeId1ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].edgesIds.topLeftId");
        int nodeId2ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].nodesIds.topRightId");
        int edgeId2ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].edgesIds.rightId");

        // First player moves #1
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[firstGameUserNumber], 1, gameId, firstGameUserNumber, secondGameUserNumber, thirdGameUserNumber, nodeId1ToBuildForFirstUser, "BUILD_SETTLEMENT", edgeId1ToBuildForFirstUser);
        // Second player moves #1
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[secondGameUserNumber], 1, gameId, secondGameUserNumber, firstGameUserNumber, thirdGameUserNumber, nodeId1ToBuildForSecondUser, "BUILD_SETTLEMENT", edgeId1ToBuildForSecondUser);
        // Third player moves #1
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[thirdGameUserNumber], 1, gameId, thirdGameUserNumber, secondGameUserNumber, firstGameUserNumber, nodeId1ToBuildForThirdUser, "BUILD_SETTLEMENT", edgeId1ToBuildForThirdUser);


        // Third player moves #2

        // should_fail_if_try_to_build_settlement_on_existing_settlement
        buildSettlement(userTokens[thirdGameUserNumber], gameId, nodeId1ToBuildForThirdUser)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        // should_fail_if_try_to_build_settlement_close_to_another_settlement_less_than_2_roads
        int nodeIdClosedToPreviousSettlement = viewGame(userToken1, gameId).path("map.hexes[10].nodesIds.bottomLeftId");
        buildSettlement(userTokens[thirdGameUserNumber], gameId, nodeIdClosedToPreviousSettlement)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        // should_fail_if_node_does_not_belong_to_this_game
        int nonexistentNodeId = -1;
        buildSettlement(userTokens[thirdGameUserNumber], gameId, nonexistentNodeId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        // check if message is correct
        String message = userNames[thirdGameUserNumber] + FunctionalTestUtil.GLOBAL_UNIQUE_USERNAME_SUFFIX + ", build your second office";
        checkDisplayedMessage(userTokens, gameId, thirdGameUserNumber, secondGameUserNumber, firstGameUserNumber, message);

        // should_successfully_build_settlement_on_empty_node
        buildSettlement(userTokens[thirdGameUserNumber], gameId, nodeId2ToBuildForThirdUser)
                .then()
                .statusCode(200);
        int thirdGameUserId = viewGame(userToken1, gameId).path("gameUsers[" + thirdGameUserNumber + "].id");
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .rootPath("map.nodes.find {it.nodeId == " + nodeId2ToBuildForThirdUser + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(thirdGameUserId))
                .body("building.built", equalTo("SETTLEMENT"));

        // should_fail_if_try_to_build_road_on_existing_road
        buildRoad(userTokens[thirdGameUserNumber], gameId, edgeId1ToBuildForThirdUser)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        // should_fail_if_player_try_to_build_road_without_any_connection_to_settlement_or_city_or_road
        int edgeIdWithoutAnyConnection = viewGame(userToken1, gameId).path("map.hexes[10].edgesIds.bottomLeftId");
        buildRoad(userTokens[thirdGameUserNumber], gameId, edgeIdWithoutAnyConnection)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        // should_fail_if_player_try_to_build_road_without_any_connection_to_his_settlement_or_city_or_road_but_connected_with_opponents_settlement
        int edgeIdWithConnectionToOpponentSettlement = viewGame(userToken1, gameId).path("map.hexes[8].edgesIds.leftId");
        buildRoad(userTokens[thirdGameUserNumber], gameId, edgeIdWithConnectionToOpponentSettlement)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        // should_fail_if_player_try_to_build_road_without_any_connection_to_his_settlement_or_city_or_road_but_connected_with_opponents_road
        int edgeIdWithConnectionToOpponentRoad = viewGame(userToken1, gameId).path("map.hexes[8].edgesIds.topRightId");
        buildRoad(userTokens[thirdGameUserNumber], gameId, edgeIdWithConnectionToOpponentRoad)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        // should_fail_if_edge_does_not_belong_to_this_game
        int nonexistentEdgeId = -1;
        buildRoad(userTokens[thirdGameUserNumber], gameId, nonexistentEdgeId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        // check if message is correct
        message = userNames[thirdGameUserNumber] + FunctionalTestUtil.GLOBAL_UNIQUE_USERNAME_SUFFIX + ", build network near your office";
        checkDisplayedMessage(userTokens, gameId, thirdGameUserNumber, secondGameUserNumber, firstGameUserNumber, message);

        // should_successfully_build_road_on_empty_edge_if_has_neighbour_road_that_belongs_to_this_player
        buildRoad(userTokens[thirdGameUserNumber], gameId, edgeId2ToBuildForThirdUser)
                .then()
                .statusCode(200);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .rootPath("map.edges.find {it.edgeId == " + edgeId2ToBuildForThirdUser + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(thirdGameUserId))
                .body("building.built", equalTo("ROAD"));

        // check if message is correct
        checkIfThereIsNoDisplayedMessage(userTokens, gameId);

        // should_successfully_end_turn
        endTurn(userTokens[thirdGameUserNumber], gameId)
                .then()
                .statusCode(200);

        given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", userToken1)
                .when()
                .post(URL_CURRENT_GAMES_LIST)
                .then()
                .statusCode(200)
                .body("findall.size()", greaterThan(0));

        // should fail when end turn if current move is not move of third user
        endTurn(userTokens[thirdGameUserNumber], gameId)
                .then()
                .statusCode(400);


        // Second player moves #2
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[secondGameUserNumber], 2, gameId, secondGameUserNumber, firstGameUserNumber, thirdGameUserNumber, nodeId2ToBuildForSecondUser, "BUILD_SETTLEMENT", edgeId2ToBuildForSecondUser);
        // First player moves #2
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[firstGameUserNumber], 2, gameId, firstGameUserNumber, secondGameUserNumber, thirdGameUserNumber, nodeId2ToBuildForFirstUser, "BUILD_SETTLEMENT", edgeId2ToBuildForFirstUser);


        // MAIN STAGE

        // check if message is correct
        checkIfThereIsNoDisplayedMessage(userTokens, gameId);

        // check currentMove after the preparation stage ending
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", equalTo(1));

        int nodeId3ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.bottomId");
        int edgeId3ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.bottomLeftId");

        int nodeId3ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].nodesIds.bottomId");
        int edgeId3ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].edgesIds.bottomLeftId");

        int nodeId3ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].nodesIds.bottomId");
        int edgeId3ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].edgesIds.bottomLeftId");

        checkAvailableActionsMainStageMove(userTokens, gameId, firstGameUserNumber, secondGameUserNumber, thirdGameUserNumber, nodeId3ToBuildForFirstUser, edgeId3ToBuildForFirstUser);
        checkAvailableActionsMainStageMove(userTokens, gameId, secondGameUserNumber, firstGameUserNumber, thirdGameUserNumber, nodeId3ToBuildForSecondUser, edgeId3ToBuildForSecondUser);
        checkAvailableActionsMainStageMove(userTokens, gameId, thirdGameUserNumber, secondGameUserNumber, firstGameUserNumber, nodeId3ToBuildForThirdUser, edgeId3ToBuildForThirdUser);


        // check currentMove after one moves' cycle in the main stages
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", equalTo(1));
    }


    @Test
    public void should_provide_correct_available_actions_and_build_correctly_during_preparation_stage_buildings_set_2() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);
        String[] userTokens = {userToken1, userToken2, userToken3};
        String[] userNames = {USER_NAME_1, USER_NAME_2, USER_NAME_3};

        int gameId = createNewGame(userToken1, false, 12, 2).path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        int gameUserNumber0MoveOrder = viewGame(userToken1, gameId).path("gameUsers[0].moveOrder");
        int gameUserNumber1MoveOrder = viewGame(userToken1, gameId).path("gameUsers[1].moveOrder");

        int firstGameUserNumber = (gameUserNumber0MoveOrder == 1) ? 0 : ((gameUserNumber1MoveOrder == 1) ? 1 : 2);
        int secondGameUserNumber = (gameUserNumber0MoveOrder == 2) ? 0 : ((gameUserNumber1MoveOrder == 2) ? 1 : 2);
        int thirdGameUserNumber = (gameUserNumber0MoveOrder == 3) ? 0 : ((gameUserNumber1MoveOrder == 3) ? 1 : 2);

        /* Achtung! Nodes and edges are correct only for currently generated map
        *     --------------
        *     | 7 |12 | 16 |
        *   -------------------
        *   | 3 | 8 | 13 | 17 |
        * -----------------------
        * | 0 | 4 | 9 | 14 | 18 |
        * -----------------------
        *   | 1 | 5 | 10 | 15 |
        *   -------------------
        *     | 2 | 6 | 11 |
        *     --------------
        */
        int nodeId1ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int edgeId1ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");
        int nodeId2ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topRightId");
        int edgeId2ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.rightId");
        int nodeId3ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.bottomId");
        int edgeId3ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.bottomLeftId");

        int nodeId1ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].nodesIds.topLeftId");
        int edgeId1ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].edgesIds.topLeftId");
        int nodeId2ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].nodesIds.topRightId");
        int edgeId2ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].edgesIds.rightId");
        int nodeId3ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].nodesIds.bottomId");
        int edgeId3ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].edgesIds.bottomLeftId");

        int nodeId1ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].nodesIds.topLeftId");
        int edgeId1ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].edgesIds.topLeftId");
        int nodeId2ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].nodesIds.topRightId");
        int edgeId2ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].edgesIds.rightId");
        int nodeId3ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].nodesIds.bottomId");
        int edgeId3ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].edgesIds.bottomLeftId");

        // First player moves #1
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[firstGameUserNumber], 1, gameId, firstGameUserNumber, secondGameUserNumber, thirdGameUserNumber, nodeId1ToBuildForFirstUser, "BUILD_SETTLEMENT", edgeId1ToBuildForFirstUser);
        // Second player moves #1
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[secondGameUserNumber], 1, gameId, secondGameUserNumber, firstGameUserNumber, thirdGameUserNumber, nodeId1ToBuildForSecondUser, "BUILD_SETTLEMENT", edgeId1ToBuildForSecondUser);
        // Third player moves #1
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[thirdGameUserNumber], 1, gameId, thirdGameUserNumber, secondGameUserNumber, firstGameUserNumber, nodeId1ToBuildForThirdUser, "BUILD_SETTLEMENT", edgeId1ToBuildForThirdUser);

        // Third player moves #2
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[thirdGameUserNumber], 1, gameId, thirdGameUserNumber, secondGameUserNumber, firstGameUserNumber, nodeId2ToBuildForThirdUser, "BUILD_CITY", edgeId2ToBuildForThirdUser);
        // Second player moves #2
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[secondGameUserNumber], 1, gameId, secondGameUserNumber, firstGameUserNumber, thirdGameUserNumber, nodeId2ToBuildForSecondUser, "BUILD_CITY", edgeId2ToBuildForSecondUser);
        // First player moves #2
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[firstGameUserNumber], 1, gameId, firstGameUserNumber, secondGameUserNumber, thirdGameUserNumber, nodeId2ToBuildForFirstUser, "BUILD_CITY", edgeId2ToBuildForFirstUser);

        // First player moves #3
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[firstGameUserNumber], 2, gameId, firstGameUserNumber, secondGameUserNumber, thirdGameUserNumber, nodeId3ToBuildForFirstUser, "BUILD_SETTLEMENT", edgeId3ToBuildForFirstUser);
        // Second player moves #3
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[secondGameUserNumber], 2, gameId, secondGameUserNumber, firstGameUserNumber, thirdGameUserNumber, nodeId3ToBuildForSecondUser, "BUILD_SETTLEMENT", edgeId3ToBuildForSecondUser);
        // Third player moves #3
        checkAvailableActionsAndBuildDuringOneMove(userTokens, userNames[thirdGameUserNumber], 2, gameId, thirdGameUserNumber, secondGameUserNumber, firstGameUserNumber, nodeId3ToBuildForThirdUser, "BUILD_SETTLEMENT", edgeId3ToBuildForThirdUser);

        given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", userToken1)
                .when()
                .post(URL_CURRENT_GAMES_LIST)
                .then()
                .statusCode(200)
                .body("findall.size()", greaterThan(0));
    }

    public void nextRandomDiceValues(List<Integer> nextRandomValues) {
        for (Integer nextRandomValue : nextRandomValues) {
            RandomValueTestUtil.setNextDiceNumber(nextRandomValue);
        }
    }

}
