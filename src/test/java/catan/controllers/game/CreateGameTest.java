package catan.controllers.game;

import catan.config.ApplicationConfig;
import catan.domain.model.game.GameStatus;
import catan.services.impl.GameServiceImpl;
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
public class CreateGameTest extends GameTestUtil {

    public static final String USER_NAME_1 = "user1_CreateGameTest";
    public static final String USER_PASSWORD_1 = "password1";

    private static boolean initialized = false;

    @Before
    public void setup() {
        if (!initialized) {
            registerUser(USER_NAME_1, USER_PASSWORD_1);
            initialized = true;
        }
    }

    @Test
    public void should_successfully_create_new_private_game() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int userId = getUserId(userToken);

        createNewGame(userToken, true, 12)
                .then()
                .statusCode(200)
                .contentType(ACCEPT_CONTENT_TYPE)
                .body("gameId", greaterThan(0))
                .body("creatorId", equalTo(userId))
                .body("privateGame", equalTo(true))
                .body("status", equalTo(GameStatus.NEW.toString()))
                .body("privateCode", not(equalTo(isEmptyString())))
                .body("dateCreated", is(both(   //closeTo can be applied only to 'double' values, but dateCreated is 'long' value
                        greaterThan(System.currentTimeMillis() - 60000))
                        .and(
                                lessThanOrEqualTo(System.currentTimeMillis()))))
                .body("minUsers", equalTo(GameServiceImpl.MIN_USERS))
                .body("maxUsers", equalTo(GameServiceImpl.MAX_USERS))
                .body("targetVictoryPoints", greaterThan(2))
                .body("gameUsers", not(equalTo(isEmptyString())));
    }

    @Test
    public void should_successfully_create_new_public_game() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);

        createNewGame(userToken, false, 12)
                .then()
                .statusCode(200)
                .body("privateCode", isEmptyOrNullString());
    }

    @Test
    public void should_fail_with_error_when_there_is_no_private_game_variable() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);

        given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", userToken)
                .when()
                .post(URL_CREATE_NEW_GAME)
                .then()
                .statusCode(400);
    }

    @Test
    public void should_fail_with_403_error_when_user_is_not_authorized() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);

        // without token
        given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("privateGame", true) // 'privateGame' is mandatory, 'token' is not mandatory
                .when()
                .post(URL_CREATE_NEW_GAME)
                .then()
                .statusCode(403);

        logoutUser(userToken);

        // with old token
        given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", userToken, "privateGame", true)
                .when()
                .post(URL_CREATE_NEW_GAME)
                .then()
                .statusCode(403);
    }

    @Test
    public void should_fail_when_targetVictoryPoints_less_than_min() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);

        createNewGame(userToken, false, 2)
                .then()
                .statusCode(400)
                //.body("targetVictoryPoints", lessThan(3))
                .body("errorCode", equalTo("ERROR"));
    }
}