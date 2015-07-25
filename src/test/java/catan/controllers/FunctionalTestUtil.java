// TODO: remove checking of statusCode in each method. (Needs to be approved)

package catan.controllers;

import static com.jayway.restassured.RestAssured.given;
import com.jayway.restassured.response.Response;

public abstract class FunctionalTestUtil {
    public static final int SERVER_PORT = 8091;
    public static final String ACCEPT_CONTENT_TYPE = "application/json";

    protected void registerUser(String username, String password) {
        given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("username", username, "password", password)
                .when()
                .post("/api/user/register")
                .then()
                .statusCode(200);
    }

    protected String loginUser(String username, String password) {
        return given()
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

    protected void logoutUser(String token) {
        given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token)
                .when()
                .post("/api/user/logout")
                .then()
                .statusCode(200);
    }

    protected int getUserId(String token) {
        return given()
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

    protected Response registerAndLoginGuest(String username) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("username", username)
                .when()
                .post("/api/user/register/guest");
    }

    protected Response getUserDetails(String token) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token)
                .when()
                .post("/api/user/details");
    }

}
