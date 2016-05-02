package catan.controllers.util;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;

public abstract class FunctionalTestUtil {
    // TODO: remove checking of statusCode in each method. (Needs to be approved)

    public static final String ACCEPT_CONTENT_TYPE = "application/json";
    public static final String GLOBAL_UNIQUE_USERNAME_SUFFIX = "_" + (long)(Math.random() * Long.MAX_VALUE);

    public static void registerUser(String username, String password) {
        given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("username", username + GLOBAL_UNIQUE_USERNAME_SUFFIX, "password", password)
                .when()
                .post("/api/user/register")
                .then()
                .statusCode(200);
    }

    public static String loginUser(String username, String password) {
        return given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("username", username + GLOBAL_UNIQUE_USERNAME_SUFFIX, "password", password)
                .when()
                .post("/api/user/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    protected void logoutUser(String token) {
        given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token)
                .when()
                .post("/api/user/logout")
                .then()
                .statusCode(200);
    }

    protected int getUserId(String token) {
        return given()
                .port(RestAssured.port)
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
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("username", username.isEmpty() ? "" : username + GLOBAL_UNIQUE_USERNAME_SUFFIX)
                .when()
                .post("/api/user/register/guest");
    }

    protected Response getUserDetails(String token) {
        return given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token)
                .when()
                .post("/api/user/details");
    }

    public static String toGlobalName(String name){
        return name == null || name.isEmpty() ? "" : name + FunctionalTestUtil.GLOBAL_UNIQUE_USERNAME_SUFFIX;
    }

}
