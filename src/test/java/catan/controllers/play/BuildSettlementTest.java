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
import static org.hamcrest.Matchers.nullValue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class BuildSettlementTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_StartGameTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_StartGameTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_StartGameTest";
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
        int node_id = viewGame(userToken1, gameId).path("map.nodes[0].nodeId");
        //TODO: need to fetch gameUserId from JSON, but now it is not returned
        //int gameUser_1_Id = viewGame(userToken1, gameId).path("gameUsers[0].id");


        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.nodes[0].nodeId", is(node_id))
                .body("map.nodes[0].building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildSettlement(userToken1, gameId, node_id)
                .then()
                .statusCode(200)
                .body("map.nodes[0].nodeId", is(node_id))
                .body("map.nodes[0].building.ownerGameUserId", is(1))   //need to compare with gameUser_1_Id, when it will be available
                .body("map.nodes[0].building.built", equalTo("SETTLEMENT"))
                .body("status", equalTo("PLAYING"));
    }

    @Test
    public void should_fail_if_try_to_build_settlement_on_existing_settlement() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int node_id = viewGame(userToken1, gameId).path("map.nodes[0].nodeId");
        //TODO: need to fetch gameUserId from JSON, but now it is not returned
        //int gameUser_1_Id = viewGame(userToken1, gameId).path("gameUsers[0].id");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.nodes[0].nodeId", is(node_id))
                .body("map.nodes[0].building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildSettlement(userToken1, gameId, node_id)
                .then()
                .statusCode(200)
                .body("map.nodes[0].nodeId", is(node_id))
                .body("map.nodes[0].building.ownerGameUserId", is(1))   //need to compare with gameUser_1_Id, when it will be available
                .body("map.nodes[0].building.built", equalTo("SETTLEMENT"))
                .body("status", equalTo("PLAYING"));

        buildSettlement(userToken2, gameId, node_id)
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
        int node_id = viewGame(userToken1, gameId).path("map.nodes[0].nodeId");

        //TODO: need to fetch gameUserId from JSON, but now it is not returned
        //int gameUser_1_Id = viewGame(userToken1, gameId).path("gameUsers[0].id");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.nodes[0].nodeId", is(node_id))
                .body("map.nodes[0].building", nullValue())
                .body("status", equalTo("PLAYING"));

        buildSettlement(userToken1, gameId, node_id)
                .then()
                .statusCode(200)
                .body("map.nodes[0].nodeId", is(node_id))
                .body("map.nodes[0].building.ownerGameUserId", is(1))   //need to compare with gameUser_1_Id, when it will be available
                .body("map.nodes[0].building.built", equalTo("SETTLEMENT"))
                .body("status", equalTo("PLAYING"));

        //TODO:  (node_id + 1) can belong to another hex than (node_id), think about using following values in requests:
        // map.hexes[0].nodes.topLeftId
        // map.hexes[0].nodes.topId
        buildSettlement(userToken2, gameId, node_id + 1)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }
}