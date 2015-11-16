package catan.controllers.testcases.play;

import catan.controllers.ctf.TestApplicationConfig;
import catan.controllers.ctf.Scenario;
import catan.controllers.util.PlayTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;


@RunWith(SpringJUnit4ClassRunner.class)

//@SpringApplicationConfiguration(classes = {TestApplicationConfig.class, RequestResponseLogger.class})  // if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class BuildSettlementTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_BuildSettlementTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_BuildSettlementTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_BuildSettlementTest";
    public static final String USER_PASSWORD_3 = "password3";

    private static boolean initialized = false;

    private Scenario scenario;

    @Before
    public void setup() {
        if (!initialized) {
            registerUser(USER_NAME_1, USER_PASSWORD_1);
            registerUser(USER_NAME_2, USER_PASSWORD_2);
            registerUser(USER_NAME_3, USER_PASSWORD_3);
            initialized = true;
        }

        scenario = new Scenario();
    }

    private Scenario startNewGame() {
        return scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                .createNewPublicGameByUser(USER_NAME_1)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)

                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3);
    }

    /*
    *          (X, Y) coordinates of generated map:                          Node position at hex:
    *
    *           *----*----*----*----*----*----*                                      top
    *           | ( 0,-2) | ( 1,-2) | ( 2,-2) |                          topLeft *----*----* topRight
    *      *----*----*----*----*----*----*----*----*                             |         |
    *      | (-1,-1) | ( 0,-1) | ( 1,-1) | ( 2,-1) |                  bottomLeft *----*----* bottomRight
    * *----*----*----*----*----*----*----*----*----*----*                           bottom
    * | (-2, 0) | (-1, 0) | ( 0, 0) | ( 1, 0) | ( 2, 0) |
    * *----*----*----*----*----*----*----*----*----*----*                    Edge position at hex:
    *      | (-2, 1) | (-1, 1) | ( 0, 1) | ( 1, 1) |
    *      *----*----*----*----*----*----*----*----*                           topLeft topRight
    *           | (-2, 2) | (-1, 2) | ( 0, 2) |                                  .====.====.
    *           *----*----*----*----*----*----*                            left ||         || right
    *                                                                            .====.====.
    *                                                                       bottomLeft bottomRight
    */

    @Test
    public void should_successfully_build_settlement_on_empty_node_in_preparation_stage() {
        startNewGame()
                //Given
                .getGameDetails(1).statusIsPlaying().and().node(0, 0, "topLeft").buildingIsEmpty()

                //When
                .buildSettlement(1).atNode(0, 0, "topLeft")

                //Then
                .getGameDetails(1).node(0, 0, "topLeft").buildingBelongsToPlayer(1);
    }

    @Test
    public void should_fail_if_try_to_build_settlement_on_existing_settlement_in_preparation_stage() {
        startNewGame()
                //Given
                .buildSettlement(1).atNode(0, 0, "topLeft")
                .buildRoad(1).atEdge(0, 0, "topLeft")
                .endTurn(1)

                        //When                              //Then
                .buildSettlement(2).atNode(0, 0, "topLeft").failsWithError("ERROR")

                //Check that this player still can build settlement on empty node
                .getGameDetails(2).node(0, 0, "topRight").buildingIsEmpty()
                .buildSettlement(2).atNode(0, 0, "topRight")
                .getGameDetails(2).node(0, 0, "topRight").buildingBelongsToPlayer(2);
    }

    @Test
    public void should_fail_if_try_to_build_settlement_close_to_another_settlement_less_than_2_roads_in_preparation_stage() {
        startNewGame()
                //Given
                .buildSettlement(1).atNode(0, 0, "topLeft")
                .buildRoad(1).atEdge(0, 0, "topLeft")
                .endTurn(1)

                        //When                          //Then
                .buildSettlement(2).atNode(0, 0, "top").failsWithError("ERROR")

                //Check that this player still can build settlement on empty node
                .getGameDetails(2).node(0, 0, "topRight").buildingIsEmpty()
                .buildSettlement(2).atNode(0, 0, "topRight")
                .getGameDetails(2).node(0, 0, "topRight").buildingBelongsToPlayer(2);
    }

    public void OLD___should_successfully_build_settlement_on_empty_node_in_preparation_stage() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.nodes[0].nodeId");
        int gameUserId1 = viewGame(userToken1, gameId).path("gameUsers[0].id");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.nodes[0].nodeId", is(nodeIdToBuild))
                .body("map.nodes[0].building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildSettlement(userToken1, gameId, nodeIdToBuild)
                .then()
                .statusCode(200);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.nodes[0].nodeId", is(nodeIdToBuild))
                .body("map.nodes[0].building.ownerGameUserId", is(gameUserId1))
                .body("map.nodes[0].building.built", equalTo("SETTLEMENT"))
                .body("status", equalTo("PLAYING"));
    }

    public void OLD_should_fail_if_try_to_build_settlement_on_existing_settlement_in_preparation_stage() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.nodes[0].nodeId");
        int gameUserId1 = viewGame(userToken1, gameId).path("gameUsers[0].id");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.nodes[0].nodeId", is(nodeIdToBuild))
                .body("map.nodes[0].building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildSettlement(userToken1, gameId, nodeIdToBuild)
                .then()
                .statusCode(200);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.nodes[0].nodeId", is(nodeIdToBuild))
                .body("map.nodes[0].building.ownerGameUserId", is(gameUserId1))
                .body("map.nodes[0].building.built", equalTo("SETTLEMENT"))
                .body("status", equalTo("PLAYING"));

        buildSettlement(userToken2, gameId, nodeIdToBuild)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    public void OLD_should_fail_if_try_to_build_settlement_close_to_another_settlement_less_than_2_roads_in_preparation_stage() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuildFirst = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int nodeIdToBuildSecond = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topId");
        int gameUserId1 = viewGame(userToken1, gameId).path("gameUsers[0].id");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.hexes[0].nodesIds.topLeftId", is(nodeIdToBuildFirst))
                .body("map.nodes.find {it.nodeId == " + nodeIdToBuildFirst + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildSettlement(userToken1, gameId, nodeIdToBuildFirst)
                .then()
                .statusCode(200);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("map.hexes[0].nodesIds.topLeftId", is(nodeIdToBuildFirst))
                .rootPath("map.nodes.find {it.nodeId == " + nodeIdToBuildFirst + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("SETTLEMENT"));


        buildSettlement(userToken2, gameId, nodeIdToBuildSecond)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    /*
    @Test
    public void should_fail_if_try_to_build_settlement_and_there_are_no_neighbour_roads_that_belongs_to_this_player_when_game_status_is_not_preparation() {
        //TODO: IMPLEMENT WHEN PREPARATION STATUS IS IMPLEMENTED
    }
    */


    //TODO: do we need this scenario?????
    public void should_fail_if_try_to_build_settlement_if_two_of_three_neighbour_roads_belongs_to_other_player() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int gameUserId1 = viewGame(userToken1, gameId).path("gameUsers[0].id");
        int nodeIdToBuildFirstSettlement = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int nodeIdToBuildSecondSettlement = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topRightId");
        int edgeIdToBuildFirstRoad = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");
        int edgeIdToBuildSecondRoad = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topRightId");
        int edgeIdToBuildThirdRoad = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.rightId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.hexes[0].nodesIds.topLeftId", is(nodeIdToBuildFirstSettlement))
                .body("map.nodes.find {it.nodeId == " + nodeIdToBuildFirstSettlement + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildSettlement(userToken1, gameId, nodeIdToBuildFirstSettlement)
                .then()
                .statusCode(200);

        buildRoad(userToken1, gameId, edgeIdToBuildFirstRoad)
                .then()
                .statusCode(200);
        buildRoad(userToken1, gameId, edgeIdToBuildSecondRoad)
                .then()
                .statusCode(200);
        buildRoad(userToken1, gameId, edgeIdToBuildThirdRoad)
                .then()
                .statusCode(200);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.hexes[0].nodesIds.topLeftId", is(nodeIdToBuildFirstSettlement))
                .rootPath("map.nodes.find {it.nodeId == " + nodeIdToBuildFirstSettlement + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("SETTLEMENT"))
                .rootPath("map.nodes.find {it.nodeId == " + nodeIdToBuildSecondSettlement + "}")
                .body("building", nullValue())
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuildFirstRoad + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"))
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuildSecondRoad + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"))
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuildThirdRoad + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"));

        buildSettlement(userToken2, gameId, nodeIdToBuildSecondSettlement)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    //TODO: such test case should be covered by unit test
    public void should_fail_if_node_does_not_belong_to_this_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = -1;

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        buildSettlement(userToken1, gameId, nodeIdToBuild)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }
}