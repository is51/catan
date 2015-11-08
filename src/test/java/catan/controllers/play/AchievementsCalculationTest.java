package catan.controllers.play;

import catan.config.ApplicationConfig;
import catan.controllers.util.PlayTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class AchievementsCalculationTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_AchievesCalcTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_AchievesCalcTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_AchievesCalcTest";
    public static final String USER_PASSWORD_3 = "password3";

    private static boolean initialized = false;

    @Before
    public void setup() {
        if (!initialized) {
            registerUser(USER_NAME_1, USER_PASSWORD_1);
            registerUser(USER_NAME_2, USER_PASSWORD_2);
            registerUser(USER_NAME_3, USER_PASSWORD_3);
            initialized = true;
        }
    }

    @Test
    public void should_successfully_calculate_victory_points_when_build_settlement() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);
        String[] userTokens = {userToken1, userToken2, userToken3};

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

        int nodeId1ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers.achievements.displayVictoryPoints", everyItem(is(0)));

        buildSettlement(userTokens[firstGameUserNumber], gameId, nodeId1ToBuildForFirstUser)
                .then()
                .statusCode(200);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + firstGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + secondGameUserNumber + "].achievements.displayVictoryPoints", is(0))
                .body("gameUsers[" + thirdGameUserNumber + "].achievements.displayVictoryPoints", is(0));

    }

    @Test
    public void should_successfully_calculate_victory_points_when_build_city_in_preparation_stage() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);
        String[] userTokens = {userToken1, userToken2, userToken3};

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

        int nodeId1ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int edgeId1ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");

        int nodeId1ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].nodesIds.topLeftId");
        int edgeId1ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].edgesIds.topLeftId");

        int nodeId1ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].nodesIds.topLeftId");
        int edgeId1ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].edgesIds.topLeftId");
        int nodeId2ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].nodesIds.topRightId");

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers.achievements.displayVictoryPoints", everyItem(is(0)));

        // First player moves #1
        buildSettlement(userTokens[firstGameUserNumber], gameId, nodeId1ToBuildForFirstUser);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + firstGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + secondGameUserNumber + "].achievements.displayVictoryPoints", is(0))
                .body("gameUsers[" + thirdGameUserNumber + "].achievements.displayVictoryPoints", is(0));

        buildRoad(userTokens[firstGameUserNumber], gameId, edgeId1ToBuildForFirstUser);
        endTurn(userTokens[firstGameUserNumber], gameId);

        // Second player moves #1
        buildSettlement(userTokens[secondGameUserNumber], gameId, nodeId1ToBuildForSecondUser);

        viewGame(userToken1, gameId)

                .then()
                .statusCode(200)
                .body("gameUsers[" + firstGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + secondGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + thirdGameUserNumber + "].achievements.displayVictoryPoints", is(0));

        buildRoad(userTokens[secondGameUserNumber], gameId, edgeId1ToBuildForSecondUser);
        endTurn(userTokens[secondGameUserNumber], gameId);

        // Third player moves #1
        buildSettlement(userTokens[thirdGameUserNumber], gameId, nodeId1ToBuildForThirdUser);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + firstGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + secondGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + thirdGameUserNumber + "].achievements.displayVictoryPoints", is(1));

        buildRoad(userTokens[thirdGameUserNumber], gameId, edgeId1ToBuildForThirdUser);
        endTurn(userTokens[thirdGameUserNumber], gameId);

        // Third player moves #2
        buildCity(userTokens[thirdGameUserNumber], gameId, nodeId2ToBuildForThirdUser);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + firstGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + secondGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + thirdGameUserNumber + "].achievements.displayVictoryPoints", is(3));
    }

    @Test
    public void should_successfully_calculate_victory_points_when_build_city_in_main_stage() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);
        String[] userTokens = {userToken1, userToken2, userToken3};

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

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers.achievements.displayVictoryPoints", everyItem(is(0)));

        // First player moves #1
        buildSettlement(userTokens[firstGameUserNumber], gameId, nodeId1ToBuildForFirstUser);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + firstGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + secondGameUserNumber + "].achievements.displayVictoryPoints", is(0))
                .body("gameUsers[" + thirdGameUserNumber + "].achievements.displayVictoryPoints", is(0));

        buildRoad(userTokens[firstGameUserNumber], gameId, edgeId1ToBuildForFirstUser);
        endTurn(userTokens[firstGameUserNumber], gameId);

        // Second player moves #1
        buildSettlement(userTokens[secondGameUserNumber], gameId, nodeId1ToBuildForSecondUser);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + firstGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + secondGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + thirdGameUserNumber + "].achievements.displayVictoryPoints", is(0));

        buildRoad(userTokens[secondGameUserNumber], gameId, edgeId1ToBuildForSecondUser);
        endTurn(userTokens[secondGameUserNumber], gameId);

        // Third player moves #1
        buildSettlement(userTokens[thirdGameUserNumber], gameId, nodeId1ToBuildForThirdUser);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + firstGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + secondGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + thirdGameUserNumber + "].achievements.displayVictoryPoints", is(1));

        buildRoad(userTokens[thirdGameUserNumber], gameId, edgeId1ToBuildForThirdUser);
        endTurn(userTokens[thirdGameUserNumber], gameId);

        // Third player moves #2
        buildSettlement(userTokens[thirdGameUserNumber], gameId, nodeId2ToBuildForThirdUser);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + firstGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + secondGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + thirdGameUserNumber + "].achievements.displayVictoryPoints", is(2));

        buildRoad(userTokens[thirdGameUserNumber], gameId, edgeId2ToBuildForThirdUser);
        endTurn(userTokens[thirdGameUserNumber], gameId);

        // Second player moves #2
        buildSettlement(userTokens[secondGameUserNumber], gameId, nodeId2ToBuildForSecondUser);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + firstGameUserNumber + "].achievements.displayVictoryPoints", is(1))
                .body("gameUsers[" + secondGameUserNumber + "].achievements.displayVictoryPoints", is(2))
                .body("gameUsers[" + thirdGameUserNumber + "].achievements.displayVictoryPoints", is(2));

        buildRoad(userTokens[secondGameUserNumber], gameId, edgeId2ToBuildForSecondUser);
        endTurn(userTokens[secondGameUserNumber], gameId);

        // First player moves #2
        buildSettlement(userTokens[firstGameUserNumber], gameId, nodeId2ToBuildForFirstUser);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers.achievements.displayVictoryPoints", everyItem(is(2)));

        buildRoad(userTokens[firstGameUserNumber], gameId, edgeId2ToBuildForFirstUser);
        endTurn(userTokens[firstGameUserNumber], gameId);

        // MAIN STAGE STARTS
        throwDice(userTokens[firstGameUserNumber], gameId);
        buildCity(userTokens[firstGameUserNumber], gameId, nodeId1ToBuildForFirstUser);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[" + firstGameUserNumber + "].achievements.displayVictoryPoints", is(3))
                .body("gameUsers[" + secondGameUserNumber + "].achievements.displayVictoryPoints", is(2))
                .body("gameUsers[" + thirdGameUserNumber + "].achievements.displayVictoryPoints", is(2));
    }

}
