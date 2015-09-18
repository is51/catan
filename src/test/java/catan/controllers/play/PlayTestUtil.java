package catan.controllers.play;

import catan.controllers.game.GameTestUtil;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;

public abstract class PlayTestUtil extends GameTestUtil {

    protected static final String URL_END_TURN = "/api/play/end-turn";

    protected Response endTurn(String token, int gameId) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "gameId", gameId)
                .when()
                .post(URL_END_TURN);
    }

}
