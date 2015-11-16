package catan.controllers.testcases.play;

import catan.controllers.ctf.TestApplicationConfig;
import catan.controllers.util.PlayTestUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.*;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class EndTurnTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_EndTurnTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_EndTurnTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_EndTurnTest";
    public static final String USER_PASSWORD_3 = "password3";
    public static final String USER_NAME_4 = "user4_EndTurnTest";
    public static final String USER_PASSWORD_4 = "password4";

    private static boolean initialized = false;

    @Before
    public void setup() {
        if (!initialized) {
            registerUser(USER_NAME_1, USER_PASSWORD_1);
            registerUser(USER_NAME_2, USER_PASSWORD_2);
            registerUser(USER_NAME_3, USER_PASSWORD_3);
            registerUser(USER_NAME_4, USER_PASSWORD_4);
            initialized = true;
        }
    }

    @Test
    public void should_successfully_end_turn() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int userId1 = getUserId(userToken1);
        int userId2 = getUserId(userToken2);
        int userId3 = getUserId(userToken3);

        int gameId = createNewGame(userToken1, false, 12, 1).path("gameId");
        int nodeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].nodesIds.topLeftId");
        int edgeIdToBuild = viewGame(userToken1, gameId).path("map.hexes[0].edgesIds.topLeftId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        int[] userIdsSortedByMoveOrder = new int[3];

        int moveOrderOfUser1 = viewGame(userToken1, gameId).path("gameUsers[0].moveOrder");
        int moveOrderOfUser2 = viewGame(userToken1, gameId).path("gameUsers[1].moveOrder");
        int moveOrderOfUser3 = viewGame(userToken1, gameId).path("gameUsers[2].moveOrder");

        userIdsSortedByMoveOrder[moveOrderOfUser1 - 1] = viewGame(userToken1, gameId).path("gameUsers[0].user.id");
        userIdsSortedByMoveOrder[moveOrderOfUser2 - 1] = viewGame(userToken1, gameId).path("gameUsers[1].user.id");
        userIdsSortedByMoveOrder[moveOrderOfUser3 - 1] = viewGame(userToken1, gameId).path("gameUsers[2].user.id");

        String userTokenOfFirstPlayer = "";

        if (userIdsSortedByMoveOrder[0] == userId1) {
            userTokenOfFirstPlayer = userToken1;
        } else
            if (userIdsSortedByMoveOrder[0] == userId2) {
                userTokenOfFirstPlayer = userToken2;
            } else
                if (userIdsSortedByMoveOrder[0] == userId3) {
                    userTokenOfFirstPlayer = userToken3;
                }

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(1))
                .body("status", equalTo("PLAYING"));

        buildSettlement(userTokenOfFirstPlayer, gameId, nodeIdToBuild);
        buildRoad(userTokenOfFirstPlayer, gameId, edgeIdToBuild);
        endTurn(userTokenOfFirstPlayer, gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(2))
                .body("status", equalTo("PLAYING"));
    }

    @Test
    public void should_successfully_end_turn_in_different_stages_and_preparationCycles_in_case_of_even_cycles_amount() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int userId1 = getUserId(userToken1);
        int userId2 = getUserId(userToken2);
        int userId3 = getUserId(userToken3);

        int gameId = createNewGame(userToken1, false, 12, 1).path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        int[] userIdsSortedByMoveOrder = new int[3];

        int moveOrderOfUser1 = viewGame(userToken1, gameId).path("gameUsers[0].moveOrder");
        int moveOrderOfUser2 = viewGame(userToken1, gameId).path("gameUsers[1].moveOrder");
        int moveOrderOfUser3 = viewGame(userToken1, gameId).path("gameUsers[2].moveOrder");

        userIdsSortedByMoveOrder[moveOrderOfUser1-1] = viewGame(userToken1, gameId).path("gameUsers[0].user.id");
        userIdsSortedByMoveOrder[moveOrderOfUser2-1] = viewGame(userToken1, gameId).path("gameUsers[1].user.id");
        userIdsSortedByMoveOrder[moveOrderOfUser3-1] = viewGame(userToken1, gameId).path("gameUsers[2].user.id");

        String[] userTokensSortedByMoveOrder = new String[3];

        for (int i = 0; i < 3; i++) {
            if (userIdsSortedByMoveOrder[i] == userId1) {
                userTokensSortedByMoveOrder[i] = userToken1;
            } else
                if (userIdsSortedByMoveOrder[i] == userId2) {
                    userTokensSortedByMoveOrder[i] = userToken2;
                } else
                    if (userIdsSortedByMoveOrder[i] == userId3) {
                        userTokensSortedByMoveOrder[i] = userToken3;
                    }
        }

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(1))
                .body("status", equalTo("PLAYING"));

        //Players are ending their turns in preparation stage and first cycle
        endTurn(userTokensSortedByMoveOrder[0], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(2))
                .body("status", equalTo("PLAYING"));

        endTurn(userTokensSortedByMoveOrder[1], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(3))
                .body("status", equalTo("PLAYING"));

        //Last player tries to end his turn in preparation stage and first cycle
        endTurn(userTokensSortedByMoveOrder[2], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(3))
                .body("status", equalTo("PLAYING"));

        //Last player tries to end his turn again in preparation stage while cycle increased by 1
        endTurn(userTokensSortedByMoveOrder[2], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(2))
                .body("status", equalTo("PLAYING"));

        endTurn(userTokensSortedByMoveOrder[1], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(1))
                .body("status", equalTo("PLAYING"));

        //First player tries to end his turn in preparation stage and second cycle. Game stage would change to MAIN
        endTurn(userTokensSortedByMoveOrder[0], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(1))
                .body("status", equalTo("PLAYING"));

        //First player tries to end his turn in main stage
        endTurn(userTokensSortedByMoveOrder[0], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(2))
                .body("status", equalTo("PLAYING"));

        endTurn(userTokensSortedByMoveOrder[1], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(3))
                .body("status", equalTo("PLAYING"));

        //Last player tries to end his turn in main stage
        endTurn(userTokensSortedByMoveOrder[2], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(1))
                .body("status", equalTo("PLAYING"));
    }

    @Test
    public void should_successfully_end_turn_in_different_stages_and_preparationCycles_in_case_of_odd_cycles_amount() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int userId1 = getUserId(userToken1);
        int userId2 = getUserId(userToken2);
        int userId3 = getUserId(userToken3);

        int gameId = createNewGame(userToken1, false, 12, 2).path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        int[] userIdsSortedByMoveOrder = new int[3];

        int moveOrderOfUser1 = viewGame(userToken1, gameId).path("gameUsers[0].moveOrder");
        int moveOrderOfUser2 = viewGame(userToken1, gameId).path("gameUsers[1].moveOrder");
        int moveOrderOfUser3 = viewGame(userToken1, gameId).path("gameUsers[2].moveOrder");

        userIdsSortedByMoveOrder[moveOrderOfUser1-1] = viewGame(userToken1, gameId).path("gameUsers[0].user.id");
        userIdsSortedByMoveOrder[moveOrderOfUser2-1] = viewGame(userToken1, gameId).path("gameUsers[1].user.id");
        userIdsSortedByMoveOrder[moveOrderOfUser3-1] = viewGame(userToken1, gameId).path("gameUsers[2].user.id");

        String[] userTokensSortedByMoveOrder = new String[3];

        for (int i = 0; i < 3; i++) {
            if (userIdsSortedByMoveOrder[i] == userId1) {
                userTokensSortedByMoveOrder[i] = userToken1;
            } else
            if (userIdsSortedByMoveOrder[i] == userId2) {
                userTokensSortedByMoveOrder[i] = userToken2;
            } else
            if (userIdsSortedByMoveOrder[i] == userId3) {
                userTokensSortedByMoveOrder[i] = userToken3;
            }
        }

        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(1))
                .body("status", equalTo("PLAYING"));

        //Players are ending their turns in preparation stage and first cycle
        endTurn(userTokensSortedByMoveOrder[0], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(2))
                .body("status", equalTo("PLAYING"));

        endTurn(userTokensSortedByMoveOrder[1], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(3))
                .body("status", equalTo("PLAYING"));

        //Last player tries to end his turn in preparation stage and first cycle
        endTurn(userTokensSortedByMoveOrder[2], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(3))
                .body("status", equalTo("PLAYING"));

        //Last player tries to end his turn again in preparation stage while cycle increased by 1
        endTurn(userTokensSortedByMoveOrder[2], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(2))
                .body("status", equalTo("PLAYING"));

        endTurn(userTokensSortedByMoveOrder[1], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(1))
                .body("status", equalTo("PLAYING"));

        //First player tries to end his turn in preparation stage and second cycle
        endTurn(userTokensSortedByMoveOrder[0], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(1))
                .body("status", equalTo("PLAYING"));

        //First player tries to end his turn again in preparation stage while cycle increased by 1
        endTurn(userTokensSortedByMoveOrder[0], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(2))
                .body("status", equalTo("PLAYING"));

        endTurn(userTokensSortedByMoveOrder[1], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(3))
                .body("status", equalTo("PLAYING"));

        //Last player tries to end his turn in preparation stage and third cycle. Game stage would change to MAIN
        endTurn(userTokensSortedByMoveOrder[2], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(1))
                .body("status", equalTo("PLAYING"));

        //First player tries to end his turn in main stage
        endTurn(userTokensSortedByMoveOrder[0], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(2))
                .body("status", equalTo("PLAYING"));

        endTurn(userTokensSortedByMoveOrder[1], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(3))
                .body("status", equalTo("PLAYING"));

        //Last player tries to end his turn in main stage
        endTurn(userTokensSortedByMoveOrder[2], gameId);
        viewGame(userToken1, gameId)
                .then()
                .statusCode(200)
                .body("currentMove", is(1))
                .body("status", equalTo("PLAYING"));
    }

    @Test
    public void should_fail_end_turn_if_game_does_not_exist() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int gameId = createNewGame(userToken1, false).path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        endTurn(userToken1, 999999999)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    @Test
    public void should_fail_end_turn_if_game_status_is_not_playing() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        int gameId = createNewGame(userToken1, false).path("gameId");

        endTurn(userToken1, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    @Test
    public void should_fail_end_turn_if_user_is_not_connected_to_game() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);
        String userToken4 = loginUser(USER_NAME_4, USER_PASSWORD_4);

        int gameId = createNewGame(userToken1, false).path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        endTurn(userToken4, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

    @Test
    public void should_fail_end_turn_if_current_move_does_not_belong_to_user() {
        String userToken1 = loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = loginUser(USER_NAME_3, USER_PASSWORD_3);

        int userId1 = getUserId(userToken1);
        int gameId = createNewGame(userToken1, false).path("gameId");

        joinPublicGame(userToken2, gameId);
        joinPublicGame(userToken3, gameId);

        setUserReady(userToken1, gameId);
        setUserReady(userToken2, gameId);
        setUserReady(userToken3, gameId);

        int[] userIdsSortedByMoveOrder = new int[3];

        int moveOrderOfUser1 = viewGame(userToken1, gameId).path("gameUsers[0].moveOrder");
        int moveOrderOfUser2 = viewGame(userToken1, gameId).path("gameUsers[1].moveOrder");
        int moveOrderOfUser3 = viewGame(userToken1, gameId).path("gameUsers[2].moveOrder");

        userIdsSortedByMoveOrder[moveOrderOfUser1-1] = viewGame(userToken1, gameId).path("gameUsers[0].user.id");
        userIdsSortedByMoveOrder[moveOrderOfUser2-1] = viewGame(userToken1, gameId).path("gameUsers[1].user.id");
        userIdsSortedByMoveOrder[moveOrderOfUser3-1] = viewGame(userToken1, gameId).path("gameUsers[2].user.id");

        String userTokenOfNonActivePlayer;
        if (userIdsSortedByMoveOrder[0] != userId1) {
            userTokenOfNonActivePlayer = userToken1;
        } else {
            userTokenOfNonActivePlayer = userToken2;
        }

        endTurn(userTokenOfNonActivePlayer, gameId)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));

    }
}
