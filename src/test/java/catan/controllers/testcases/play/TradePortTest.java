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
public class TradePortTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_TradePortTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_TradePortTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_TradePortTest";
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
    public void should_successfully_trade_single_resource_without_any_port() {
        playPreparationStageAndGiveResources()
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(2)

                .startTrackResourcesQuantity()
                .TRADE_PORT(2).withResources(1, 0, 0, 0, -4).successfully()
                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(1, 0, 0, 0, -4);
    }

    @Test
    public void should_successfully_trade_single_resource_via_ANY_port() {
        playPreparationStageAndGiveResources()
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1)

                .startTrackResourcesQuantity()
                .TRADE_PORT(1).withResources(1, 0, 0, -3, 0).successfully()
                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(1, 0, 0, -3, 0);
    }

    @Test
    public void should_successfully_trade_single_resource_via_specific_resource_port() {
        playPreparationStageAndGiveResources()
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1)

                .startTrackResourcesQuantity()
                .TRADE_PORT(1).withResources(1, 0, 0, 0, -2).successfully()
                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(1, 0, 0, 0, -2);
    }

    @Test
    public void should_successfully_trade_multiple_resources_via_different_ports() {
        playPreparationStageAndGiveResources()
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1)

                .startTrackResourcesQuantity()
                .TRADE_PORT(1).withResources(1, 1, 0, -3, -2).successfully()
                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(1, 1, 0, -3, -2);
    }

    @Test
    public void should_fail_if_player_has_not_thrown_the_dice() {
        playPreparationStage()
                .nextRandomDiceValues(asList(2, 3)) // P1: +1 wheat, P2: +1 stone
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(2, 3)) // P1: +1 wheat, P2: +1 stone
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(2, 3)) // P1: +1 wheat, P2: +1 stone
                .THROW_DICE(3)
                .END_TURN(3)

                .TRADE_PORT(1).withResources(0, 1, 0, -3, 0).failsWithError("ERROR")
                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .TRADE_PORT(1).withResources(0, 1, 0, -3, 0).successfully();
    }

    @Test
    public void should_fail_if_it_is_not_players_turn() {
        playPreparationStageAndGiveResources()
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1)

                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("TRADE_PORT").withParameters("brick=3", "wood=3", "sheep=3", "wheat=3", "stone=2")

                .getGameDetails(2)
                .gameUser(2).doesntHaveAvailableAction("TRADE_PORT")

                .TRADE_PORT(2).withResources(0, 1, 0, -3, 0).failsWithError("ERROR");
    }

    @Test
    public void should_fail_when_resource_quantity_is_incorrect() {
        playPreparationStageAndGiveResources()
                .nextRandomDiceValues(asList(3, 3)) // P1: +1 stone, P2: +1 stone
                .THROW_DICE(1).END_TURN(1)
                .nextRandomDiceValues(asList(3, 3)) // P1: +1 stone, P2: +1 stone
                .THROW_DICE(2).END_TURN(2)
                .nextRandomDiceValues(asList(3, 2)) // P1: +1 wheat, P2: +1 stone
                .THROW_DICE(3).END_TURN(3)
                .nextRandomDiceValues(asList(3, 2)) // P1: +1 wheat, P2: +1 stone
                .THROW_DICE(1).END_TURN(1)
                .nextRandomDiceValues(asList(3, 2)) // P1: +1 wheat, P2: +1 stone
                .THROW_DICE(2).END_TURN(2)
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(3).END_TURN(3)

                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("TRADE_PORT").withParameters("brick=3", "wood=3", "sheep=3", "wheat=3", "stone=2")

                .TRADE_PORT(1).withResources(0, 0, 1, -6, 0).failsWithError("ERROR")        // single source resource correct, single target resource not correct (lower)
                .TRADE_PORT(1).withResources(0, 0, 0, -3, 0).failsWithError("ERROR")        // single source resource correct, single target resource not correct (empty)
                .TRADE_PORT(1).withResources(0, 0, 2, -3, 0).failsWithError("ERROR")        // single source resource correct, single target resource not correct (grater)
                .TRADE_PORT(1).withResources(0, 1, 1, -6, -2).failsWithError("ERROR")       // both source resources correct, first target resource correct, second target resource not correct (lower)
                .TRADE_PORT(1).withResources(0, 0, 1, -3, -2).failsWithError("ERROR")       // both source resources correct, first target resource correct, second target resource not correct (empty)
                .TRADE_PORT(1).withResources(0, 2, 1, -3, -2).failsWithError("ERROR")       // both source resources correct, first target resource correct, second target resource not correct (grater)
                .TRADE_PORT(1).withResources(0, 0, 1, -1, 0).failsWithError("ERROR")        // single source resource not correct (lower), single target resource correct
                .TRADE_PORT(1).withResources(0, 0, 1, 0, 0).failsWithError("ERROR")         // single source resource not correct (empty), single target resource correct
                .TRADE_PORT(1).withResources(0, 0, 1, -4, 0).failsWithError("ERROR")        // single source resource not correct (grater), single target resource correct
                .TRADE_PORT(1).withResources(0, 1, 1, -3, -1).failsWithError("ERROR")       // first source resource not correct (lower), second source resource correct, both target resources correct
                .TRADE_PORT(1).withResources(0, 1, 1, -3, 0).failsWithError("ERROR")        // first source resource not correct (empty), second source resource correct, both target resources correct
                .TRADE_PORT(1).withResources(0, 1, 1, -3, -3).failsWithError("ERROR")       // first source resource not correct (grater), second source resource correct, both target resources correct
                .TRADE_PORT(1).withResources(0, 0, 0, 0, 0).failsWithError("ERROR")         // all resources are zero
        ;
    }

    @Test
    public void should_fail_when_player_has_not_enough_resources() {
        playPreparationStage()
                .startTrackResourcesQuantity()
                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("TRADE_PORT").withParameters("brick=3", "wood=3", "sheep=3", "wheat=3", "stone=2")

                .TRADE_PORT(1).withResources(-3, 1, 0, 0, 0).failsWithError("ERROR")
                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .TRADE_PORT(1).withResources(-6, 2, 0, 0, 0).failsWithError("ERROR")
                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .TRADE_PORT(1).withResources(-3, -3, 1, 1, 0).failsWithError("ERROR")
                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .TRADE_PORT(1).withResources(0, 1, 0, 0, -2).failsWithError("ERROR")
                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    private Scenario playPreparationStageAndGiveResources() {
        return playPreparationStage()
                .nextRandomDiceValues(asList(3, 3)) // P1: +1 stone, P2: +1 stone
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(3, 3)) // P1: +1 stone, P2: +1 stone
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(3, 2)) // P1: +1 wheat, P2: +1 stone
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(3, 2)) // P1: +1 wheat, P2: +1 stone
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(3, 2)) // P1: +1 wheat, P2: +1 stone
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
                .setHex(SHEEP, 3).atCoordinates(1, 0)
                .setHex(BRICK, 3).atCoordinates(2, -1)
                .setHex(WOOD, 4).atCoordinates(1, 1)
                .setHex(STONE, 4).atCoordinates(-1, 0)
                .setHex(WHEAT, 5).atCoordinates(-1, -1)
                .setHex(WHEAT, 8).atCoordinates(0, 2)
                .setHex(STONE, 6).atCoordinates(0, -2)
                .setHex(WOOD, 6).atCoordinates(0, -1)
                .setHex(EMPTY, null).atCoordinates(0, 0)
                .setHex(STONE, 5).atCoordinates(1, -2)
                .setHex(WOOD, 8).atCoordinates(-2, 0)
                .setHex(SHEEP, 9).atCoordinates(-2, 1)
                .setHex(BRICK, 9).atCoordinates(-1, 1)
                .setHex(SHEEP, 10).atCoordinates(1, -1)
                .setHex(WOOD, 10).atCoordinates(-2, 2)
                .setHex(BRICK, 11).atCoordinates(2, 0)
                .setHex(SHEEP, 11).atCoordinates(0, 1)
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

                .BUILD_SETTLEMENT(2).atNode(0, -2, "topRight")
                .BUILD_ROAD(2).atEdge(0, -2, "right")
                .END_TURN(2)

                .BUILD_SETTLEMENT(1).atNode(-1, -1, "topLeft")
                .BUILD_ROAD(1).atEdge(-1, -1, "topLeft")
                .END_TURN(1);
    }

