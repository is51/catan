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
    public void should_successfully_build_road_on_empty_edge() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int edgeId = viewGame(userToken1, gameId).path("map.edges[0].edgeId");
        int firstUserId = getUserId(userToken1);

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("map.edges[0].edgeId", is(edgeId))
                .body("map.edges[0].building", nullValue())
                .body("status", equalTo("PLAYING"));

        //TODO: need to build settlement first. add call of this method after its implementation
        /*
        buildRoad(userToken1, gameId, edgeId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .rootPath("map.edges[0]")
                .body("edgeId", is(edgeId))
                .body("building.buildingOwner.user.id", is(firstUserId))
                .body("building.built", equalTo("ROAD"));
                //TODO: check if there is a settlement on node that belong to edge or if there is another road with mutual node
        */
    }

    @Test
    public void should_fail_if_try_to_build_road_on_existing_road() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int edgeId = viewGame(userToken1, gameId).path("map.edges[0].edgeId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        //TODO: need to build settlement first. add call of this method after its implementation
        /*
        buildRoad(userToken1, gameId, edgeId);

        buildRoad(userToken2, gameId, edgeId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
        */
    }

    @Test
    public void should_fail_if_player_try_to_build_road_without_any_connection_to_his_settlement_or_city_or_road() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int edgeId = viewGame(userToken1, gameId).path("map.edges[0].edgeId");
        int firstUserId = getUserId(userToken1);

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        //build road without any connections
        buildRoad(userToken1, gameId, edgeId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        //TODO: build road with connection to smb else's settlement or city

        //TODO: build road with connection to smb else's road

    }

    @Test
    public void should_fail_if_edge_does_not_belong_to_this_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");
        int edgeId = viewGame(userToken1, gameId).path("map.edges[0].edgeId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        //TODO: need to build settlement first. add call of this method after its implementation
        /*
        buildRoad(userToken1, gameId, 999999999)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
         */
    }
}
