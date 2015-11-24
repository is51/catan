package catan.controllers.util;

import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;

public abstract class PlayTestUtil extends GameTestUtil {

    protected static final String URL_BUILD_ROAD = "/api/play/build/road";
    protected static final String URL_BUILD_SETTLEMENT = "/api/play/build/settlement";
    protected static final String URL_BUILD_CITY = "/api/play/build/city";
    protected static final String URL_END_TURN = "/api/play/end-turn";
    protected static final String URL_THROW_DICE = "/api/play/throw-dice";
    protected static final String URL_BUY_CARD = "/api/play/buy/card";

    public static Response buildSettlement(String token, int gameId, int nodeId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId, "nodeId", nodeId)
                .when()
                .post(URL_BUILD_SETTLEMENT);
    }

    public static Response buildCity(String token, int gameId, int nodeId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId, "nodeId", nodeId)
                .when()
                .post(URL_BUILD_CITY);
    }

    public static Response buildRoad(String token, int gameId, int edgeId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId, "edgeId", edgeId)
                .when()
                .post(URL_BUILD_ROAD);
    }

    public static Response endTurn(String token, int gameId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId)
                .when()
                .post(URL_END_TURN);
    }

    public static Response throwDice(String token, int gameId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId)
                .when()
                .post(URL_THROW_DICE);
    }

    public static Response buyCard(String token, int gameId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId)
                .when()
                .post(URL_BUY_CARD);
    }
}
