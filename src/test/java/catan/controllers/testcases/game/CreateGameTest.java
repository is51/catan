package catan.controllers.testcases.game;

import catan.config.ApplicationConfig;
import catan.controllers.util.GameTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isEmptyOrNullString;


@RunWith(SpringJUnit4ClassRunner.class)
//Add it if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = {ApplicationConfig.class, RequestResponseLogger.class})
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class CreateGameTest extends GameTestUtil {

    public static final String USER_NAME_1 = "user1_CreateGameTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final int MIN_TARGET_VICTORY_POINTS = 2;
    public static final String USER_NAME_GUEST_1 = "guest1_CreateGameTest";
    public static final String USER_NAME_GUEST_2 = "guest2_CreateGameTest";

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

        createNewGame(userToken, true, 99)
                .then()
                .statusCode(200)
                .contentType(ACCEPT_CONTENT_TYPE)
                .body("gameId", greaterThan(0));
    }

    @Test
    public void should_guest_successfully_create_new_private_game() {
        String userToken = registerAndLoginGuest(USER_NAME_GUEST_1)
                .path("token");

        createNewGame(userToken, true)
                .then()
                .statusCode(200);
    }

    @Test
    public void should_successfully_create_new_public_game() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);

        createNewGame(userToken, false)
                .then()
                .statusCode(200)
                .body("privateCode", isEmptyOrNullString());
    }

    @Test
    public void should_fail_when_create_new_public_game_if_creator_is_guest() {
        String userToken = registerAndLoginGuest(USER_NAME_GUEST_2)
                .path("token");

        createNewGame(userToken, false)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("GUEST_NOT_PERMITTED"));
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
                .parameters("privateGame",    // 'privateGame' is mandatory, 'token' is not mandatory
                            true,
                            "targetVictoryPoints",
                            DEFAULT_TARGET_VICTORY_POINTS,
                            "initialBuildingsSetId",
                            DEFAULT_INITIAL_BUILDINGS_SET_ID)
                .when()
                .post(URL_CREATE_NEW_GAME)
                .then()
                .statusCode(403);

        logoutUser(userToken);

        // with old token
        createNewGame(userToken, true)
                .then()
                .statusCode(403);
    }

    @Test
    public void should_fail_when_target_victory_points_fewer_than_min() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);

        createNewGame(userToken, false, MIN_TARGET_VICTORY_POINTS - 1)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    @Test
    public void should_fail_when_initial_buildings_set_is_not_exist() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);

        createNewGame(userToken, false, DEFAULT_TARGET_VICTORY_POINTS, -1)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

}