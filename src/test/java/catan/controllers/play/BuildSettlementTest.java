package catan.controllers.play;

import catan.config.ApplicationConfig;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;


@Ignore
@RunWith(SpringJUnit4ClassRunner.class)

//@SpringApplicationConfiguration(classes = {ApplicationConfig.class, RequestResponseLogger.class})  // if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class BuildSettlementTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_BuildSettlementTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_BuildSettlementTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_BuildSettlementTest";
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
    public void should_successfully_build_settlement_on_empty_node() {
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

    @Test
    public void should_fail_if_try_to_build_settlement_on_existing_settlement() {
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

    @Test
    public void should_fail_if_try_to_build_settlement_close_to_another_settlement_less_than_2_roads() {
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


    @Test
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

    @Test
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