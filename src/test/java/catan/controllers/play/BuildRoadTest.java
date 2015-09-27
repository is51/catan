package catan.controllers.play;

import catan.config.ApplicationConfig;
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
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class BuildRoadTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_BuildRoadTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_BuildRoadTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_BuildRoadTest";
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
    public void should_successfully_build_road_on_empty_edge_if_has_neighbour_settlement_that_belongs_to_this_player() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].nodes.topLeftId");
        int edgeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].edges.topLeftId");
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
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuild))
                .body("map.nodes.find {it.nodeId == " + nodeIdToBuild + "}.building", notNullValue())
                .body("map.edges.find {it.edgeId == " + edgeIdToBuild + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuild);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuild))
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuild + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"));

    }

    @Test
    public void should_successfully_build_road_on_empty_edge_if_has_neighbour_road_that_belongs_to_this_player() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].nodes.topLeftId");
        int edgeIdToBuildFirst = viewGame(userToken1, gameId).path("map.hexes[0].edges.topLeftId");
        int edgeIdToBuildSecond = viewGame(userToken1, gameId).path("map.hexes[0].edges.topRightId");
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
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuildFirst))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildFirst + "}.building", nullValue())
                .body("map.hexes[0].edges.topRightId", is(edgeIdToBuildSecond))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildSecond + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuildFirst);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuildFirst))
                .body("map.hexes[0].edges.topRightId", is(edgeIdToBuildSecond))
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
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuildFirst))
                .body("map.hexes[0].edges.topRightId", is(edgeIdToBuildSecond))
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuildFirst + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"))
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuildSecond + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"));

    }

    @Test
    public void should_fail_if_try_to_build_road_on_existing_road() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].nodes.topLeftId");
        int edgeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].edges.topLeftId");
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
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuild))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuild + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuild);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuild))
                .rootPath("map.edges.find {it.edgeId == " + edgeIdToBuild + "}")
                .body("building", notNullValue())
                .body("building.ownerGameUserId", is(gameUserId1))
                .body("building.built", equalTo("ROAD"));

        buildRoad(userToken2, gameId, edgeIdToBuild)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

    }

    @Test
    public void should_fail_if_player_try_to_build_road_without_any_connection_to_settlement_or_city_or_road() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int edgeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].edges.topLeftId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuild))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuild + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuild)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

    }

    @Test
    public void should_fail_if_player_try_to_build_road_without_any_connection_to_his_settlement_or_city_or_road_but_connected_with_opponents_settlement() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].nodes.topLeftId");
        int edgeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].edges.topLeftId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        buildSettlement(userToken2, gameId, nodeIdToBuild);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuild))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuild + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuild)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

    }

    @Test
    public void should_fail_if_player_try_to_build_road_without_any_connection_to_his_settlement_or_city_or_road_but_connected_with_opponents_road() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].nodes.topLeftId");
        int edgeIdToBuildFirst = viewGame(userToken1, gameId).path("map.hexes[0].edges.topLeftId");
        int edgeIdToBuildSecond = viewGame(userToken1, gameId).path("map.hexes[0].edges.topRightId");;
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
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuildFirst))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildFirst + "}.building", nullValue())
                .body("map.hexes[0].edges.topRightId", is(edgeIdToBuildSecond))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildSecond + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuildFirst);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuildFirst))
                .body("map.hexes[0].edges.topRightId", is(edgeIdToBuildSecond))
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

    @Test
    public void should_fail_if_edge_does_not_belong_to_this_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        buildRoad(userToken1, gameId, -1)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

    }


    @Test
    public void should_fail_if_edge_has_connection_to_neighbour_road_but_opposite_node_has_building_that_belongs_to_another_player() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int nodeIdToBuildFirst = viewGame(userToken1, gameId).path("map.hexes[0].nodes.topLeftId");
        int nodeIdToBuildSecond = viewGame(userToken1, gameId).path("map.hexes[0].nodes.topRightId");
        int edgeIdToBuildFirst = viewGame(userToken1, gameId).path("map.hexes[0].edges.topLeftId");
        int edgeIdToBuildSecond = viewGame(userToken1, gameId).path("map.hexes[0].edges.topRightId");
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
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuildFirst))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildFirst + "}.building", nullValue())
                .body("map.hexes[0].edges.topRightId", is(edgeIdToBuildSecond))
                .body("map.edges.find {it.edgeId == " + edgeIdToBuildSecond + "}.building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildRoad(userToken1, gameId, edgeIdToBuildFirst);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("map.hexes[0].edges.topLeftId", is(edgeIdToBuildFirst))
                .body("map.hexes[0].edges.topRightId", is(edgeIdToBuildSecond))
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
}
