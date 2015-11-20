package catan.controllers.testcases.play;

import catan.controllers.ctf.Scenario;
import catan.controllers.ctf.TestApplicationConfig;
import catan.controllers.util.PlayTestUtil;
import catan.services.util.random.RandomUtil;
import catan.services.util.random.RandomUtilMock;
import org.junit.Before;
import org.junit.Ignore;
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
import static org.hamcrest.Matchers.nullValue;

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

    @Ignore
    @Test
    public void should_correctly_give_resources_to_players() {
        Scenario scenario =  playPreparationStageAndBuildCity();

        // TODO: get resources of all players into some variables

        scenario
                .nextRandomDiceValues(asList(2, 6)) // TODO: set the dice number which is near both some settlement and city
                .THROW_DICE(1)

                .getGameDetails(1)
                // TODO: check if player1 resources are correct

                .getGameDetails(2)
                // TODO: check if player2 resources are correct

                .getGameDetails(3);
                // TODO: check if player3 resources are correct
    }

    private Scenario playPreparationStageAndBuildCity() {

        //  TODO: map shouldn't be random

        return playPreparationStage()
                .nextRandomDiceValues(asList(2, 6)) // TODO: set the best dice for player 1
                .THROW_DICE(1)
                .BUILD_ROAD(1).atEdge(0, -1, "right")
                .END_TURN(1)

                .nextRandomDiceValues(asList(2, 6)) // set the best dice for player 1
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(2, 6)) // set the best dice for player 1
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(2, 6)) // set the best dice for player 1
                .THROW_DICE(1)
                .BUILD_SETTLEMENT(1).atNode(0, -1, "topLeft")
                .buildCity(1).atNode(0, -1, "topLeft")
                .END_TURN(1)

                .THROW_DICE(2)
                .END_TURN(2)

                .THROW_DICE(3)
                .END_TURN(3);
    }

    private Scenario playPreparationStage() {
        return scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                //TODO: set all hex types accordingly
                .setHex(BRICK, 11).atCoordinates(-1, -1)
                .setHex(WHEAT, 9).atCoordinates(-1, 1)
                .setHex(WOOD, 5).atCoordinates(-1, 0)
                .setHex(SHEEP, 1).atCoordinates(0, -1)
                .setHex(STONE, 2).atCoordinates(0, 1)
                .setHex(EMPTY, 7).atCoordinates(0, 0)

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

                .BUILD_SETTLEMENT(1).atNode(0, 0, "topLeft")
                .BUILD_ROAD(1).atEdge(0, 0, "topLeft")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(0, 0, "topRight")
                .BUILD_ROAD(2).atEdge(0, 0, "right")
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(0, 0, "bottom")
                .BUILD_ROAD(3).atEdge(0, 0, "bottomLeft")
                .END_TURN(3)

                .BUILD_SETTLEMENT(3).atNode(0, -2, "topLeft")
                .BUILD_ROAD(3).atEdge(0, -2, "topLeft")
                .END_TURN(3)

                .BUILD_SETTLEMENT(2).atNode(0, -2, "topRight")
                .BUILD_ROAD(2).atEdge(0, -2, "right")
                .END_TURN(2)

                .BUILD_SETTLEMENT(1).atNode(0, -2, "bottom")
                .BUILD_ROAD(1).atEdge(0, -2, "bottomLeft")
                .END_TURN(1);
    }

    /*
    *          (X, Y) coordinates of generated map:                          Node position at hex:
    *
    *           *----*----*----*----*----*----*                                      top
    *           | ( 0,-2) | ( 1,-2) | ( 2,-2) |                          topLeft *----*----* topRight
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