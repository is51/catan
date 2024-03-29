package catan.controllers.testcases.play;

import catan.config.ApplicationConfig;
import catan.controllers.ctf.Scenario;
import catan.controllers.util.FunctionalTestUtil;
import catan.controllers.util.PlayTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)

//@SpringApplicationConfiguration(classes = {ApplicationConfig.class, RequestResponseLogger.class})  // if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class ThrowDiceTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_ThrowDiceTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_ThrowDiceTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_ThrowDiceTest";
    public static final String USER_PASSWORD_3 = "password3";

    private static boolean initialized = false;

    private Scenario scenario;
    public static final String NOTIFY_MESSAGE_THROW_DICE = "Your turn!";

    @Before
    public void setup() {
        scenario = new Scenario();

        if (!initialized) {
            scenario
                    .registerUser(USER_NAME_1, USER_PASSWORD_1)
                    .registerUser(USER_NAME_2, USER_PASSWORD_2)
                    .registerUser(USER_NAME_3, USER_PASSWORD_3);
            initialized = true;
        }
    }

    @Test
    public void should_user_has_available_action_throw_dice_before_move_and_shouldnt_after_throw_dice() {

        playPreparationStage()
                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("THROW_DICE").withNotification(NOTIFY_MESSAGE_THROW_DICE)
                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1).successfully()
                .getGameDetails(1)
                .gameUser(1).doesntHaveAvailableAction("THROW_DICE")
                .END_TURN(1)

                .getGameDetails(2)
                .gameUser(2).hasAvailableAction("THROW_DICE").withNotification(NOTIFY_MESSAGE_THROW_DICE)
                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(2).successfully()
                .getGameDetails(2)
                .gameUser(2).doesntHaveAvailableAction("THROW_DICE")
                .END_TURN(2);
    }

    @Test
    public void should_fail_when_user_throw_dice_after_he_has_already_thrown() {

        playPreparationStage()
                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1).successfully()
                .THROW_DICE(1).failsWithError("ERROR");
    }

    @Test
    public void should_fail_when_user_throw_dice_in_not_his_move() {

        playPreparationStage()
                .THROW_DICE(2).failsWithError("ERROR");
    }

    @Test
    public void should_game_details_have_dice_information_after_throwing() {
        playPreparationStage()

                .getGameDetails(1)
                    .dice().isNotThrown()
                    .dice().hasNoValues()

                .getGameDetails(2)
                    .dice().isNotThrown()
                    .dice().hasNoValues()

                .nextRandomDiceValues(asList(2, 6))
                .THROW_DICE(1)

                .getGameDetails(1)
                    .dice().isThrown()
                    .dice().hasValues(2, 6)

                .getGameDetails(2)
                    .dice().isThrown()
                    .dice().hasValues(2, 6)

                .END_TURN(1)

                .getGameDetails(1)
                    .dice().isNotThrown()
                    .dice().hasNoValues()

                .getGameDetails(2)
                    .dice().isNotThrown()
                    .dice().hasNoValues();
    }

    @Test
    public void should_correctly_give_resources_to_players() {
        playPreparationStageAndBuildCity()
                .nextRandomDiceValues(asList(2, 4))
                .THROW_DICE(1)
                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 4, 0, 0, 1)
                .gameUser(1).hasLogWithCode("THROW_DICE").hasMessage("You threw 6 and got 4 cables, 1 consultant").isDisplayedOnTop()
                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 1)
                .gameUser(2).hasLogWithCode("THROW_DICE").hasMessage(scenario.getUsername(1) + " threw 6. You got 1 consultant").isDisplayedOnTop()
                .getGameDetails(3)
                .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 1)
                .gameUser(3).hasLogWithCode("THROW_DICE").hasMessage(scenario.getUsername(1) + " threw 6. You got 1 consultant").isDisplayedOnTop()

                .END_TURN(1)
                .getGameDetails(1)
                .gameUser(1).hasLogWithCode("END_TURN").hasMessage("You ended turn").isHidden()
                .getGameDetails(2)
                .gameUser(2).hasLogWithCode("END_TURN").hasMessage(scenario.getUsername(1) + " ended turn").isHidden()
                .getGameDetails(3)
                .gameUser(3).hasLogWithCode("END_TURN").hasMessage(scenario.getUsername(1) + " ended turn").isHidden()

                .nextRandomDiceValues(asList(2, 2))
                .THROW_DICE(2)
                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 2)
                .gameUser(1).hasLogWithCode("THROW_DICE").hasMessage(scenario.getUsername(2) + " threw 4. You got 2 consultants").isDisplayedOnTop()
                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .gameUser(2).hasLogWithCode("THROW_DICE").hasMessage("You threw 4").isDisplayedOnTop()
                .getGameDetails(3)
                .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .gameUser(3).hasLogWithCode("THROW_DICE").hasMessage(scenario.getUsername(2) + " threw 4").isDisplayedOnTop()

                .END_TURN(2)
                .getGameDetails(1)
                .gameUser(1).hasLogWithCode("END_TURN").hasMessage(scenario.getUsername(2) + " ended turn").isHidden()
                .getGameDetails(2)
                .gameUser(2).hasLogWithCode("END_TURN").hasMessage("You ended turn").isHidden()
                .getGameDetails(3)
                .gameUser(3).hasLogWithCode("END_TURN").hasMessage(scenario.getUsername(2) + " ended turn").isHidden()

                .nextRandomDiceValues(asList(2, 5))
                .THROW_DICE(3)
                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .gameUser(1).hasLogWithCode("THROW_DICE").hasMessage(scenario.getUsername(3) + " threw 7 and activate hacker").isDisplayedOnTop()
                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .gameUser(2).hasLogWithCode("THROW_DICE").hasMessage(scenario.getUsername(3) + " threw 7 and activate hacker").isDisplayedOnTop()
                .getGameDetails(3)
                .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .gameUser(3).hasLogWithCode("THROW_DICE").hasMessage("You threw 7 and activate hacker").isDisplayedOnTop();
    }


    private Scenario playPreparationStageAndBuildCity() {

        return playPreparationStage()
                .nextRandomDiceValues(asList(2, 4)) // P1: +1stone +2wood
                .THROW_DICE(1)                      // P2: +1stone
                .END_TURN(1)                        // P3: +1stone

                .nextRandomDiceValues(asList(2, 4)) // P1: +1stone +2wood
                .THROW_DICE(2)                      // P2: +1stone
                .END_TURN(2)                        // P3: +1stone

                .nextRandomDiceValues(asList(2, 4)) // P1: +1stone +2wood
                .THROW_DICE(3)                      // P2: +1stone
                .END_TURN(3)                        // P3: +1stone

                .nextRandomDiceValues(asList(2, 6)) // P1: +1brick
                .THROW_DICE(1)                      // P2: +1brick
                .END_TURN(1)                        // P3: --

                .nextRandomDiceValues(asList(2, 6)) // P1: +1brick
                .THROW_DICE(2)                      // P2: +1brick
                .END_TURN(2)                        // P3: --

                .nextRandomDiceValues(asList(2, 3)) // P1: +1wheat
                .THROW_DICE(3)                      // P2: --
                .END_TURN(3)                        // P3: --

                .nextRandomDiceValues(asList(2, 3)) // P1: +1wheat
                .THROW_DICE(1)                      // P2: --
                .END_TURN(1)                        // P3: --

                .nextRandomDiceValues(asList(2, 3)) // P1: +1wheat
                .THROW_DICE(2)                      // P2: --
                .END_TURN(2)                        // P3: --

                .nextRandomDiceValues(asList(2, 8)) // P1: +1sheep
                .THROW_DICE(3)                      // P2: +1sheep
                .END_TURN(3)                        // P3: --

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1)
                .BUILD_ROAD(1).atEdge(0, 0, "topLeft")          // P1: -1brick -1wood
                .BUILD_SETTLEMENT(1).atNode(0, 0, "topLeft")    // P1: -1brick -1wood -1wheat -1sheep
                .BUILD_CITY(1).atNode(0, 0, "topLeft")           // P1: -2wheat -3stone
                .END_TURN(1)

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(3)
                .END_TURN(3)
                .startTrackResourcesQuantity();
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
                .setHex(WHEAT, 5).atCoordinates(0, 2)

                .createNewPublicGameByUser(USER_NAME_1)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)

                // take last player from the list each time, when pulling move order from the list to have order: 3, 2, 1
                .nextRandomMoveOrderValues(asList(3, 2, 1))

                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3)

                //check that move orders are never changed and users have move orders according to joined order
                .getGameDetails(1)
                    .gameUser(3).check("user.username", is(USER_NAME_1 + FunctionalTestUtil.GLOBAL_UNIQUE_USERNAME_SUFFIX))
                    .gameUser(2).check("user.username", is(USER_NAME_2 + FunctionalTestUtil.GLOBAL_UNIQUE_USERNAME_SUFFIX))
                    .gameUser(1).check("user.username", is(USER_NAME_3 + FunctionalTestUtil.GLOBAL_UNIQUE_USERNAME_SUFFIX))

                .BUILD_SETTLEMENT(1).atNode(0, -1, "topRight")
                .BUILD_ROAD(1).atEdge(0, -1, "right")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(0, 0, "topRight")
                .BUILD_ROAD(2).atEdge(0, 0, "right")
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
    *           *----*----*----*----*----*----*                                      top
    *           |    6    |    8    |    12   |                          topLeft *----*----* topRight
    *           |  STONE  |  BRICK  |  WHEAT  |                                  |         |
    *           | ( 0,-2) | ( 1,-2) | ( 2,-2) |                       bottomLeft *----*----* bottomRight
    *      *----*----*----*----*----*----*----*----*                                bottom
    *      |    5    |    6    |    10   |    3    |
    *      |  WHEAT  |   WOOD  |  SHEEP  |  BRICK  |
    *      | (-1,-1) | ( 0,-1) | ( 1,-1) | ( 2,-1) |                        Edge position at hex:
    * *----*----*----*----*----*----*----*----*----*----*
    * |    8    |    4    |         |    3    |    11   |                      topLeft topRight
    * |   WOOD  |  STONE  |  EMPTY  |  SHEEP  |  STONE  |                        .====.====.
    * | (-2, 0) | (-1, 0) | ( 0, 0) | ( 1, 0) | ( 2, 0) |                  left ||         || right
    * *----*----*----*----*----*----*----*----*----*----*                        .====.====.
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