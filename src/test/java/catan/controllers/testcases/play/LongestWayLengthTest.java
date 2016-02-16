package catan.controllers.testcases.play;

import catan.controllers.ctf.Scenario;
import catan.controllers.ctf.TestApplicationConfig;
import catan.controllers.util.PlayTestUtil;
import catan.services.util.random.RandomUtil;
import catan.services.util.random.RandomUtilMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static catan.domain.model.dashboard.types.HexType.BRICK;
import static catan.domain.model.dashboard.types.HexType.EMPTY;
import static catan.domain.model.dashboard.types.HexType.SHEEP;
import static catan.domain.model.dashboard.types.HexType.STONE;
import static catan.domain.model.dashboard.types.HexType.WHEAT;
import static catan.domain.model.dashboard.types.HexType.WOOD;
import static java.util.Arrays.asList;

@RunWith(SpringJUnit4ClassRunner.class)

//@SpringApplicationConfiguration(classes = {TestApplicationConfig.class, RequestResponseLogger.class})  // if needed initial request and JSON response logging:
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class LongestWayLengthTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_LongestWayLengthTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_LongestWayLengthTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_LongestWayLengthTest";
    public static final String USER_PASSWORD_3 = "password3";

    private static boolean initialized = false;

    @Autowired
    private RandomUtil randomUtil;

    private Scenario scenario;

    @Before
    public void setup() {
        scenario = new Scenario((RandomUtilMock) randomUtil);

        if (!initialized) {
            scenario
                    .registerUser(USER_NAME_1, USER_PASSWORD_1)
                    .registerUser(USER_NAME_2, USER_PASSWORD_2)
                    .registerUser(USER_NAME_3, USER_PASSWORD_3);
            initialized = true;
        }
    }

    @Test
    public void should_successfully_calculate_longest_way() {

    }

    @Test
    public void should_successfully_ASSIGN_longest_way_owner_and_add_2_victory_points_for_that() {

    }

    @Test
    public void should_successfully_REASSIGN_longest_way_owner_if_another_player_builds_more_longer_way() {

    }

    @Test
    public void should_successfully_LEAVE_longest_way_owner_if_it_interrupted_but_player_still_has_longest_way() {

    }

    @Test
    public void should_successfully_DISCARD_longest_way_owner_if_it_interrupted_but_player_still_has_longest_way_less_than_5() {

    }

    @Test
    public void should_successfully_DISCARD_longest_way_owner_if_it_interrupted_and_another_player_has_more_longer_road_less_than_5() {

    }

    @Test
    public void should_successfully_DISCARD_longest_way_owner_if_it_interrupted_and_two_players_now_has_SAME_more_longer_roads_more_than_5() {

    }

    @Test
    public void should_successfully_REASSIGN_longest_way_owner_if_it_interrupted_and_another_player_has_more_longer_road_more_than_5() {

    }

    @Test
    public void should_not_assign_longest_way_owner_if_player_has_5_roads_but_not_connected() {

    }

    @Test
    public void should_not_assign_longest_way_owner_if_player_has_5_connected_roads_but_not_continuous() {

    }

    @Test
    public void should_not_assign_longest_way_owner_if_player_has_4_roads_but_cyclically_enclosed() {

    }



    private Scenario playPreparationStageAndGiveResources() {
        return playPreparationStage()
                .nextRandomDiceValues(asList(3, 3)) // P1: +1 stone, P2: +1 wood
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(3, 3)) // P1: +1 stone, P2: +1 wood
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(3, 2)) // P1: +1 wheat, P2: +1 sheep
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(3, 2)) // P1: +1 wheat, P2: +1 sheep
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(3, 2)) // P1: +1 wheat, P2: +1 sheep
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(3)
                .END_TURN(3);
    }

    private Scenario playPreparationStage() {
        return scenario
                //possible dice values: 2, 3, 3, 4, 4, 5, 5, 6, 6, 7 8, 8, 9, 9, 10, 10, 11, 11, 12
                //possible hex type values: WOOD, WOOD, WOOD, WOOD, SHEEP, SHEEP, SHEEP, SHEEP,
                //    WHEAT, WHEAT, WHEAT, WHEAT, BRICK, BRICK, BRICK, STONE, STONE, STONE, EMPTY
                .setHex(WHEAT, 2).atCoordinates(-1, 2)
                .setHex(BRICK, 3).atCoordinates(1, 0)
                .setHex(BRICK, 3).atCoordinates(2, -1)
                .setHex(WOOD, 4).atCoordinates(1, 1)
                .setHex(STONE, 4).atCoordinates(-1, 0)
                .setHex(WHEAT, 5).atCoordinates(-1, -1)
                .setHex(SHEEP, 5).atCoordinates(1, -2)
                .setHex(WOOD, 6).atCoordinates(0, -1)
                .setHex(WOOD, 6).atCoordinates(0, 1)
                .setHex(EMPTY, null).atCoordinates(0, 0)
                .setHex(WHEAT, 8).atCoordinates(0, 2)
                .setHex(WOOD, 8).atCoordinates(-2, 0)
                .setHex(SHEEP, 9).atCoordinates(-2, 1)
                .setHex(BRICK, 9).atCoordinates(-1, 1)
                .setHex(STONE, 10).atCoordinates(1, -1)
                .setHex(SHEEP, 10).atCoordinates(-2, 2)
                .setHex(SHEEP, 11).atCoordinates(2, 0)
                .setHex(STONE, 11).atCoordinates(0, -2)
                .setHex(WHEAT, 12).atCoordinates(2, -2).
                        loginUser(USER_NAME_1, USER_PASSWORD_1).
                        loginUser(USER_NAME_2, USER_PASSWORD_2).
                        loginUser(USER_NAME_3, USER_PASSWORD_3).
                        createNewPublicGameByUser(USER_NAME_1).
                        joinPublicGame(USER_NAME_2).
                        joinPublicGame(USER_NAME_3).
                        setUserReady(USER_NAME_1).
                        setUserReady(USER_NAME_2).
                        setUserReady(USER_NAME_3)

                .BUILD_SETTLEMENT(1).atNode(0, -2, "topLeft")
                .BUILD_ROAD(1).atEdge(0, -2, "topLeft")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(0, 0, "topRight")
                .BUILD_ROAD(2).atEdge(0, 0, "right")
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(0, 0, "bottom")
                .BUILD_ROAD(3).atEdge(0, 0, "bottomLeft")
                .END_TURN(3)

                .BUILD_SETTLEMENT(3).atNode(2, -1, "topRight")
                .BUILD_ROAD(3).atEdge(2, -1, "right")
                .END_TURN(3)

                .BUILD_SETTLEMENT(2).atNode(0, -1, "topRight")
                .BUILD_ROAD(2).atEdge(0, -1, "right")
                .END_TURN(2)

                .BUILD_SETTLEMENT(1).atNode(-1, -1, "topLeft")
                .BUILD_ROAD(1).atEdge(-1, -1, "topLeft")
                .END_TURN(1);
    }

