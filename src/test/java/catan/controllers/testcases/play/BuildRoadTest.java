package catan.controllers.testcases.play;

import catan.controllers.ctf.Scenario;
import catan.controllers.ctf.TestApplicationConfig;
import catan.controllers.util.PlayTestUtil;
import catan.domain.model.dashboard.types.HexType;
import catan.services.util.random.RandomUtil;
import catan.services.util.random.RandomUtilMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class BuildRoadTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_BuildRoadTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_BuildRoadTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_BuildRoadTest";
    public static final String USER_PASSWORD_3 = "password3";

    private static boolean initialized = false;

    @Autowired
    private RandomUtil randomUtil;

    private Scenario scenario;

    @Before
    public void setup() {
        scenario = new Scenario((RandomUtilMock) randomUtil);

        if (!initialized) {
            scenario
                    .registerUser(USER_NAME_1, USER_PASSWORD_1)
                    .registerUser(USER_NAME_2, USER_PASSWORD_2)
                    .registerUser(USER_NAME_3, USER_PASSWORD_3);
            initialized = true;
        }
    }

    @Test
    public void should_not_take_resources_from_player_when_build_road_in_preparation_stage() {
        startNewGame()
                .BUILD_SETTLEMENT(1).atNode(2, -2, "topLeft")

                .startTrackResourcesQuantity()

                .BUILD_ROAD(1).atEdge(2, -2, "topLeft").successfully()
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_successfully_take_resources_from_player_when_build_road_in_main_stage() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForRoadBuilding(1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .startTrackResourcesQuantity()

                .BUILD_ROAD(1).atEdge(2, -2, "topRight").successfully()
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(-1, -1, 0, 0, 0);
    }

    @Test
    public void should_successfully_build_road_even_if_user_does_not_have_resources_in_preparation_stage() {
        startNewGame()
                .BUILD_SETTLEMENT(1).atNode(2, -2, "topLeft")

                .getGameDetails(1).gameUser(1).check("resources.brick", is(0))
                .getGameDetails(1).gameUser(1).check("resources.wood", is(0))

                .BUILD_ROAD(1).atEdge(2, -2, "topLeft").successfully();
    }

    @Test
    public void should_successfully_build_road_if_user_has_enough_resources_in_main_stage() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForRoadBuilding(1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .getGameDetails(1).gameUser(1).check("resources.brick", greaterThanOrEqualTo(1))
                .getGameDetails(1).gameUser(1).check("resources.wood", greaterThanOrEqualTo(1))

                .BUILD_ROAD(1).atEdge(2, -2, "topRight").successfully();
    }

    @Test
    public void should_fail_when_build_road_if_user_does_not_have_resources_in_main_stage() {
        startNewGame();
        playPreparationStage()
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .getGameDetails(1).gameUser(1).check("resources.brick", is(0))
                .getGameDetails(1).gameUser(1).check("resources.wood", is(0))
                .getGameDetails(1).gameUser(1).doesntHaveAvailableAction("BUILD_ROAD")

                .BUILD_ROAD(1).atEdge(2, -2, "topLeft").failsWithError("ERROR");
    }

    @Ignore
    @Test
    public void should_successfully_build_road_on_empty_edge_if_has_neighbour_settlement_that_belongs_to_this_player() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int edgeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");
        int gameUserId1 = viewGame(userToken1, gameId).path("gameUsers[0].id");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        buildSettlement(userToken1, gameId, nodeIdToBuild);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuild))
                .body("map.nodes.find {it.nodeId == " + nodeIdToBuild + "}.building", notNullValue())
                .body("map.edges.find {it.edgeId == " + edgeIdToBuild + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuild);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuild))
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuild + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"));

    }

    @Ignore
    @Test
    public void should_successfully_build_road_on_empty_edge_if_has_neighbour_road_that_belongs_to_this_player() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int edgeIdToBuildFirst = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");
        int edgeIdToBuildSecond = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topRightId");
        int gameUserId1 = viewGame(userToken1, gameId).path("gameUsers[0].id");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        buildSettlement(userToken1, gameId, nodeIdToBuild);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuildFirst))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildFirst + "}.building", nullValue())
                .body("map.hexes[0].edgesIds.topRightId", is(edgeIdToBuildSecond))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildSecond + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuildFirst);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuildFirst))
                .body("map.hexes[0].edgesIds.topRightId", is(edgeIdToBuildSecond))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildSecond + "}.building", nullValue())
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuildFirst + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"));

        buildRoad(userToken1, gameId, edgeIdToBuildSecond);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuildFirst))
                .body("map.hexes[0].edgesIds.topRightId", is(edgeIdToBuildSecond))
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuildFirst + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"))
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuildSecond + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"));

    }

    @Ignore
    @Test
    public void should_fail_if_try_to_build_road_on_existing_road() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int edgeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");
        int gameUserId1 = viewGame(userToken1, gameId).path("gameUsers[0].id");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        buildSettlement(userToken1, gameId, nodeIdToBuild);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuild))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuild + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuild);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuild))
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuild + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"));

        buildRoad(userToken2, gameId, edgeIdToBuild)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

    }

    @Ignore
    @Test
    public void should_fail_if_player_try_to_build_road_without_any_connection_to_settlement_or_city_or_road() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int edgeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuild))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuild + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuild)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

    }

    @Ignore
    @Test
    public void should_fail_if_player_try_to_build_road_without_any_connection_to_his_settlement_or_city_or_road_but_connected_with_opponents_settlement() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int edgeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        buildSettlement(userToken2, gameId, nodeIdToBuild);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuild))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuild + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuild)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

    }

    @Ignore
    @Test
    public void should_fail_if_player_try_to_build_road_without_any_connection_to_his_settlement_or_city_or_road_but_connected_with_opponents_road() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int edgeIdToBuildFirst = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");
        int edgeIdToBuildSecond = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topRightId");;
        int gameUserId1 = viewGame(userToken1, gameId).path("gameUsers[0].id");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        buildSettlement(userToken1, gameId, nodeIdToBuild);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuildFirst))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildFirst + "}.building", nullValue())
                .body("map.hexes[0].edgesIds.topRightId", is(edgeIdToBuildSecond))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildSecond + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuildFirst);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuildFirst))
                .body("map.hexes[0].edgesIds.topRightId", is(edgeIdToBuildSecond))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildSecond + "}.building", nullValue())
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuildFirst + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"));

        buildRoad(userToken2, gameId, edgeIdToBuildSecond)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

    }

    @Ignore
    @Test
    public void should_fail_if_edge_has_connection_to_neighbour_road_but_opposite_node_has_building_that_belongs_to_another_player() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuildFirst = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int nodeIdToBuildSecond = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topRightId");
        int edgeIdToBuildFirst = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");
        int edgeIdToBuildSecond = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topRightId");
        int gameUserId1 = viewGame(userToken1, gameId).path("gameUsers[0].id");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        buildSettlement(userToken1, gameId, nodeIdToBuildFirst);
        buildSettlement(userToken2, gameId, nodeIdToBuildSecond);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuildFirst))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildFirst + "}.building", nullValue())
                .body("map.hexes[0].edgesIds.topRightId", is(edgeIdToBuildSecond))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildSecond + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuildFirst);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("map.hexes[0].edgesIds.topLeftId", is(edgeIdToBuildFirst))
                .body("map.hexes[0].edgesIds.topRightId", is(edgeIdToBuildSecond))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildSecond + "}.building", nullValue())
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuildFirst + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"));

        buildRoad(userToken1, gameId, edgeIdToBuildSecond)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    private Scenario giveResourcesToPlayerForRoadBuilding(int moveOrder) {
        return scenario
                .nextRandomDiceValues(asList(moveOrder, moveOrder))
                .THROW_DICE(moveOrder)
                .END_TURN(moveOrder)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(moveOrder == 1 ? 2 : moveOrder == 2 ? 3 : 1)
                .END_TURN(moveOrder == 1 ? 2 : moveOrder == 2 ? 3 : 1)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(moveOrder == 1 ? 3 : moveOrder == 2 ? 1 : 2)
                .END_TURN(moveOrder == 1 ? 3 : moveOrder == 2 ? 1 : 2);
    }

    private Scenario startNewGame() {
        return scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                .setHex(HexType.STONE, 11).atCoordinates(0, -2)
                .setHex(HexType.BRICK, 2).atCoordinates(1, -2)
                .setHex(HexType.WOOD, 2).atCoordinates(2, -2)

                .setHex(HexType.STONE, 11).atCoordinates(-1, -1)
                .setHex(HexType.WOOD, 6).atCoordinates(0, -1)
                .setHex(HexType.SHEEP, 10).atCoordinates(1, -1)
                .setHex(HexType.BRICK, 4).atCoordinates(2, -1)

                .setHex(HexType.STONE, 11).atCoordinates(-2, 0)
                .setHex(HexType.STONE, 4).atCoordinates(-1, 0)
                .setHex(HexType.EMPTY, null).atCoordinates(0, 0)
                .setHex(HexType.SHEEP, 3).atCoordinates(1, 0)
                .setHex(HexType.WOOD, 4).atCoordinates(2, 0)

                .setHex(HexType.SHEEP, 9).atCoordinates(-2, 1)
                .setHex(HexType.BRICK, 9).atCoordinates(-1, 1)
                .setHex(HexType.SHEEP, 11).atCoordinates(0, 1)
                .setHex(HexType.WOOD, 6).atCoordinates(1, 1)

                .setHex(HexType.WOOD, 10).atCoordinates(-2, 2)
                .setHex(HexType.WHEAT, 2).atCoordinates(-1, 2)
                .setHex(HexType.BRICK, 6).atCoordinates(0, 2)

                .createNewPublicGameByUser(USER_NAME_1)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)

                        // take last player from the list each time, when pulling move order from the list to have order: 3, 2, 1
                .nextRandomMoveOrderValues(asList(3, 2, 1))

                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3);
    }

    private Scenario playPreparationStage() {
        return scenario
                .BUILD_SETTLEMENT(1).atNode(2, -2, "topLeft")
                .BUILD_ROAD(1).atEdge(2, -2, "topLeft")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(2, -1, "bottomRight")
                .BUILD_ROAD(2).atEdge(2, -1, "bottomRight")
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(0, 2, "topRight")
                .BUILD_ROAD(3).atEdge(0, 2, "topRight")
                .END_TURN(3)

                .BUILD_SETTLEMENT(3).atNode(0, -2, "topLeft")
                .BUILD_ROAD(3).atEdge(0, -2, "topLeft")
                .END_TURN(3)

                .BUILD_SETTLEMENT(2).atNode(-1, -1, "topLeft")
                .BUILD_ROAD(2).atEdge(-1, -1, "topLeft")
                .END_TURN(2)

                .BUILD_SETTLEMENT(1).atNode(-2, 0, "topLeft")
                .BUILD_ROAD(1).atEdge(-2, 0, "topLeft")
                .END_TURN(1);
    }

    /*
    *          (X, Y) coordinates of generated map:                          Node position at hex:
    *
    *           *----*----*----*----*----*----*                                      top
    *           |   11    |    2    |     2   |                          topLeft *----*----* topRight
    *           |  STONE  |  BRICK  |   WOOD  |                                  |         |
    *           | ( 0,-2) | ( 1,-2) | ( 2,-2) |                       bottomLeft *----*----* bottomRight
    *      *----*----*----*----*----*----*----*----*                                bottom
    *      |    11   |    3    |    10   |    4    |
    *      |  STONE  |   WOOD  |  SHEEP  |  BRICK  |
    *      | (-1,-1) | ( 0,-1) | ( 1,-1) | ( 2,-1) |                        Edge position at hex:
    * *----*----*----*----*----*----*----*----*----*----*
    * |   11    |    4    |         |    3    |    4    |                      topLeft topRight
    * |  STONE  |  STONE  |  EMPTY  |  SHEEP  |   WOOD  |                        .====.====.
    * | (-2, 0) | (-1, 0) | ( 0, 0) | ( 1, 0) | ( 2, 0) |                  left ||         || right
    * *----*----*----*----*----*----*----*----*----*----*                        .====.====.
    *      |    9    |    9    |    11   |    6    |                        bottomLeft bottomRight
    *      |  SHEEP  |  BRICK  |  SHEEP  |   WOOD  |
    *      | (-2, 1) | (-1, 1) | ( 0, 1) | ( 1, 1) |
    *      *----*----*----*----*----*----*----*----*
    *           |    10   |    2    |    6    |
    *           |   WOOD  |  WHEAT  |  BRICK  |
    *           | (-2, 2) | (-1, 2) | ( 0, 2) |
    *           *----*----*----*----*----*----*
    *
    *
    */
}
