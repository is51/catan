package catan.controllers.play;

import catan.controllers.game.GameTestUtil;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;

public abstract class PlayTestUtil extends GameTestUtil {

    protected static final String URL_BUILD_SETTLEMENT = "/api/play/build/settlement";

    protected Response buildSettlement(String token, int gameId, int nodeId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId, "nodeId", nodeId)
                .when()
                .post(URL_BUILD_SETTLEMENT);
    }

}