//                     Coordinates of generated map:
//
//                    (ANY)          (SHEEP)                                       [Node position at hex]:
//                    /   \           /  \
//                   (1)xxx*----*----*----*----*----*                                      top
//                    |   11    X    5    |    12   |                          topLeft *----*----* topRight
//                    |  STONE  X  SHEEP  |  WHEAT  | (ANY)                            |         |
//                    | ( 0,-2) X ( 1,-2) | ( 2,-2) |/   |                  bottomLeft *----*----* bottomRight
//              (1)xxx*----*----*---(2)---*----*----*---(3)                               bottom
//            /  |    5    |    6    X    10   |    3    X
//      (STONE)  |  WHEAT  |   WOOD  X  STONE  |  BRICK  X
//            \  | (-1,-1) | ( 0,-1) X ( 1,-1) | ( 2,-1) X                        [Edge position at hex]:
//          *----*----*----*----*----*---(2)---*----*----*----*
//          |    8    |    4    |         X    3    |    11   | \                     topLeft topRight
//          |   WOOD  |  STONE  |  EMPTY  X  BRICK  |  SHEEP  |  (ANY)                  .====.====.
//          | (-2, 0) | (-1, 0) | ( 0, 0) X ( 1, 0) | ( 2, 0) | /                 left ||         || right
//          *----*----*----*----*xxx(3)---*----*----*----*----*                         .====.====.
//             / |    9    |    9    |    6    |    4    |                        bottomLeft bottomRight
//      (WHEAT)  |  SHEEP  |  BRICK  |   WOOD  |   WOOD  |
//             \ | (-2, 1) | (-1, 1) | ( 0, 1) | ( 1, 1) |
//               *----*----*----*----*----*----*----*----*
//                    |    10   |    2    |    8    |\   |
//                    |  SHEEP  |  WHEAT  |  WHEAT  | (BRICK)
//                    | (-2, 2) | (-1, 2) | ( 0, 2) |
//                    *----*----*----*----*----*----*
//                     \  /           \  /
//                    (ANY)          (WOOD)
//
//
}