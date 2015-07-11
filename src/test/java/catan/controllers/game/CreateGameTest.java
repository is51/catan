package catan.controllers.game;

import catan.config.ApplicationConfig;
import catan.controllers.FunctionalTestUtil;
import catan.domain.model.game.GameStatus;
import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class CreateGameTest extends FunctionalTestUtil {
    public static final String URL_CREATE_NEW_GAME = "/api/game/create";

    public static final String USER_NAME_1 = "user1_CreateGameTest";
    public static final String USER_PASSWORD_1 = "password1";

    private static boolean initialized = false;


    private Response createNewGame(String token, boolean privateGame) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "privateGame", privateGame)
                .when()
                .post(URL_CREATE_NEW_GAME);
    }


    @Before
    public void setup() {
        if (!initialized) {
            registerUser(USER_NAME_1, USER_PASSWORD_1);
            initialized = true;
        }
    }

    // ----------------
    // Create New Game
    // ----------------

    @Test
    public void should_successfully_create_new_private_game() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int userId = getUserId(userToken);

        createNewGame(userToken, true)
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
                                lessThanOrEqualTo(System.currentTimeMillis()))));
    }

    @Test
    public void should_successfully_create_new_public_game() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);

        createNewGame(userToken, false)
                .then()
                .statusCode(200)
                .body("privateCode", equalTo(isEmptyOrNullString()));
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

    /*@Test
    public void should_creator_be_added_to_game_as_player() {
        //TODO: should_creator_be_added_to_game_as_player
    }*/
}