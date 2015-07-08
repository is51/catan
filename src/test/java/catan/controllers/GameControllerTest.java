package catan.controllers;

import catan.config.ApplicationConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.isA;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class GameControllerTest {
    public static final int SERVER_PORT = 8091;
    public static final String CONTENT_TYPE = "application/json";

    public static final String USER_NAME = "test game";
    public static final String USER_PASSWORD = "test game";

    public static final String URL_CREATE_NEW_GAME = "/api/game/create";

    private void registerUser(String username, String password) {
        given().
                port(SERVER_PORT).
                header("Content-Type", CONTENT_TYPE).
                parameters("username", username, "password", password).
        when().
                post("/api/user/register").
        then().
                statusCode(200).
                contentType(CONTENT_TYPE);
    }

    private String loginUser(String username, String password) {
        return
            given().
                    port(SERVER_PORT).
                    header("Content-Type", CONTENT_TYPE).
                    parameters("username", username, "password", password).
            when().
                    post("/api/user/login").
            then().
                    statusCode(200).
                    contentType(CONTENT_TYPE).
            extract()
                    .path("token");
    }

    private void logoutUser(String token) {
        given().
                port(SERVER_PORT).
                header("Content-Type", CONTENT_TYPE).
                parameters("token", token).
        when().
                post("/api/user/logout").
        then().
                statusCode(200).
                contentType(CONTENT_TYPE);
    }

    private int getUserId(String token) {
        return
            given().
                    port(SERVER_PORT).
                    header("Content-Type", CONTENT_TYPE).
                    parameters("token", token).
            when().
                    post("/api/user/details").
            then().
                    statusCode(200).
                    contentType(CONTENT_TYPE).
            extract()
                    .path("id");
    }

    @BeforeClass
    public void setup(){
        registerUser(USER_NAME, USER_PASSWORD);
    }

    // ----------------
    // Create New Game
    // ----------------

    @Test
    public void shouldSuccessfullyCreateNewGame(){

        String userToken = loginUser(USER_NAME, USER_PASSWORD);
        int userId = getUserId(userToken);

        given().
                port(SERVER_PORT).
                header("Content-Type", CONTENT_TYPE).
                parameters("token", userToken, "privateGame", true).
        when().
                post(URL_CREATE_NEW_GAME).
        then().
                statusCode(200).
                contentType(CONTENT_TYPE).
                body("gameId", isA(int)).
                and().
                body("creatorId", equalTo(userId)).
                and().
                body("privateGame", equalTo(true)).
                and().
                body("dateCreated", lessThanOrEqualTo(System.currentTimeMillis()))
                and().
                body("dateCreated", greaterThanOrEqualTo(System.currentTimeMillis() - 1 * 60 * 1000)); // 1 minute
    }

    @Test
    public void shouldFailWithErrorWhenThereIsNoPrivateGameVariable(){

        String userToken = loginUser(USER_NAME, USER_PASSWORD);

        given().
                port(SERVER_PORT).
                header("Content-Type", CONTENT_TYPE).
                parameters("token", userToken).
        when().
                post(URL_CREATE_NEW_GAME).
        then().
                statusCode(400);
    }

    @Test
    public void shouldFailWith403ErrorWhenUserIsNotAuthorized(){

        String userToken = loginUser(USER_NAME, USER_PASSWORD);

        // without token

        given().
                port(SERVER_PORT).
                header("Content-Type", CONTENT_TYPE).
        when().
                post(URL_CREATE_NEW_GAME).
        then().
                statusCode(403);

        // with old token

        logoutUser(userToken);

        given().
                port(SERVER_PORT).
                header("Content-Type", CONTENT_TYPE).
                parameters("token", userToken).
        when().
                post(URL_CREATE_NEW_GAME).
        then().
                statusCode(403);
    }

    // ----------------------
    // List of Current Games
    // ----------------------

    // ..................
    // ..............
    // ..........


}