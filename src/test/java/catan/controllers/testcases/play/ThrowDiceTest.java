package catan.controllers.testcases.play;

import catan.controllers.ctf.Scenario;
import catan.controllers.ctf.TestApplicationConfig;
import catan.controllers.util.PlayTestUtil;
import catan.services.util.random.RandomValueGeneratorMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    private RandomValueGeneratorMock rvg;

    private Scenario scenario;

    @Before
    public void setup() {
        if (!initialized) {
            registerUser(USER_NAME_1, USER_PASSWORD_1);
            registerUser(USER_NAME_2, USER_PASSWORD_2);
            registerUser(USER_NAME_3, USER_PASSWORD_3);
            initialized = true;
        }

        scenario = new Scenario(rvg);
    }

    @Test
    public void should_user_has_available_action_throw_dice_before_move_and_shouldnt_after_throw_dice() {

        playPreparationStage()
                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("THROW_DICE")
                .throwDice(1).successfully()
                .getGameDetails(1)
                .gameUser(1).doesntHaveAvailableAction("THROW_DICE")
                .endTurn(1)

                .getGameDetails(2)
                .gameUser(2).hasAvailableAction("THROW_DICE")
                .throwDice(2).successfully()
                .getGameDetails(2)
                .gameUser(2).doesntHaveAvailableAction("THROW_DICE")
                .endTurn(2);
    }

    @Test
    public void should_fail_when_user_throw_dice_after_he_has_already_thrown() {

        playPreparationStage()
                .throwDice(1)
                .throwDice(1).failsWithError("ERROR");
    }

    @Test
    public void should_fail_when_user_throw_dice_in_not_his_move() {

        playPreparationStage()
                .throwDice(2).failsWithError("ERROR");
    }

    private Scenario playPreparationStage() {
        return scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                .createNewPublicGameByUser(USER_NAME_1)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)

                // return random values as 0 each time, when pulling move order from the list to have order: 1, 2, 3
                .nextRandomGeneratedValues(asList(0, 0, 0))

                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3)

                //check that move orders are never changed and users have move orders according to joined order
                .getGameDetails(1)
                .gameUser(1).check("user.username", is(USER_NAME_1))
                .gameUser(2).check("user.username", is(USER_NAME_2))
                .gameUser(3).check("user.username", is(USER_NAME_3))

                .buildSettlement(1).atNode(0, 0, "topLeft")
                .buildRoad(1).atEdge(0, 0, "topLeft")
                .endTurn(1)

                .buildSettlement(2).atNode(0, 0, "topRight")
                .buildRoad(2).atEdge(0, 0, "right")
                .endTurn(2)

                .buildSettlement(3).atNode(0, 0, "bottom")
                .buildRoad(3).atEdge(0, 0, "bottomLeft")
                .endTurn(3)

                .buildSettlement(3).atNode(0, -2, "topLeft")
                .buildRoad(3).atEdge(0, -2, "topLeft")
                .endTurn(3)

                .buildSettlement(2).atNode(0, -2, "topRight")
                .buildRoad(2).atEdge(0, -2, "right")
                .endTurn(2)

                .buildSettlement(1).atNode(0, -2, "bottom")
                .buildRoad(1).atEdge(0, -2, "bottomLeft")
                .endTurn(1);
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