package catan.controllers.game;

import catan.controllers.FunctionalTestUtil;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;

public abstract class GameTestUtil extends FunctionalTestUtil {

    protected static final String URL_CREATE_NEW_GAME = "/api/game/create";
    protected static final String URL_JOIN_PUBLIC_GAME = "/api/game/join/public";
    protected static final String URL_JOIN_PRIVATE_GAME = "/api/game/join/private";
    public static final String URL_CURRENT_GAMES_LIST = "/api/game/list/current";
    public static final String URL_PUBLIC_GAMES_LIST = "/api/game/list/public";
    public static final String URL_LEAVE_GAME = "/api/game/leave";

    protected Response createNewGame(String token, boolean privateGame) {
        return given()
                .port(SERVER_PORT)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("token", token, "privateGame", privateGame)
                .when()
                .post(URL_CREATE_NEW_GAME);
    }

    protected Response joinPublicGame(String token, int gameId) {
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
}
