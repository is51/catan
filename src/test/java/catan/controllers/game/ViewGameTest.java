package catan.controllers.game;

import catan.config.ApplicationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringJUnit4ClassRunner.class)
//Add it if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = {ApplicationConfig.class, RequestResponseLogger.class})
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class ViewGameTest extends GameTestUtil {

    public static final String USER_NAME_1 = "user1_ViewGameTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_ViewGameTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final int DEFAULT_ROUND_MAP_SIZE = 2;

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
                        greaterThan(System.currentTimeMillis() - 60000)).and(
                        lessThanOrEqualTo(System.currentTimeMillis()))))
                .body("status", not(equalTo(isEmptyString())))
                .body("gameUsers.user.id", hasItems(userId))
                .body("targetVictoryPoints", equalTo(DEFAULT_TARGET_VICTORY_POINTS))
                .body("minPlayers", equalTo(TEMPORARY_MIN_PLAYERS))
                .body("maxPlayers", equalTo(TEMPORARY_MAX_PLAYERS))

                .body("map.edges.edgeId", everyItem(greaterThan(0)))
                .body("map.edges.orientation", everyItem(anyOf(
                        equalTo("BOTTOM_RIGHT"),
                        equalTo("VERTICAL"),
                        equalTo("BOTTOM_LEFT"))))

                .body("map.hexes.hexId", everyItem(greaterThan(0)))
                .body("map.hexes.x", everyItem(both(
                        greaterThanOrEqualTo(-DEFAULT_ROUND_MAP_SIZE)).and(
                        lessThanOrEqualTo(DEFAULT_ROUND_MAP_SIZE))))
                .body("map.hexes.y", everyItem(both(
                        greaterThanOrEqualTo(-DEFAULT_ROUND_MAP_SIZE)).and(
                        lessThanOrEqualTo(DEFAULT_ROUND_MAP_SIZE))))
                .body("map.hexes.type.findAll {it == 'BRICK'}", hasSize(3))
                .body("map.hexes.type.findAll {it == 'WOOD'}", hasSize(4))
                .body("map.hexes.type.findAll {it == 'SHEEP'}", hasSize(4))
                .body("map.hexes.type.findAll {it == 'WHEAT'}", hasSize(4))
                .body("map.hexes.type.findAll {it == 'STONE'}", hasSize(3))
                .body("map.hexes.type.findAll {it == 'EMPTY'}", hasSize(1))
                .body("map.hexes.dice.findAll {it == 2}", hasSize(1))
                .body("map.hexes.dice.findAll {it == 3}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 4}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 5}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 6}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 7}", hasSize(1))
                .body("map.hexes.dice.findAll {it == 8}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 9}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 10}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 11}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 12}", hasSize(1))
                .body("map.hexes.robbed.findAll {it == true}", hasSize(1))

                .body("map.nodes.nodeId", everyItem(greaterThan(0)))
                .body("map.nodes.port.findAll {it == 'BRICK'}", hasSize(2))
                .body("map.nodes.port.findAll {it == 'WOOD'}", hasSize(2))
                .body("map.nodes.port.findAll {it == 'SHEEP'}", hasSize(2))
                .body("map.nodes.port.findAll {it == 'WHEAT'}", hasSize(2))
                .body("map.nodes.port.findAll {it == 'STONE'}", hasSize(2))
                .body("map.nodes.port.findAll {it == 'ANY'}", hasSize(8))
                .body("map.nodes.port", everyItem(anyOf(
                        equalTo("BRICK"),
                        equalTo("WOOD"),
                        equalTo("SHEEP"),
                        equalTo("WHEAT"),
                        equalTo("STONE"),
                        equalTo("NONE"),
                        equalTo("ANY"))))
                .body("map.nodes.orientation", everyItem(anyOf(
                        equalTo("SINGLE_TOP"),
                        equalTo("SINGLE_BOTTOM"))));
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
                        greaterThan(0)).and(
                        not(equalTo(userId2)))))
                .body("dateCreated", is(both(
                        greaterThan(System.currentTimeMillis() - 60000)).and(
                        lessThanOrEqualTo(System.currentTimeMillis()))))
                .body("status", not(equalTo(isEmptyString())))
                .body("gameUsers.user.id", hasItems(userId1, userId2))
                .body("targetVictoryPoints", equalTo(DEFAULT_TARGET_VICTORY_POINTS))
                .body("minPlayers", equalTo(TEMPORARY_MIN_PLAYERS))
                .body("maxPlayers", equalTo(TEMPORARY_MAX_PLAYERS))

                .body("map.edges.edgeId", everyItem(greaterThan(0)))
                .body("map.edges.orientation", everyItem(anyOf(
                        equalTo("BOTTOM_RIGHT"),
                        equalTo("VERTICAL"),
                        equalTo("BOTTOM_LEFT"))))

                .body("map.hexes.hexId", everyItem(greaterThan(0)))
                .body("map.hexes.x", everyItem(both(
                        greaterThanOrEqualTo(-DEFAULT_ROUND_MAP_SIZE)).and(
                        lessThanOrEqualTo(DEFAULT_ROUND_MAP_SIZE))))
                .body("map.hexes.y", everyItem(both(
                        greaterThanOrEqualTo(-DEFAULT_ROUND_MAP_SIZE)).and(
                        lessThanOrEqualTo(DEFAULT_ROUND_MAP_SIZE))))
                .body("map.hexes.type.findAll {it == 'BRICK'}", hasSize(3))
                .body("map.hexes.type.findAll {it == 'WOOD'}", hasSize(4))
                .body("map.hexes.type.findAll {it == 'SHEEP'}", hasSize(4))
                .body("map.hexes.type.findAll {it == 'WHEAT'}", hasSize(4))
                .body("map.hexes.type.findAll {it == 'STONE'}", hasSize(3))
                .body("map.hexes.type.findAll {it == 'EMPTY'}", hasSize(1))
                .body("map.hexes.dice.findAll {it == 2}", hasSize(1))
                .body("map.hexes.dice.findAll {it == 3}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 4}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 5}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 6}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 7}", hasSize(1))
                .body("map.hexes.dice.findAll {it == 8}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 9}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 10}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 11}", hasSize(2))
                .body("map.hexes.dice.findAll {it == 12}", hasSize(1))
                .body("map.hexes.robbed.findAll {it == true}", hasSize(1))

                .body("map.nodes.nodeId", everyItem(greaterThan(0)))
                .body("map.nodes.port.findAll {it == 'BRICK'}", hasSize(2))
                .body("map.nodes.port.findAll {it == 'WOOD'}", hasSize(2))
                .body("map.nodes.port.findAll {it == 'SHEEP'}", hasSize(2))
                .body("map.nodes.port.findAll {it == 'WHEAT'}", hasSize(2))
                .body("map.nodes.port.findAll {it == 'STONE'}", hasSize(2))
                .body("map.nodes.port.findAll {it == 'ANY'}", hasSize(8))
                .body("map.nodes.port", everyItem(anyOf(
                        equalTo("BRICK"),
                        equalTo("WOOD"),
                        equalTo("SHEEP"),
                        equalTo("WHEAT"),
                        equalTo("STONE"),
                        equalTo("NONE"),
                        equalTo("ANY"))))
                .body("map.nodes.orientation", everyItem(anyOf(
                        equalTo("SINGLE_TOP"),
                        equalTo("SINGLE_BOTTOM"))));
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