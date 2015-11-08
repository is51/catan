package catan.controllers.util;

import catan.controllers.util.FunctionalTestUtil;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;

public abstract class GameTestUtil extends FunctionalTestUtil {

    protected static final String URL_CREATE_NEW_GAME = "/api/game/create";
    protected static final String URL_JOIN_PUBLIC_GAME = "/api/game/join/public";
    protected static final String URL_JOIN_PRIVATE_GAME = "/api/game/join/private";
    public static final String URL_CURRENT_GAMES_LIST = "/api/game/list/current";
    public static final String URL_PUBLIC_GAMES_LIST = "/api/game/list/public";
    public static final String URL_LEAVE_GAME = "/api/game/leave";
    public static final String URL_CANCEL_GAME = "/api/game/cancel";
    public static final String URL_VIEW_GAME_DETAILS = "/api/game/details";
    public static final String URL_SET_USER_READY = "/api/game/ready";
    public static final String URL_SET_USER_NOT_READY = "/api/game/not-ready";

    public static final int DEFAULT_TARGET_VICTORY_POINTS = 12;
    public static final int DEFAULT_INITIAL_BUILDINGS_SET_ID = 1;
    public static final int TEMPORARY_MIN_PLAYERS = 3;
    public static final int TEMPORARY_MAX_PLAYERS = 4;

    public static Response createNewGame(String token, boolean privateGame) {
        return createNewGame(token, privateGame, DEFAULT_TARGET_VICTORY_POINTS, DEFAULT_INITIAL_BUILDINGS_SET_ID);
    }

    public static Response createNewGame(String token, boolean privateGame, int targetVictoryPoints) {
        return createNewGame(token, privateGame, targetVictoryPoints, DEFAULT_INITIAL_BUILDINGS_SET_ID);
    }

    protected static Response createNewGame(String token, boolean privateGame, int targetVictoryPoints, int initialBuildingsSetId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "privateGame", privateGame, "targetVictoryPoints", targetVictoryPoints, "initialBuildingsSetId", initialBuildingsSetId)
                .when()
                .post(URL_CREATE_NEW_GAME);
    }

    public static Response joinPublicGame(String token, int gameId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId)
                .when()
                .post(URL_JOIN_PUBLIC_GAME);
    }

    protected Response joinPrivateGame(String token, String privateCode) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "privateCode", privateCode)
                .when()
                .post(URL_JOIN_PRIVATE_GAME);
    }

    protected Response leaveGame(String token, int gameId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId)
                .when()
                .post(URL_LEAVE_GAME);
    }

    protected Response cancelGame(String token, int gameId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId)
                .when()
                .post(URL_CANCEL_GAME);
    }

    public static Response viewGame(String token, int gameId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId)
                .when()
                .post(URL_VIEW_GAME_DETAILS);
    }

    public static Response setUserReady(String token, int gameId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId)
                .when()
                .post(URL_SET_USER_READY);
    }

    protected Response setUserNotReady(String token, int gameId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId)
                .when()
                .post(URL_SET_USER_NOT_READY);
    }
}
