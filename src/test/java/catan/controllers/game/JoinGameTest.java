package catan.controllers.game;

import catan.config.ApplicationConfig;
import catan.controllers.FunctionalTestUtil;
import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class JoinGameTest extends FunctionalTestUtil {

    public static final String URL_CREATE_NEW_GAME = "/api/game/create";
    public static final String URL_JOIN_PUBLIC_GAME = "/api/game/join/public";
    public static final String URL_JOIN_PRIVATE_GAME = "/api/game/join/private";

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

    private static boolean initialized = false;

    private Response createNewGame(String token, boolean privateGame) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "privateGame", privateGame)
                .when()
                .post(URL_CREATE_NEW_GAME);
    }

    private Response joinPublicGame(String token, int gameId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId)
                .when()
                .post(URL_JOIN_PUBLIC_GAME);
    }

    private Response joinPrivateGame(String token, String privateCode) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "privateCode", privateCode)
                .when()
                .post(URL_JOIN_PRIVATE_GAME);
    }

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

    /*@Test
    public void should_fails_when_user_joins_public_game_if_game_is_canceled() {
        //TODO: should_fails_when_user_joins_public_game_if_game_is_canceled
        //needs to be written when game canceling is implemented
    }*/

    @Test
    public void should_fails_when_user_joins_public_game_if_to_many_players_in() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        joinPublicGame(loginUser(USER_NAME_2, USER_PASSWORD_2), gameId);
        joinPublicGame(loginUser(USER_NAME_3, USER_PASSWORD_3), gameId);
        joinPublicGame(loginUser(USER_NAME_4, USER_PASSWORD_4), gameId);

        joinPublicGame(loginUser(USER_NAME_5, USER_PASSWORD_5), gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("TO_MANY_PLAYERS"));
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
        String privateCode = createNewGame(userToken1, true)
                .path("privateCode");

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);

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
}