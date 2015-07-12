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
public class ViewGameTest extends GameTestUtil {

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
    public void creator_should_successfully_view_game_page() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200);
    }

    @Test
    public void non_creator_should_successfully_view_game_page() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        joinPublicGame(userToken2, gameId);

        viewGame(userToken2, gameId)
                .then()
                .statusCode(200);
    }

    @Test
    public void should_fail_when_user_view_game_page_if_he_is_not_joined() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);

        viewGame(userToken2, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ACCESS_DENIED"));
    }

    @Test
    public void should_fail_when_user_view_game_page_of_nonexistent_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);

        viewGame(userToken1, 999999999)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    @Test
    public void should_fail_when_user_view_game_page_if_he_is_not_authorized() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");
        logoutUser(userToken1);

        viewGame("some invalid token", gameId)
                .then()
                .statusCode(403)
                .body("errorCode", equalTo("ERROR"));
    }
}