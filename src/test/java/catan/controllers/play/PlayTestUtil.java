package catan.controllers.play;

import catan.controllers.game.GameTestUtil;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;

public abstract class PlayTestUtil extends GameTestUtil {

    protected static final String URL_BUILD_ROAD = "/api/play/build/road";
    protected static final String URL_BUILD_SETTLEMENT = "/api/play/build/settlement";
    protected static final String URL_BUILD_CITY = "/api/play/build/city";
    protected static final String URL_END_TURN = "/api/play/end-turn";
    
    protected Response buildSettlement(String token, int gameId, int nodeId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId, "nodeId", nodeId)
                .when()
                .post(URL_BUILD_SETTLEMENT);
    }

    protected Response buildCity(String token, int gameId, int nodeId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId, "nodeId", nodeId)
                .when()
                .post(URL_BUILD_CITY);
    }

    protected Response buildRoad(String token, int gameId, int edgeId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId, "edgeId", edgeId)
                .when()
                .post(URL_BUILD_ROAD);
    }

    protected Response endTurn(String token, int gameId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId)
                .when()
                .post(URL_END_TURN);
    }

}
