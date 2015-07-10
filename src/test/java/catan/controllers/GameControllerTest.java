package catan.controllers;

import catan.config.ApplicationConfig;
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
import static org.hamcrest.Matchers.lessThanOrEqualTo;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class GameControllerTest {
    public static final int SERVER_PORT = 8091;
    public static final String ACCEPT_CONTENT_TYPE = "application/json";

    public static final String URL_CREATE_NEW_GAME = "/api/game/create";
    public static final String URL_CURRENT_GAMES_LIST = "/api/game/list/current";
    public static final String URL_PUBLIC_GAMES_LIST = "/api/game/list/public";

    public static final String USER_NAME = "test game";
    public static final String USER_PASSWORD = "test game";

    private static boolean initialized = false;

    private void registerUser(String username, String password) {
        given()
            .port(SERVER_PORT)
            .header("Accept", ACCEPT_CONTENT_TYPE)
            .parameters("username", username, "password", password)
        .when()
            .post("/api/user/register")
        .then()
            .statusCode(200);
    }

    private String loginUser(String username, String password) {
        return
            given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("username", username, "password", password)
                .when()
                .post("/api/user/login")
            .then()
                .statusCode(200)
            .extract()
                .path("token");
    }

    private void logoutUser(String token) {
        given()
            .port(SERVER_PORT)
            .header("Accept", ACCEPT_CONTENT_TYPE)
            .parameters("token", token)
        .when()
            .post("/api/user/logout")
        .then()
            .statusCode(200);
    }

    private int getUserId(String token) {
        return
            given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token)
            .when()
                .post("/api/user/details")
            .then()
                .statusCode(200)
            .extract()
                .path("id");
    }

    @Before
    public void setup(){
        if(!initialized){
            registerUser(USER_NAME, USER_PASSWORD);
            initialized = true;
        }
    }

    // ----------------
    // Create New Game
    // ----------------

    @Test
    public void should_successfully_create_new_game(){
        String userToken = loginUser(USER_NAME, USER_PASSWORD);
        int userId = getUserId(userToken);

        given()
            .port(SERVER_PORT)
            .header("Accept", ACCEPT_CONTENT_TYPE)
            .parameters("token", userToken, "privateGame", true)
        .when()
            .post(URL_CREATE_NEW_GAME)
        .then()
            .statusCode(200)
            .contentType(ACCEPT_CONTENT_TYPE)
            .body("gameId", greaterThan(0))
                .and()
            .body("creatorId", equalTo(userId))
                .and()
            .body("privateGame", equalTo(true))
                .and()
            .body("dateCreated", is(both(   //closeTo can be applied only to 'double' values, but dateCreated is 'long' value
                    greaterThan(System.currentTimeMillis() - 60000))
                .and(
                    lessThanOrEqualTo(System.currentTimeMillis())))); // +-1 minute

    }

    @Test
    public void should_fail_with_error_when_there_is_no_private_game_variable(){
        String userToken = loginUser(USER_NAME, USER_PASSWORD);

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
    public void should_fail_with_403_error_when_user_is_not_authorized(){
        String userToken = loginUser(USER_NAME, USER_PASSWORD);

        // without token

        given()
            .port(SERVER_PORT)
            .header("Accept", ACCEPT_CONTENT_TYPE)
            .parameters("privateGame", true) // 'privateGame' is mandatory, 'token' is not mandatory
        .when()
            .post(URL_CREATE_NEW_GAME)
        .then()
            .statusCode(403);

        // with old token

        logoutUser(userToken);

        given()
            .port(SERVER_PORT)
            .header("Accept", ACCEPT_CONTENT_TYPE)
            .parameters("token", userToken,"privateGame", true)
        .when()
            .post(URL_CREATE_NEW_GAME)
        .then()
            .statusCode(403);
    }

    // ----------------------
    // List of Current Games
    // ----------------------


    /*@Test
    public void should_get_current_games_list_with_special_parameters(){
        //String userToken = loginUser(USER_NAME, USER_PASSWORD);
        //int userId = getUserId(userToken);

        //TODO: check properties of all objects in array (gameId, creatorId, privateGame, dateCreated)
    }*/

    @Test
    public void should_fail_with_403_error_when_getting_current_games_list_if_user_is_not_authorized(){
        String userToken = loginUser(USER_NAME, USER_PASSWORD);

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

    /*@Test
    public void should_get_public_games_list_with_special_parameters(){

        //TODO: check properties of all objects in array (gameId, creatorId, privateGame, dateCreated)
    }*/

    @Test
    public void should_get_public_games_list_even_if_user_is_not_authorized(){
        String userToken = loginUser(USER_NAME, USER_PASSWORD);

        // without token

        given()
            .port(SERVER_PORT)
            .header("Accept", ACCEPT_CONTENT_TYPE)
        .when()
            .post(URL_PUBLIC_GAMES_LIST)
        .then()
            .statusCode(200);

        // with old token

        logoutUser(userToken);

        given()
            .port(SERVER_PORT)
            .header("Accept", ACCEPT_CONTENT_TYPE)
            .parameters("token", userToken)
        .when()
            .post(URL_PUBLIC_GAMES_LIST)
        .then()
            .statusCode(200);
    }


}