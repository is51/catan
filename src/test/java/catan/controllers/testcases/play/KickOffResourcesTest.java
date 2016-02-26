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
//@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class KickOffResourcesTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_KickOffResourcesTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_KickOffResourcesTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_KickOffResourcesTest";
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
    public void should_successfully_allow_user_to_move_robber_if_no_one_has_more_than_7_resources_after_dice_thrown() {
        playPreparationStage()
                .nextRandomDiceValues(asList(4, 3))
                .THROW_DICE(1)

                .getGameDetails(1)
                    .gameUser(1).hasAvailableAction("MOVE_ROBBER")
                    .gameUser(1).doesntHaveAvailableAction("KICK_OFF_RESOURCES")

                .getGameDetails(2)
                    .gameUser(2).doesntHaveAvailableAction("KICK_OFF_RESOURCES")

                .getGameDetails(3)
                    .gameUser(3).doesntHaveAvailableAction("KICK_OFF_RESOURCES");

    }

    @Test
    public void should_successfully_kick_off_resources_by_users() {
        playPreparationStageAndGiveResources()
                .nextRandomDiceValues(asList(4, 3))
                .THROW_DICE(1)

                .getGameDetails(1)
                    .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                    .gameUser(1).hasAvailableAction("KICK_OFF_RESOURCES")

                .getGameDetails(2)
                    .gameUser(2).hasAvailableAction("KICK_OFF_RESOURCES")

                .getGameDetails(3)
                    .gameUser(3).doesntHaveAvailableAction("KICK_OFF_RESOURCES")

                .startTrackResourcesQuantity()
                .KICK_OFF_RESOURCES(1, 2, 3, 0, 0, 2).successfully()

                .getGameDetails(1)
                    .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                    .gameUser(1).doesntHaveAvailableAction("KICK_OFF_RESOURCES")
                    .gameUser(1).resourcesQuantityChangedBy(-2, -3, 0, 0, -2)

                .getGameDetails(2)
                    .gameUser(2).hasAvailableAction("KICK_OFF_RESOURCES")
                    .gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .getGameDetails(3)
                    .gameUser(3).doesntHaveAvailableAction("KICK_OFF_RESOURCES")
                    .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .KICK_OFF_RESOURCES(2, 5, 0, 0, 0, 0).successfully()

                .getGameDetails(1)
                    .gameUser(1).hasAvailableAction("MOVE_ROBBER")
                    .gameUser(1).doesntHaveAvailableAction("KICK_OFF_RESOURCES")
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .getGameDetails(2)
                    .gameUser(2).doesntHaveAvailableAction("KICK_OFF_RESOURCES")
                    .gameUser(2).resourcesQuantityChangedBy(-5, 0, 0, 0, 0)

                .getGameDetails(3)
                    .gameUser(3).doesntHaveAvailableAction("KICK_OFF_RESOURCES")
                    .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_when_user_kicking_off_resources_which_quantity_differ_from_half_of_total_user_resources() {
        playPreparationStageAndGiveResources()
                .nextRandomDiceValues(asList(4, 3))
                .THROW_DICE(1)

                .getGameDetails(1)
                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).hasAvailableAction("KICK_OFF_RESOURCES")

                .getGameDetails(2)
                .gameUser(2).hasAvailableAction("KICK_OFF_RESOURCES")

                .getGameDetails(3)
                .gameUser(3).doesntHaveAvailableAction("KICK_OFF_RESOURCES")

                .startTrackResourcesQuantity()
                .KICK_OFF_RESOURCES(1, 2, 10, 0, 0, 1).failsWithError("ERROR")

                .getGameDetails(1)
                    .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                    .gameUser(1).hasAvailableAction("KICK_OFF_RESOURCES")
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .getGameDetails(2)
                    .gameUser(2).hasAvailableAction("KICK_OFF_RESOURCES")
                    .gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .getGameDetails(3)
                    .gameUser(3).doesntHaveAvailableAction("KICK_OFF_RESOURCES")
                    .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_when_user_kicking_off_resources_but_quantity_of_one_of_the_resources_is_below_zero() {
        playPreparationStageAndGiveResources()
                .nextRandomDiceValues(asList(4, 3))
                .THROW_DICE(1)

                .getGameDetails(1)
                    .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                    .gameUser(1).hasAvailableAction("KICK_OFF_RESOURCES")

                .getGameDetails(2)
                    .gameUser(2).hasAvailableAction("KICK_OFF_RESOURCES")

                .getGameDetails(3)
                    .gameUser(3).doesntHaveAvailableAction("KICK_OFF_RESOURCES")

                .startTrackResourcesQuantity()
                .KICK_OFF_RESOURCES(1, 2, 4, -1, 0, 1).failsWithError("ERROR")

                .getGameDetails(1)
                    .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                    .gameUser(1).hasAvailableAction("KICK_OFF_RESOURCES")
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .getGameDetails(2)
                    .gameUser(2).hasAvailableAction("KICK_OFF_RESOURCES")
                    .gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .getGameDetails(3)
                    .gameUser(3).doesntHaveAvailableAction("KICK_OFF_RESOURCES")
                    .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_when_user_kicking_off_resources_but_quantity_of_one_of_the_resources_is_higher_then_available() {
        playPreparationStageAndGiveResources()
                .nextRandomDiceValues(asList(4, 3))
                .THROW_DICE(1)

                .getGameDetails(1)
                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).hasAvailableAction("KICK_OFF_RESOURCES")

                .getGameDetails(2)
                .gameUser(2).hasAvailableAction("KICK_OFF_RESOURCES")

                .getGameDetails(3)
                .gameUser(3).doesntHaveAvailableAction("KICK_OFF_RESOURCES")

                .startTrackResourcesQuantity()
                .KICK_OFF_RESOURCES(1, 6, 0, 0, 0, 0).failsWithError("ERROR")

                .getGameDetails(1)
                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).hasAvailableAction("KICK_OFF_RESOURCES")
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .getGameDetails(2)
                .gameUser(2).hasAvailableAction("KICK_OFF_RESOURCES")
                .gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .getGameDetails(3)
                .gameUser(3).doesntHaveAvailableAction("KICK_OFF_RESOURCES")
                .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    private Scenario playPreparationStageAndGiveResources() {

        return playPreparationStage()
                .nextRandomDiceValues(asList(4, 2)) // P1: +2woods +1stone
                .THROW_DICE(1)                      // P2: +1stone
                .END_TURN(1)                        // P3: +1stone

                .nextRandomDiceValues(asList(2, 4)) // P1: +2woods +1stone
                .THROW_DICE(2)                      // P2: +1stone
                .END_TURN(2)                        // P3: +1stone

                .nextRandomDiceValues(asList(2, 4)) // P1: +2woods +1stone
                .THROW_DICE(3)                      // P2: +1stone
                .END_TURN(3)                        // P3: +1stone

                .nextRandomDiceValues(asList(4, 4)) // P1: +1brick
                .THROW_DICE(1)                      // P2: +2bricks
                .END_TURN(1)                        // P3: --

                .nextRandomDiceValues(asList(4, 4)) // P1: +1brick
                .THROW_DICE(2)                      // P2: +2bricks
                .END_TURN(2)                        // P3: --

                .nextRandomDiceValues(asList(4, 4)) // P1: +1brick
                .THROW_DICE(3)                      // P2: +2bricks
                .END_TURN(3);                        // P3: --
    }

    private Scenario playPreparationStage() {
        return scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                /*
                possible dice values

                2,
                3, 3,
                4, 4,
                5, 5,
                6, 6,
                7
                8, 8,
                9, 9,
                10, 10,
                11, 11,
                12

                possible hex type values:
                WOOD, WOOD, WOOD, WOOD,
                SHEEP, SHEEP, SHEEP, SHEEP,
                WHEAT, WHEAT, WHEAT, WHEAT,
                BRICK, BRICK, BRICK,
                STONE, STONE, STONE,
                EMPTY

                */
                .setHex(STONE, 6).atCoordinates(0, -2)
                .setHex(BRICK, 8).atCoordinates(1, -2)
                .setHex(WHEAT, 12).atCoordinates(2, -2)

                .setHex(WHEAT, 5).atCoordinates(-1, -1)
                .setHex(WOOD, 6).atCoordinates(0, -1)
                .setHex(SHEEP, 10).atCoordinates(1, -1)
                .setHex(BRICK, 3).atCoordinates(2, -1)

                .setHex(WOOD, 8).atCoordinates(-2, 0)
                .setHex(STONE, 4).atCoordinates(-1, 0)
                .setHex(EMPTY, null).atCoordinates(0, 0)
                .setHex(SHEEP, 3).atCoordinates(1, 0)
                .setHex(STONE, 11).atCoordinates(2, 0)

                .setHex(SHEEP, 9).atCoordinates(-2, 1)
                .setHex(BRICK, 9).atCoordinates(-1, 1)
                .setHex(SHEEP, 11).atCoordinates(0, 1)
                .setHex(WOOD, 4).atCoordinates(1, 1)

                .setHex(WOOD, 10).atCoordinates(-2, 2)
                .setHex(WHEAT, 2).atCoordinates(-1, 2)
                .setHex(EMPTY, null).atCoordinates(0, 2)

                .createNewPublicGameByUser(USER_NAME_1)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)

                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3)

                .BUILD_SETTLEMENT(1).atNode(0, -1, "topRight")
                .BUILD_ROAD(1).atEdge(0, -1, "right")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(1, -2, "topRight")
                .BUILD_ROAD(2).atEdge(1, -2, "right")
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(0, 0, "bottom")
                .BUILD_ROAD(3).atEdge(0, 0, "bottomLeft")
                .END_TURN(3)

                .BUILD_SETTLEMENT(3).atNode(0, -2, "topLeft") // P3: +1brick (after US-69)
                .BUILD_ROAD(3).atEdge(0, -2, "topLeft")
                .END_TURN(3)

                .BUILD_SETTLEMENT(2).atNode(0, -2, "topRight") // P2: +1brick +1stone (after US-69)
                .BUILD_ROAD(2).atEdge(0, -2, "right")
                .END_TURN(2)

                .BUILD_SETTLEMENT(1).atNode(0, -2, "bottom") // P1: +1stone +1wood +1wheat (after US-69)
                .BUILD_ROAD(1).atEdge(0, -2, "bottomLeft")
                .END_TURN(1);
    }

    /*
    *          (X, Y) coordinates of generated map:                          Node position at hex:
    *
    *          (3)xxx*---(2)---*---(2)---*----*                                      top
    *           |    6    X    8    X    12   |                          topLeft *----*----* topRight
    *           |  STONE  X  BRICK  X  WHEAT  |                                  |         |
    *           | ( 0,-2) X ( 1,-2) X ( 2,-2) |                       bottomLeft *----*----* bottomRight
    *      *----*xxx(1)---*---(1)---*----*----*----*                                bottom
    *      |    5    |    6    X    10   |    3    |
    *      |  WHEAT  |   WOOD  X  SHEEP  |  BRICK  |
    *      | (-1,-1) | ( 0,-1) X ( 1,-1) | ( 2,-1) |                        Edge position at hex:
    * *----*----*----*----*----*----*----*----*----*----*
    * |    8    |    4    |         |    3    |    11   |                      topLeft topRight
    * |   WOOD  |  STONE  |  EMPTY  |  SHEEP  |  STONE  |                        .====.====.
    * | (-2, 0) | (-1, 0) | ( 0, 0) | ( 1, 0) | ( 2, 0) |                  left ||         || right
    * *----*----*----*----*xxx(3)---*----*----*----*----*                        .====.====.
    *      |    9    |    9    |    11   |    4    |                        bottomLeft bottomRight
    *      |  SHEEP  |  BRICK  |  SHEEP  |   WOOD  |
    *      | (-2, 1) | (-1, 1) | ( 0, 1) | ( 1, 1) |
    *      *----*----*----*----*----*----*----*----*
    *           |    10   |    2    |    5    |
    *           |   WOOD  |  WHEAT  |  WHEAT  |
    *           | (-2, 2) | (-1, 2) | ( 0, 2) |
    *           *----*----*----*----*----*----*
    *
    *
    */

}