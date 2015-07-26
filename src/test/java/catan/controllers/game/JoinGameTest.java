package catan.controllers.game;

import catan.config.ApplicationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.jayway.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class JoinGameTest extends GameTestUtil {

    public static final String USER_NAME_1 = "user1_JoinGameTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_JoinGameTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_JoinGameTest";
    public static final String USER_PASSWORD_3 = "password3";
    public static final String USER_NAME_4 = "user4_JoinGameTest";
    public static final String USER_PASSWORD_4 = "password4";
    public static final String USER_NAME_5 = "user5_JoinGameTest";
    public static final String USER_PASSWORD_5 = "password5";

    public static final String USER_NAME_GUEST_1 = "guest1_JoinGameTest";
    public static final String USER_NAME_GUEST_2 = "guest2_JoinGameTest";

    private static boolean initialized = false;

    @Before
    public void setup() {
        if (!initialized) {
            registerUser(USER_NAME_1, USER_PASSWORD_1);
            registerUser(USER_NAME_2, USER_PASSWORD_2);
            registerUser(USER_NAME_3, USER_PASSWORD_3);
            registerUser(USER_NAME_4, USER_PASSWORD_4);
            registerUser(USER_NAME_5, USER_PASSWORD_5);
            initialized = true;
        }
    }

    @Test
    public void should_successfully_join_public_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);

        joinPublicGame(userToken2, gameId)
                .then()
                .statusCode(200);
    }

    @Test
    public void should_guest_successfully_join_public_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        String userToken2 = registerAndLoginGuest(USER_NAME_GUEST_1)
                .path("token");

        joinPublicGame(userToken2, gameId)
                .then()
                .statusCode(200);
    }

    @Test
    public void should_fails_when_user_joins_public_game_if_game_is_not_really_public() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, true)
                .path("gameId");

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);

        joinPublicGame(userToken2, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    /*@Test
    public void should_fails_when_user_joins_public_game_if_game_has_already_started() {
        //TODO: should_fails_when_user_joins_public_game_if_game_has_already_started
        //needs to be written when game starting is implemented
    }*/

    /*@Test
    public void should_fails_when_user_joins_public_game_if_game_is_finished() {
        //TODO: should_fails_when_user_joins_public_game_if_game_is_finished
        //needs to be written when game finishing is implemented
    }*/

    @Test
    public void should_fails_when_user_joins_public_game_if_game_is_canceled() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);

        int gameId = createNewGame(userToken1, false)
                .path("gameId");
        cancelGame(userToken1, gameId);

        joinPublicGame(userToken2, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("GAME_CANCELED"));
    }

    @Test
    public void should_fails_when_user_joins_public_game_if_too_many_players_in() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        joinPublicGame(loginUser(USER_NAME_2, USER_PASSWORD_2), gameId);
        joinPublicGame(loginUser(USER_NAME_3, USER_PASSWORD_3), gameId);
        joinPublicGame(loginUser(USER_NAME_4, USER_PASSWORD_4), gameId);

        joinPublicGame(loginUser(USER_NAME_5, USER_PASSWORD_5), gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("TOO_MANY_PLAYERS"));
    }

    @Test
    public void should_fails_when_user_joins_public_game_if_there_is_no_game() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);
        joinPublicGame(userToken, 999999999)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    @Test
    public void should_fails_when_user_joins_public_game_if_user_is_not_authorized() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken, false)
                .path("gameId");
        logoutUser(userToken);

        joinPublicGame("some invalid token", gameId)
                .then()
                .statusCode(403);
    }

    // ----------------------
    // Join Private Game
    // ----------------------

    @Test
    public void should_successfully_join_private_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);

        Response response = createNewGame(userToken1, true);
        int gameId = response.path("gameId");
        String privateCode = response.path("privateCode");

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);

        joinPrivateGame(userToken2, privateCode)
                .then()
                .statusCode(200)
                .body("gameId", equalTo(gameId));
    }

    public void should_guest_successfully_join_private_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String privateCode = createNewGame(userToken1, true)
                .path("privateCode");

        String userToken2 = registerAndLoginGuest(USER_NAME_GUEST_2)
                .path("token");

        joinPrivateGame(userToken2, privateCode)
                .then()
                .statusCode(200);
    }

    @Test
    public void should_fails_when_join_private_game_with_wrong_code() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);

        joinPrivateGame(userToken, "some_wrong_code")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("INVALID_CODE"));
    }

    @Test
    public void should_fails_when_already_joined_user_joins_public_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken2, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ALREADY_JOINED"));
    }

    @Test
    public void should_fails_when_creator_joins_his_public_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        joinPublicGame(userToken1, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ALREADY_JOINED"));
    }

    @Test
    public void should_fails_when_already_joined_user_joins_private_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String privateCode = createNewGame(userToken1, true)
                .path("privateCode");

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);

        joinPrivateGame(userToken2, privateCode);
        joinPrivateGame(userToken2, privateCode)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ALREADY_JOINED"));
    }

    @Test
    public void should_fails_when_creator_joins_his_private_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String privateCode = createNewGame(userToken1, true)
                .path("privateCode");

        joinPrivateGame(userToken1, privateCode)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ALREADY_JOINED"));
    }

    @Test
    public void should_successfully_run_when_players_colors_are_different() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);
        String userToken4 = loginUser(USER_NAME_4, USER_PASSWORD_4);

        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);
        joinPublicGame(userToken4, gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers.colorId", hasItems(1, 2, 3, 4));

        leaveGame(userToken3, gameId);
        joinPublicGame(userToken3, gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers.colorId", hasItems(1, 2, 3, 4));
    }
}