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
public class LeaveGameTest extends GameTestUtil {

    public static final String USER_NAME_1 = "user1_LeaveGameTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_LeaveGameTest";
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
    public void should_successfully_leave_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        joinPublicGame(userToken2, gameId);

        leaveGame(userToken2, gameId)
                .then()
                .statusCode(200);

        viewGame(userToken2, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("USER_IS_NOT_JOINED"));
    }

    @Test
    public void should_fails_when_user_leaves_game_if_he_is_not_joined() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);

        leaveGame(userToken2, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    @Test
    public void should_fails_when_user_leaves_nonexistent_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);

        leaveGame(userToken1, 999999999)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    /*@Test
    public void should_fails_when_user_leaves_already_started_game() {
        //TODO: should_fails_when_user_leaves_already_started_game
        //needs to be written when game starting is implemented
    }*/

    /*@Test
    public void should_fails_when_user_leaves_finished_game() {
        //TODO: should_fails_when_user_leaves_finished_game
    }*/
}