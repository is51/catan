package catan.controllers.game;

import catan.config.ApplicationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.equalTo;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class CancelGameTest extends GameTestUtil {

    public static final String USER_NAME_1 = "user1_CancelGameTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_CancelGameTest";
    public static final String USER_PASSWORD_2 = "password2";

    private static boolean initialized = false;

    @Before
    public void setup() {
        if (!initialized) {
            registerUser(USER_NAME_1, USER_PASSWORD_1);
            registerUser(USER_NAME_2, USER_PASSWORD_2);
            initialized = true;
        }
    }

    @Test
    public void should_successfully_cancel_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        joinPublicGame(userToken2, gameId);

        cancelGame(userToken1, gameId)
                .then()
                .statusCode(200);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(400);
        //.body("errorCode", equalTo("GAME_IS_NOT_FOUND")); TODO: check errorCode, later should be "GAME_CANCELED"

        viewGame(userToken2, gameId)
                .then()
                .statusCode(400);
        //.body("errorCode", equalTo("GAME_IS_NOT_FOUND")); TODO: check errorCode, later should be "GAME_CANCELED"
    }

    @Test
    public void should_fails_when_user_cancel_not_his_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);

        cancelGame(userToken2, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    @Test
    public void should_fails_when_user_cancels_nonexistent_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);

        cancelGame(userToken1, 999999999)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    @Test
    public void should_fails_when_user_cancel_canceled_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        cancelGame(userToken1, gameId);

        cancelGame(userToken1, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    /*@Test
    public void should_fails_when_user_cancels_already_started_game() {
        //TODO: should_fails_when_user_cancel_already_started_game
        //needs to be written when game starting is implemented
    }*/

    /*@Test
    public void should_fails_when_user_cancels_finished_game() {
        //TODO: should_fails_when_user_cancels_finished_game
        //needs to be written when game finishing is implemented
    }*/
}