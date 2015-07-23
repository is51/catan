package catan.controllers.game;

import catan.config.ApplicationConfig;
import catan.services.impl.GameServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class ViewGameTest extends GameTestUtil {

    public static final String USER_NAME_1 = "user1_ViewGameTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_ViewGameTest";
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
    public void should_successfully_view_game_page_by_creator() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");
        int userId = getUserId(userToken1);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("gameId", greaterThan(0))
                .body("creatorId", equalTo(userId))
                .body("dateCreated", is(both(
                        greaterThan(System.currentTimeMillis() - 60000))
                        .and(
                                lessThanOrEqualTo(System.currentTimeMillis()))))
                .body("status", not(equalTo(isEmptyString())))
                .body("gameUsers.user.id", hasItems(userId));
                //.body("minUsers", equalTo(GameServiceImpl.MIN_USERS))
                //.body("maxUsers", equalTo(GameServiceImpl.MAX_USERS));
    }

    @Test
    public void should_successfully_view_game_page_by_non_creator() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");
        int userId1 = getUserId(userToken1);

        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        int userId2 = getUserId(userToken2);

        joinPublicGame(userToken2, gameId);

        viewGame(userToken2, gameId)
                .then()
                .statusCode(200)
                .body("gameId", greaterThan(0))
                .body("creatorId", is(both(
                        greaterThan(0))
                        .and(
                                not(equalTo(userId2)))))
                .body("dateCreated", is(both(
                        greaterThan(System.currentTimeMillis() - 60000))
                        .and(
                                lessThanOrEqualTo(System.currentTimeMillis()))))
                .body("status", not(equalTo(isEmptyString())))
                .body("gameUsers.user.id", hasItems(userId1, userId2));
                //.body("minUsers", equalTo(GameServiceImpl.MIN_USERS))
                //.body("maxUsers", equalTo(GameServiceImpl.MAX_USERS));
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
                .body("errorCode", equalTo("USER_IS_NOT_JOINED"));
    }

    @Test
    public void should_fail_when_user_view_game_page_of_nonexistent_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);

        viewGame(userToken1, 999999999)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("GAME_IS_NOT_FOUND"));
    }

    @Test
    public void should_fail_when_user_view_game_page_if_he_is_not_authorized() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");
        logoutUser(userToken1);

        viewGame("some invalid token", gameId)
                .then()
                .statusCode(403);
    }

    @Test
    public void should_fail_when_user_view_canceled_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false)
                .path("gameId");

        cancelGame(userToken1, gameId);

        viewGame(userToken1, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("GAME_CANCELED"));
    }
}