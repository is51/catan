package catan.controllers.game;

import catan.config.ApplicationConfig;
import catan.domain.model.game.types.GameStatus;
import catan.domain.transfer.output.game.GameDetails;
import catan.services.impl.GameServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
//Add it if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = {ApplicationConfig.class, RequestResponseLogger.class})
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class ListGameTest extends GameTestUtil {

    public static final String USER_NAME_1 = "user1_ListGameTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_ListGameTest";
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

    // ----------------------
    // List of Current Games
    // ----------------------


    /*@Test
    public void should_get_current_games_list_with_special_parameters(){
        //String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);
        //int userId = getUserId(userToken);

        //TODO: check properties of all objects in array (gameId, creatorId, privateGame, dateCreated)
    }*/

    @Test
    public void should_fail_with_403_error_when_getting_current_games_list_if_user_is_not_authorized() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);

        // without token
        given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .when()
                .post(URL_CURRENT_GAMES_LIST)
                .then()
                .statusCode(403);

        // with old token
        logoutUser(userToken);

        given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", userToken)
                .when()
                .post(URL_CURRENT_GAMES_LIST)
                .then()
                .statusCode(403);
    }

    // ----------------------
    // List of Public Games
    // ----------------------

    //Doesn't actually tests real game details, as games already added and more than 2 games are in response
    @Test
    public void should_get_public_games_list_with_special_parameters() {

        //TODO: change test to verify correct parameters (maybe only size?)
        // + need checking of info about players

        String firstUserToken = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String secondUserToken = loginUser(USER_NAME_2, USER_PASSWORD_2);
        int firstUserId = getUserId(firstUserToken);
        int secondUserId = getUserId(secondUserToken);

        // Assert public games empty response
        int numberOfGamesInTheList = given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", secondUserToken)
                .when()
                .post(URL_PUBLIC_GAMES_LIST)
                .then()
                .statusCode(200)
                .extract()
                .path("findall.size()");

        createNewGame(firstUserToken, false);
        createNewGame(secondUserToken, false);

        long now = System.currentTimeMillis();

        // Assert public games response entities
        given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", secondUserToken)
                .when()
                .post(URL_PUBLIC_GAMES_LIST)
                .then()
                .statusCode(200)
                .body("findall.size()", equalTo(numberOfGamesInTheList + 2))
                .body("creatorId", hasItems(firstUserId, secondUserId)) //Checks that items in the list have different creator id
                .body("[0].gameId", greaterThan(0))
                .body("[0].privateGame", equalTo(false))
                .body("[0].status", equalTo(GameStatus.NEW.toString()))
                .body("[0].dateCreated", is(both(greaterThan(now - 60000)).and(lessThanOrEqualTo(now))))
                .body("[0].targetVictoryPoints", equalTo(DEFAULT_TARGET_VICTORY_POINTS))
                .body("[0].minPlayers", equalTo(TEMPORARY_MIN_PLAYERS))
                .body("[0].maxPlayers", equalTo(TEMPORARY_MAX_PLAYERS))
                .body("[1].gameId", greaterThan(0))
                .body("[1].privateGame", equalTo(false))
                .body("[1].status", equalTo(GameStatus.NEW.toString()))
                .body("[1].dateCreated", is(both(greaterThan(now - 60000)).and(lessThanOrEqualTo(now))))
                .body("[1].targetVictoryPoints", equalTo(DEFAULT_TARGET_VICTORY_POINTS))
                .body("[1].minPlayers", equalTo(TEMPORARY_MIN_PLAYERS))
                .body("[1].maxPlayers", equalTo(TEMPORARY_MAX_PLAYERS));

        // Assert public games response again but in another way
        List<GameDetails> games = Arrays.asList(
                given()
                        .port(SERVER_PORT)
                        .header("Accept", ACCEPT_CONTENT_TYPE)
                        .parameters("token", secondUserToken)
                        .when()
                        .post(URL_PUBLIC_GAMES_LIST)
                        .as(GameDetails[].class));
        //then
        assertNotNull(games);
        assertThat("Size of list should be " + (numberOfGamesInTheList + 2), games.size(), is(numberOfGamesInTheList + 2));

        //create instances of game details after assertion that response contains 2 games
        GameDetails firstGameDetails = games.get(0);
        GameDetails secondGameDetails = games.get(1);
        assertNotEquals("Game Ids should be different", firstGameDetails.getGameId(), secondGameDetails.getGameId());

        //Assert first game details
        assertThat("Game Id should be greater than 0", firstGameDetails.getGameId(), greaterThan(0));
        assertThat("Status of first game should be NEW", firstGameDetails.getStatus(), is(GameStatus.NEW.toString()));
        assertFalse("Game should be public", firstGameDetails.isPrivateGame());
        assertThat("Date of game creation should be equal to or less than current time but not less than 60 seconds",
                firstGameDetails.getDateCreated(), is(both(greaterThan(now - 60000)).and(lessThan(now))));
        assertThat("Min users should be filled up", firstGameDetails.getMinPlayers(), equalTo(GameServiceImpl.MIN_USERS));
        assertThat("Max users should be filled up", firstGameDetails.getMaxPlayers(), equalTo(GameServiceImpl.MAX_USERS));

        //Assert second game details
        assertThat("Game Id should be greater than 0", secondGameDetails.getGameId(), greaterThan(0));
        assertThat("Status of first game should be NEW", secondGameDetails.getStatus(), is(GameStatus.NEW.toString()));
        assertFalse("Game should be public", secondGameDetails.isPrivateGame());
        assertThat("Date of game creation should be equal to or less than current time but not less than 60 seconds",
                secondGameDetails.getDateCreated(), is(both(greaterThan(now - 60000)).and(lessThan(now))));
        assertThat("Min users should be filled up", secondGameDetails.getMinPlayers(), equalTo(GameServiceImpl.MIN_USERS));
        assertThat("Max users should be filled up", secondGameDetails.getMaxPlayers(), equalTo(GameServiceImpl.MAX_USERS));

    }

    @Test
    public void should_get_public_games_list_even_if_user_is_not_authorized() {
        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);

        // without token
        given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .when()
                .post(URL_PUBLIC_GAMES_LIST)
                .then()
                .statusCode(200);

        logoutUser(userToken);

        // with old token
        given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", userToken)
                .when()
                .post(URL_PUBLIC_GAMES_LIST)
                .then()
                .statusCode(200);
    }

    @Test
    public void should_successfully_get_joined_games_list_even_if_user_is_not_creator() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);

        int gameId = createNewGame(userToken1, false)
                .path("gameId");
        createNewGame(userToken2, false);
        joinPublicGame(userToken2, gameId);

        given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", userToken1)
                .when()
                .post(URL_CURRENT_GAMES_LIST)
                .then()
                .statusCode(200)
                .body("findall.size()", is(1));

        given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", userToken2)
                .when()
                .post(URL_CURRENT_GAMES_LIST)
                .then()
                .statusCode(200)
                .body("findall.size()", is(2));
    }
}