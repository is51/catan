package catan.controllers;

import catan.controllers.util.EmbeddedServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;


public class UserControllerTest {
    public static final int SERVER_PORT = 8091;
    private static EmbeddedServer embeddedServer;

    @BeforeClass
    public static void startTomcat() throws Exception {
        embeddedServer = new EmbeddedServer(SERVER_PORT, "/");
        embeddedServer.start();
    }

    @AfterClass
    public static void stopServer() {
        embeddedServer.stop();
    }

    @Test
    public void shouldSuccessfullyRegisterNewUserAndFailRegistrationWithHttpCode400WhenSuchUserAlreadyExists(){
        //Registering new User
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("username", "user1", "password", "123").
        when().
                post("/api/user/register").
        then().
                statusCode(200).
                body(isEmptyString());

        //Registering user with existing username
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("username", "user1", "password", "123").
        when().
                post("/api/user/register").
        then().
                statusCode(400).
                contentType("application/json").
                body("errorCode", equalTo("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    public void shouldFailWithUserNotFoundErrorWithHttpCode400WhenLoginWithWrongPassword() {
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("username", "user1", "password", "00000").
        when().
                post("/api/user/login").
        then().
                statusCode(400).
                contentType("application/json").
                body("errorCode", equalTo("INCORRECT_LOGIN_PASSWORD"));
    }

    @Test
    public void shouldFailWithCommonErrorWithHttpCode400WhenLoginWithIncorrectParameters() {
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("username", "", "password", "").
        when().
                post("/api/user/login").
        then().
                statusCode(400).
                contentType("application/json").
                body("errorCode", equalTo("ERROR"));
    }

    @Test
    public void shouldReturnSessionTokenWithHttpCode200AndReturnUserDetailsWithHttpCode200WhenRegisterLoginAndGetDetails() {
        //Registering new User
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("username", "user2", "password", "123").
        when().
                post("/api/user/register").
        then().
                statusCode(200).
                body(isEmptyString());

        //Login user and extract token from response
        String token =
        given().
                port(8091).
                header("Accept-Encoding", "application/json").
                parameters("username", "user2", "password", "123").
        when().
                post("/api/user/login").
        then().
                statusCode(200).
                contentType("application/json").
        extract()
                .path("token");

        //Get details by extracted token
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("token", token).
        when().
                post("/api/user/details").
        then().
                statusCode(200).
                contentType("application/json").
                body("username", equalTo("user2"));
    }

    @Test
    public void shouldFailWithForbiddenResponseBodyWithHttpCode403WhenTryingToGetDetailsForInvalidToken() {
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("token", "12345").
        when().
                post("/api/user/details").
        then().
                statusCode(403);
    }

    @Test
    public void shouldSuccessLogoutWithHttpCode400WhenAnyTokenIsPassed() {
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("token", "12345").
        when().
                post("/api/user/logout").
        then().
                statusCode(200).
                body(isEmptyString());
    }
}