//                     Coordinates of generated map:
//
//                    (ANY)          (SHEEP)                                       [Node position at hex]:
//                    /   \           /  \
//                   (1)xxx*---(2)---*----*----*----*                                      top
//                    |    6    X    5    |    12   |                          topLeft *----*----* topRight
//                    |  STONE  X  STONE  |  WHEAT  | (ANY)                            |         |
//                    | ( 0,-2) X ( 1,-2) | ( 2,-2) |/   |                  bottomLeft *----*----* bottomRight
//              (1)xxx*----*----*----*----*----*----*---(3)                               bottom
//            /  |    5    |    6    |    10   |    3    X
//      (STONE)  |  WHEAT  |   WOOD  |  SHEEP  |  BRICK  X
//            \  | (-1,-1) | ( 0,-1) | ( 1,-1) | ( 2,-1) X                        [Edge position at hex]:
//          *----*----*----*----*----*---(2)---*----*----*----*
//          |    8    |    4    |         X    3    |    11   | \                     topLeft topRight
//          |   WOOD  |  STONE  |  EMPTY  X  SHEEP  |  BRICK  |  (ANY)                  .====.====.
//          | (-2, 0) | (-1, 0) | ( 0, 0) X ( 1, 0) | ( 2, 0) | /                 left ||         || right
//          *----*----*----*----*xxx(3)---*----*----*----*----*                         .====.====.
//             / |    9    |    9    |    11   |    4    |                        bottomLeft bottomRight
//      (WHEAT)  |  SHEEP  |  BRICK  |  SHEEP  |   WOOD  |
//             \ | (-2, 1) | (-1, 1) | ( 0, 1) | ( 1, 1) |
//               *----*----*----*----*----*----*----*----*
//                    |    10   |    2    |    8    |\   |
//                    |   WOOD  |  WHEAT  |  WHEAT  | (BRICK)
//                    | (-2, 2) | (-1, 2) | ( 0, 2) |
//                    *----*----*----*----*----*----*
//                     \  /           \  /
//                    (ANY)          (WOOD)
//
//
}