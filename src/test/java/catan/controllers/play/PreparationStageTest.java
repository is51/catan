package catan.controllers.play;

import catan.config.ApplicationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.*;

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
            registerUser(USER_NAME_1, USER_PASSWORD_1);
            registerUser(USER_NAME_2, USER_PASSWORD_2);
            registerUser(USER_NAME_3, USER_PASSWORD_3);
            initialized = true;
        }
    }

    protected void checkAvailableForUserAction(String userToken, int gameId, int gameUserNumber, String action) {
        if (action.equals("")) {
            viewGame(userToken, gameId)
                    .then()
                    .statusCode(200)
                    .body("gameUsers[" + gameUserNumber + "].actions.list", hasSize(0));
        } else {
            viewGame(userToken, gameId)
                    .then()
                    .statusCode(200)
                    .body("gameUsers[" + gameUserNumber + "].actions.isMandatory", equalTo(true))
                    .body("gameUsers[" + gameUserNumber + "].actions.list", hasSize(1))
                    .body("gameUsers[" + gameUserNumber + "].actions.list", hasItemInArray(hasEntry("code", action)));
        }
    }

    protected void checkAvailableForUserActionsDuringMove(String[] userTokens, int gameId, int activeUserNumber, int notActiveUserNumber1, int notActiveUserNumber2, int nodeIdToBuild, String nodeBuildingAction, int edgeIdToBuild) {
        checkAvailableForUserAction(userTokens[activeUserNumber], gameId, activeUserNumber, nodeBuildingAction);
        checkAvailableForUserAction(userTokens[notActiveUserNumber1], gameId, notActiveUserNumber1, "");
        checkAvailableForUserAction(userTokens[notActiveUserNumber2], gameId, notActiveUserNumber2, "");

        if (nodeBuildingAction.equals("BUILD_SETTLEMENT")) {
            buildSettlement(userTokens[activeUserNumber], gameId, nodeIdToBuild);
        }
        // uncomment when buildCity is implemented
        /*else if (nodeBuildingAction.equals("BUILD_CITY")) {
            buildCity(userTokens[activeUserNumber], gameId, nodeIdToBuild);
        }*/

        checkAvailableForUserAction(userTokens[activeUserNumber], gameId, activeUserNumber, "BUILD_ROAD");
        checkAvailableForUserAction(userTokens[notActiveUserNumber1], gameId, notActiveUserNumber1, "");
        checkAvailableForUserAction(userTokens[notActiveUserNumber2], gameId, notActiveUserNumber2, "");

        buildRoad(userTokens[activeUserNumber], gameId, edgeIdToBuild);

        checkAvailableForUserAction(userTokens[activeUserNumber], gameId, activeUserNumber, "END_TURN");
        checkAvailableForUserAction(userTokens[notActiveUserNumber1], gameId, notActiveUserNumber1, "");
        checkAvailableForUserAction(userTokens[notActiveUserNumber2], gameId, notActiveUserNumber2, "");

        endTurn(userTokens[activeUserNumber], gameId);
    }

    @Test
    public void should_provide_correct_available_actions_during_preparation_stage_if_initial_buildings_set_id_is_1() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);
        String[] userTokens = {userToken1, userToken2, userToken3};

        int gameId = createNewGame(userToken1, false, 12, 1).path("gameId");

        // nodes and edges are correct only for currently generated map

        int nodeId1ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int edgeId1ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");
        int nodeId2ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topRightId");
        int edgeId2ToBuildForFirstUser = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.right");

        int nodeId1ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].nodesIds.topLeftId");
        int edgeId1ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].edgesIds.topLeftId");
        int nodeId2ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].nodesIds.topRightId");
        int edgeId2ToBuildForSecondUser = viewGame(userToken1, gameId).path("map.hexes[8].edgesIds.right");

        int nodeId1ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].nodesIds.topLeftId");
        int edgeId1ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].edgesIds.topLeftId");
        int nodeId2ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].nodesIds.topRightId");
        int edgeId2ToBuildForThirdUser = viewGame(userToken1, gameId).path("map.hexes[10].edgesIds.right");

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

        // First player moves #1
        checkAvailableForUserActionsDuringMove(userTokens, gameId, firstGameUserNumber, secondGameUserNumber, thirdGameUserNumber, nodeId1ToBuildForFirstUser, "BUILD_SETTLEMENT", edgeId1ToBuildForFirstUser);
        // Second player moves #1
        checkAvailableForUserActionsDuringMove(userTokens, gameId, secondGameUserNumber, firstGameUserNumber, thirdGameUserNumber, nodeId1ToBuildForSecondUser, "BUILD_SETTLEMENT", edgeId1ToBuildForSecondUser);
        // Third player moves #1
        checkAvailableForUserActionsDuringMove(userTokens, gameId, thirdGameUserNumber, secondGameUserNumber, firstGameUserNumber, nodeId1ToBuildForThirdUser, "BUILD_SETTLEMENT", edgeId1ToBuildForThirdUser);
        // Third player moves #2
        checkAvailableForUserActionsDuringMove(userTokens, gameId, thirdGameUserNumber, secondGameUserNumber, firstGameUserNumber, nodeId2ToBuildForThirdUser, "BUILD_SETTLEMENT", edgeId2ToBuildForThirdUser);
        // Second player moves #2
        checkAvailableForUserActionsDuringMove(userTokens, gameId, secondGameUserNumber, firstGameUserNumber, thirdGameUserNumber, nodeId2ToBuildForSecondUser, "BUILD_SETTLEMENT", edgeId2ToBuildForSecondUser);
        // First player moves #2
        checkAvailableForUserActionsDuringMove(userTokens, gameId, firstGameUserNumber, secondGameUserNumber, thirdGameUserNumber, nodeId2ToBuildForFirstUser, "BUILD_SETTLEMENT", edgeId2ToBuildForFirstUser);
    }


}
