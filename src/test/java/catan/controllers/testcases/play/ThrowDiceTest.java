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
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)

//@SpringApplicationConfiguration(classes = {TestApplicationConfig.class, RequestResponseLogger.class})  // if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class ThrowDiceTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_ThrowDiceTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_ThrowDiceTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_ThrowDiceTest";
    public static final String USER_PASSWORD_3 = "password3";

    private static boolean initialized = false;

    @Autowired
    private RandomUtil randomUtil;

    private Scenario scenario;

    @Before
    public void setup() {
        if (!initialized) {
            registerUser(USER_NAME_1, USER_PASSWORD_1);
            registerUser(USER_NAME_2, USER_PASSWORD_2);
            registerUser(USER_NAME_3, USER_PASSWORD_3);
            initialized = true;
        }

        scenario = new Scenario((RandomUtilMock) randomUtil);
    }

    @Test
    public void should_user_has_available_action_throw_dice_before_move_and_shouldnt_after_throw_dice() {

        playPreparationStage()
                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("THROW_DICE")
                .THROW_DICE(1).successfully()
                .getGameDetails(1)
                .gameUser(1).doesntHaveAvailableAction("THROW_DICE")
                .END_TURN(1)

                .getGameDetails(2)
                .gameUser(2).hasAvailableAction("THROW_DICE")
                .THROW_DICE(2).successfully()
                .getGameDetails(2)
                .gameUser(2).doesntHaveAvailableAction("THROW_DICE")
                .END_TURN(2);
    }

    @Test
    public void should_fail_when_user_throw_dice_after_he_has_already_thrown() {

        playPreparationStage()
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
                .END_TURN(1)

                .getGameDetails(1).gameUser(1).resourcesChanged(0, 4, 0, 0, 1)
                .getGameDetails(2).gameUser(2).resourcesChanged(0, 0, 0, 0, 1)
                .getGameDetails(3).gameUser(3).resourcesChanged(0, 0, 0, 0, 1)

                .nextRandomDiceValues(asList(2, 8))
                .THROW_DICE(2)
                .END_TURN(2)

                .getGameDetails(1).gameUser(1).resourcesChanged(0, 0, 1, 0, 0)
                .getGameDetails(2).gameUser(2).resourcesChanged(0, 0, 1, 0, 0)
                .getGameDetails(3).gameUser(3).resourcesChanged(0, 0, 0, 0, 0);
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
                .END_TURN(3);
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
                    .gameUser(3).check("user.username", is(USER_NAME_1))
                    .gameUser(2).check("user.username", is(USER_NAME_2))
                    .gameUser(1).check("user.username", is(USER_NAME_3))

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
    *           |    11   |     8   |     5   |                          topLeft *----*----* topRight
    *           |  WOOD   |  BRICK  |   WHEAT |
    *           | ( 1,-2) | ( 1,-2) | ( 2,-2) |
    *      *----*----*----*----*----*----*----*----*                             |         |
    *      | (-1,-1) | ( 0,-1) | ( 1,-1) | ( 2,-1) |                  bottomLeft *----*----* bottomRight
    * *----*----*----*----*----*----*----*----*----*----*                           bottom
    * | (-2, 0) | (-1, 0) | ( 0, 0) | ( 1, 0) | ( 2, 0) |
    * *----*----*----*----*----*----*----*----*----*----*                    Edge position at hex:
    *      | (-2, 1) | (-1, 1) | ( 0, 1) | ( 1, 1) |
    *      *----*----*----*----*----*----*----*----*                           topLeft topRight
    *           | (-2, 2) | (-1, 2) | ( 0, 2) |                                  .====.====.
    *           *----*----*----*----*----*----*                            left ||         || right
    *                                                                            .====.====.
    *                                                                       bottomLeft bottomRight
    */

}