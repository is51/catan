package catan.controllers.testcases.game;

import catan.config.ApplicationConfig;
import catan.controllers.ctf.Scenario;
import catan.controllers.util.GameTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;


@RunWith(SpringJUnit4ClassRunner.class)
//Add it if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = {ApplicationConfig.class, RequestResponseLogger.class})
@SpringApplicationConfiguration(classes = {ApplicationConfig.class})
@WebIntegrationTest("server.port:8091")
public class StartGameTest extends GameTestUtil {

    public static final String USER_NAME_1 = "user1_StartGameTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_StartGameTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_StartGameTest";
    public static final String USER_PASSWORD_3 = "password3";

    private static boolean initialized = false;

    private Scenario scenario;

    @Before
    public void setup() {
        scenario = new Scenario();

        if (!initialized) {
            scenario
                    .registerUser(USER_NAME_1, USER_PASSWORD_1)
                    .registerUser(USER_NAME_2, USER_PASSWORD_2)
                    .registerUser(USER_NAME_3, USER_PASSWORD_3);
            initialized = true;
        }
    }

    @Test
    public void should_show_log_message_when_game_started() {
        scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)


                .createNewPublicGameByUser(USER_NAME_1)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)

                // take last player from the list each time, when pulling move order from the list to have order: 3, 2, 1
                .nextRandomMoveOrderValues(asList(3, 2, 1))

                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3)

                .getGameDetails(1).statusIsPlaying()
                .gameUser(1).hasLogWithCode("START_GAME").hasMessage("You start the game!").isDisplayedOnTop()

                .getGameDetails(2)
                .gameUser(2).hasLogWithCode("START_GAME").hasMessage(scenario.getUsername(1) + " starts the game!").isDisplayedOnTop()

                .getGameDetails(3)
                .gameUser(3).hasLogWithCode("START_GAME").hasMessage(scenario.getUsername(1) + " starts the game!").isDisplayedOnTop();
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
    public void should_successfully_set_move_order_to_players_when_game_starts() {
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

        viewGame(userToken3, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers.moveOrder", everyItem(both(
                        greaterThan(0))
                        .and(
                                lessThanOrEqualTo(TEMPORARY_MAX_PLAYERS))))
                .body("gameUsers.moveOrder[0]", not(isOneOf("gameUsers.moveOrder[1]", "gameUsers.moveOrder[2]")))
                .body("gameUsers.moveOrder[1]", not(equalTo("gameUsers.moveOrder[2]")));
    }

    @Test
    public void should_successfully_run_if_move_order_is_null_before_game_starts() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false) // set here minPlayers = 3 when that feature is available
                .path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        viewGame(userToken3, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers.moveOrder", everyItem(is(0)));
    }

    @Test
    public void should_successfully_set_active_player_when_game_started() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false) // set here minPlayers = 3 when that feature is available
                .path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        viewGame(userToken3, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", nullValue());

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        viewGame(userToken3, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", equalTo(1));
    }

    @Test
    public void should_successfully_set_resources_and_development_cards_to_zero_when_game_starts() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false) // set here minPlayers = 3 when that feature is available
                .path("gameId");

        // Check that user already has resources and dev cards when game is not started.
        // Maybe it should be changed to populate resources just before game is started
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("status", equalTo("NEW"))
                .rootPath("gameUsers[0].resources")
                .body("brick", is(0))
                .body("wood", is(0))
                .body("sheep", is(0))
                .body("wheat", is(0))
                .body("stone", is(0))
                .rootPath("gameUsers[0].developmentCards")
                .body("knight", is(0))
                .body("victoryPoint", is(0))
                .body("roadBuilding", is(0))
                .body("monopoly", is(0))
                .body("yearOfPlenty", is(0))
                .rootPath("gameUsers[0].achievements")
                .body("displayVictoryPoints", is(0))
                .body("totalResources", is(0))
                .body("totalCards", is(0))
                .body("totalUsedKnights", is(0))
                .body("longestWayLength", is(0));


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
                .body("gameUsers[0].developmentCards.knight", is(0))
                .body("gameUsers[0].developmentCards.victoryPoint", is(0))
                .body("gameUsers[0].developmentCards.roadBuilding", is(0))
                .body("gameUsers[0].developmentCards.monopoly", is(0))
                .body("gameUsers[0].developmentCards.yearOfPlenty", is(0))
                .body("gameUsers[1].resources", nullValue())
                .body("gameUsers[1].developmentCards", nullValue())
                .body("gameUsers[2].resources", nullValue())
                .body("gameUsers[2].developmentCards", nullValue())
                .body("gameUsers.achievements.displayVictoryPoints", everyItem(is(0)))
                .body("gameUsers.achievements.totalResources", everyItem(is(0)))
                .body("gameUsers.achievements.totalCards", everyItem(is(0)))
                .body("gameUsers.achievements.totalUsedKnights", everyItem(is(0)))
                .body("gameUsers.achievements.longestWayLength", everyItem(is(0)))
                .body("status", equalTo("PLAYING"));

        viewGame(userToken2, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[0].resources", nullValue())
                .body("gameUsers[0].developmentCards", nullValue())
                .body("gameUsers[1].resources.brick", is(0))
                .body("gameUsers[1].resources.wood", is(0))
                .body("gameUsers[1].resources.sheep", is(0))
                .body("gameUsers[1].resources.wheat", is(0))
                .body("gameUsers[1].resources.stone", is(0))
                .body("gameUsers[1].developmentCards.knight", is(0))
                .body("gameUsers[1].developmentCards.victoryPoint", is(0))
                .body("gameUsers[1].developmentCards.roadBuilding", is(0))
                .body("gameUsers[1].developmentCards.monopoly", is(0))
                .body("gameUsers[1].developmentCards.yearOfPlenty", is(0))
                .body("gameUsers[2].resources", nullValue())
                .body("gameUsers[2].developmentCards", nullValue())
                .body("gameUsers.achievements.displayVictoryPoints", everyItem(is(0)))
                .body("gameUsers.achievements.totalResources", everyItem(is(0)))
                .body("gameUsers.achievements.totalCards", everyItem(is(0)))
                .body("gameUsers.achievements.totalUsedKnights", everyItem(is(0)))
                .body("gameUsers.achievements.longestWayLength", everyItem(is(0)))
                .body("status", equalTo("PLAYING"));

        viewGame(userToken3, gameId)
                .then()
                .statusCode(200)
                .body("gameUsers[0].resources", nullValue())
                .body("gameUsers[0].developmentCards", nullValue())
                .body("gameUsers[1].resources", nullValue())
                .body("gameUsers[1].developmentCards", nullValue())
                .body("gameUsers[2].resources.brick", is(0))
                .body("gameUsers[2].resources.wood", is(0))
                .body("gameUsers[2].resources.sheep", is(0))
                .body("gameUsers[2].resources.wheat", is(0))
                .body("gameUsers[2].resources.stone", is(0))
                .body("gameUsers[2].developmentCards.knight", is(0))
                .body("gameUsers[2].developmentCards.victoryPoint", is(0))
                .body("gameUsers[2].developmentCards.roadBuilding", is(0))
                .body("gameUsers[2].developmentCards.monopoly", is(0))
                .body("gameUsers[2].developmentCards.yearOfPlenty", is(0))
                .body("gameUsers.achievements.displayVictoryPoints", everyItem(is(0)))
                .body("gameUsers.achievements.totalResources", everyItem(is(0)))
                .body("gameUsers.achievements.totalCards", everyItem(is(0)))
                .body("gameUsers.achievements.totalUsedKnights", everyItem(is(0)))
                .body("gameUsers.achievements.longestWayLength", everyItem(is(0)))
                .body("status", equalTo("PLAYING"));
    }
}