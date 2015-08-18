package catan.controllers.game;

import catan.config.ApplicationConfig;
import catan.domain.model.game.GameStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class StartGameTest extends GameTestUtil {

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
    public void should_successfully_set_user_ready() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);

        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        setUserReady(userToken1, gameId)
                .then()
                .statusCode(200);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[0].ready", equalTo(true));
    }

    @Test
    public void should_fail_when_set_user_ready_status_if_user_has_not_joined_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        setUserReady(userToken2, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        setUserNotReady(userToken3, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers.ready", everyItem(equalTo(false)));
    }

    @Test
    public void should_fail_when_set_user_ready_status_if_game_doesnt_exist() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);

        setUserReady(userToken1, 999999)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        setUserNotReady(userToken1, 999999)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    @Test
    public void should_fail_when_set_user_ready_status_if_user_is_not_authorized() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);

        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        setUserReady("some_invalid_token", gameId)
                .then()
                .statusCode(403);

        setUserNotReady("some_invalid_token", gameId)
                .then()
                .statusCode(403);
    }

    @Test
    public void should_game_has_all_players_not_ready() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        viewGame(userToken3, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers.ready", everyItem(equalTo(false)));
    }

    @Test
    public void should_successfully_set_user_not_ready() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);

        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        setUserReady(userToken1, gameId);

        setUserNotReady(userToken1, gameId)
                .then()
                .statusCode(200);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[0].ready", equalTo(false));
    }

    @Test
    public void should_game_starts_if_all_players_are_ready() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false) // set here minPlayers = 3 when that feature is available
                .path("gameId");

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("NEW"))
                .body("dateStarted", equalTo(0));

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("NEW"))
                .body("dateStarted", equalTo(0));

        setUserReady(userToken1, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("NEW"))
                .body("dateStarted", equalTo(0));

        setUserReady(userToken2, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("NEW"))
                .body("dateStarted", equalTo(0));

        setUserReady(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("PLAYING"))
                .body("dateStarted", is(both(
                        greaterThan(System.currentTimeMillis() - 60000))
                        .and(
                                lessThanOrEqualTo(System.currentTimeMillis()))));
    }

    @Test
    public void should_fail_when_set_user_ready_status_if_game_has_already_started() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false) // set here minPlayers = 3 when that feature is available
                .path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        setUserNotReady(userToken3, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        setUserReady(userToken2, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

        viewGame(userToken3, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers.ready", everyItem(equalTo(true)));
    }

    @Test
    public void should_successfully_set_resources_to_zero_when_game_starts() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false) // set here minPlayers = 3 when that feature is available
                .path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[0].resources.brick", is(0))
                .body("gameUsers[0].resources.wood", is(0))
                .body("gameUsers[0].resources.sheep", is(0))
                .body("gameUsers[0].resources.wheat", is(0))
                .body("gameUsers[0].resources.stone", is(0))
                .body("gameUsers[1].resources.brick", nullValue())
                .body("gameUsers[1].resources.wood", nullValue())
                .body("gameUsers[1].resources.sheep", nullValue())
                .body("gameUsers[1].resources.wheat", nullValue())
                .body("gameUsers[1].resources.stone", nullValue())
                .body("gameUsers[2].resources.brick", nullValue())
                .body("gameUsers[2].resources.wood", nullValue())
                .body("gameUsers[2].resources.sheep", nullValue())
                .body("gameUsers[2].resources.wheat", nullValue())
                .body("gameUsers[2].resources.stone", nullValue())
                .body("status", equalTo("PLAYING"));

        viewGame(userToken2, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[0].resources.brick", nullValue())
                .body("gameUsers[0].resources.wood", nullValue())
                .body("gameUsers[0].resources.sheep", nullValue())
                .body("gameUsers[0].resources.wheat", nullValue())
                .body("gameUsers[0].resources.stone", nullValue())
                .body("gameUsers[1].resources.brick", is(0))
                .body("gameUsers[1].resources.wood", is(0))
                .body("gameUsers[1].resources.sheep", is(0))
                .body("gameUsers[1].resources.wheat", is(0))
                .body("gameUsers[1].resources.stone", is(0))
                .body("gameUsers[2].resources.brick", nullValue())
                .body("gameUsers[2].resources.wood", nullValue())
                .body("gameUsers[2].resources.sheep", nullValue())
                .body("gameUsers[2].resources.wheat", nullValue())
                .body("gameUsers[2].resources.stone", nullValue())
                .body("status", equalTo("PLAYING"));

        viewGame(userToken3, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[0].resources.brick", nullValue())
                .body("gameUsers[0].resources.wood", nullValue())
                .body("gameUsers[0].resources.sheep", nullValue())
                .body("gameUsers[0].resources.wheat", nullValue())
                .body("gameUsers[0].resources.stone", nullValue())
                .body("gameUsers[1].resources.brick", nullValue())
                .body("gameUsers[1].resources.wood", nullValue())
                .body("gameUsers[1].resources.sheep", nullValue())
                .body("gameUsers[1].resources.wheat", nullValue())
                .body("gameUsers[1].resources.stone", nullValue())
                .body("gameUsers[2].resources.brick", is(0))
                .body("gameUsers[2].resources.wood", is(0))
                .body("gameUsers[2].resources.sheep", is(0))
                .body("gameUsers[2].resources.wheat", is(0))
                .body("gameUsers[2].resources.stone", is(0))
                .body("status", equalTo("PLAYING"));
    }

}