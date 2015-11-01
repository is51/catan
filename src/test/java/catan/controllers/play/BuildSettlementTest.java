package catan.controllers.play;

import catan.config.ApplicationConfig;
import catan.controllers.game.GameTestUtil;
import com.jayway.restassured.response.ValidatableResponse;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

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

    private class Scenario {
        private Map<String, String> userTokens = new HashMap<String, String>();
        private int idOfCreatedGame = -1;
        private ValidatableResponse currentGameDetails = null;


        public Scenario loginUser(String username, String password) {
            String userToken = GameTestUtil.loginUser(username, password);
            userTokens.put(username, userToken);

            return this;
        }

        public Scenario createNewPublicGameByUser(String userName) {
            String userToken = userTokens.get(userName);
            idOfCreatedGame = createNewGame(userToken, false).path("gameId");

            return this;
        }

        public Scenario setUserReady(String userName) {
            String userToken = userTokens.get(userName);
            GameTestUtil.setUserReady(userToken, idOfCreatedGame);

            return this;
        }

        public Scenario joinPublicGame(String userName) {
            String userToken = userTokens.get(userName);
            GameTestUtil.joinPublicGame(userToken, idOfCreatedGame);

            return this;
        }

        public Scenario getGameDetails(int moveOrder) {
            //TODO: implement search of player by move order

            return this;
        }

        public Scenario getGameDetails(String userName) {
            String userToken = userTokens.get(userName);
            currentGameDetails = viewGame(userToken, idOfCreatedGame)
                    .then()
                    .statusCode(200);

            return this;
        }

        public MapValidator node(String nodePath) {
            return new MapValidator(currentGameDetails, nodePath, this);
        }

        public Scenario buildSettlement(String userName, String nodePath) {
            String userToken = userTokens.get(userName);
            int nodeIdToBuild = viewGame(userToken, idOfCreatedGame).path(nodePath + ".nodeId");

            PlayTestUtil.buildSettlement(userToken, idOfCreatedGame, nodeIdToBuild)
                    .then()
                    .statusCode(200);
            return this;
        }

        public Scenario check(String path, Matcher matcher) {
            currentGameDetails.body(path, matcher);

            return this;
        }

        public Scenario failsWithError(String error) {
            //TODO: implement

            return this;
        }



        public Scenario stageIsPlaying() {
            currentGameDetails.body("status", equalTo("PREPARATION"));

            return this;
        }

        private class MapValidator {

            private final ValidatableResponse currentGameDetails;
            private final String path;
            private final Scenario scenario;

            public MapValidator(ValidatableResponse currentGameDetails, String path, Scenario scenario) {
                this.currentGameDetails = currentGameDetails;
                this.path = path;
                this.scenario = scenario;
            }

            public Scenario buildingIsEmpty() {
                currentGameDetails.body(path + ".building", nullValue());

                return scenario;
            }

            public Scenario hasBuiltSettlement() {
                currentGameDetails.body(path + ".building.built", equalTo("SETTLEMENT"));

                return scenario;
            }

            public Scenario buildingBelongsToPlayer(String userName) {
                String userToken = userTokens.get(userName);

                int gameUserId1 = viewGame(userToken, idOfCreatedGame).path("gameUsers[0].id");
                currentGameDetails.body(path + ".building.ownerGameUserId", is(gameUserId1));

                return scenario;
            }
        }
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

    @Test
    public void NEW___should_successfully_build_settlement_on_empty_node() {
        startNewGame()
                .getGameDetails(USER_NAME_1)
                .stageIsPlaying()
                .node("map.nodes[0]").buildingIsEmpty()
                .buildSettlement(USER_NAME_1, "map.nodes[0]")
                .getGameDetails(USER_NAME_1)
                .node("map.nodes[0]").hasBuiltSettlement()
                .node("map.nodes[0]").buildingBelongsToPlayer(USER_NAME_1);
    }

    @Test
    public void OLD___should_successfully_build_settlement_on_empty_node() {
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