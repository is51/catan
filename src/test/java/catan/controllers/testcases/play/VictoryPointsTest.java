package catan.controllers.testcases.play;

import catan.config.ApplicationConfig;
import catan.config.RequestResponseLogger;
import catan.controllers.ctf.Scenario;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
//Add it if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = {ApplicationConfig.class, RequestResponseLogger.class})
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class VictoryPointsTest {
    public static final String USER_NAME_1 = "user1_VictoryPointsTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_VictoryPointsTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_VictoryPointsTest";
    public static final String USER_PASSWORD_3 = "password3";

    private static boolean initialized = false;

    private Scenario scenario;

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

    private Scenario startNewGame(int targetVictoryPoints) {
        return scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                .createNewPublicGameByUser(USER_NAME_1, targetVictoryPoints)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)

                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3);
    }

    private Scenario playPreparationStage() {
        /*
            Each user has the following buildings on according hexes:

            .====0----.      ----  empty edge
           ||         |      ====  build road
            .----.----0         .  empty node
                                0  build settlement

         */
        return scenario
                .buildSettlement(1).atNode(0, -2, "top")
                .buildRoad(1).atEdge(0, -2, "topLeft")
                .endTurn(1)
                .buildSettlement(2).atNode(0, 0, "top")
                .buildRoad(2).atEdge(0, 0, "topLeft")
                .endTurn(2)
                .buildSettlement(3).atNode(2, -2, "top")
                .buildRoad(3).atEdge(2, -2, "topLeft")
                .endTurn(3)
                .buildSettlement(3).atNode(2, -2, "bottomRight")
                .buildRoad(3).atEdge(2, -2, "left")
                .endTurn(3)
                .buildSettlement(2).atNode(0, 0, "bottomRight")
                .buildRoad(2).atEdge(0, 0, "left")
                .endTurn(2)
                .buildSettlement(1).atNode(0, -2, "bottomRight")
                .buildRoad(1).atEdge(0, -2, "left")
                .endTurn(1);
    }

    /*
    *          (X, Y) coordinates of generated map:                          NODE position at hex:
    *
    *           *----*----*----*----*----*----*                                      top
    *           | ( 0,-2) | ( 1,-2) | ( 2,-2) |                          topLeft *----*----* topRight
    *      *----*----*----*----*----*----*----*----*                             |         |
    *      | (-1,-1) | ( 0,-1) | ( 1,-1) | ( 2,-1) |                  bottomLeft *----*----* bottomRight
    * *----*----*----*----*----*----*----*----*----*----*                           bottom
    * | (-2, 0) | (-1, 0) | ( 0, 0) | ( 1, 0) | ( 2, 0) |
    * *----*----*----*----*----*----*----*----*----*----*                    EDGE position at hex:
    *      | (-2, 1) | (-1, 1) | ( 0, 1) | ( 1, 1) |
    *      *----*----*----*----*----*----*----*----*                           topLeft topRight
    *           | (-2, 2) | (-1, 2) | ( 0, 2) |                                  .====.====.
    *           *----*----*----*----*----*----*                            left ||         || right
    *                                                                            .====.====.
    *                                                                       bottomLeft bottomRight
    */

    @Test
    public void should_show_real_victory_points_only_for_current_user_when_game_is_playing() {
        startNewGame(3);
        playPreparationStage()
                .getGameDetails(1) //get game details for first user
                .statusIsPlaying()

                //Check details of current user
                .gameUser(1).check("achievements.realVictoryPoints", notNullValue())
                .gameUser(1).check("achievements.displayVictoryPoints", notNullValue())

                //Check details of other user
                .gameUser(2).check("achievements.realVictoryPoints", nullValue())
                .gameUser(2).check("achievements.displayVictoryPoints", notNullValue())

                .getGameDetails(2) //get game details for second user

                //Check details of current user
                .gameUser(2).check("achievements.realVictoryPoints", notNullValue())
                .gameUser(2).check("achievements.displayVictoryPoints", notNullValue())

                //Check details of other user
                .gameUser(1).check("achievements.realVictoryPoints", nullValue())
                .gameUser(1).check("achievements.displayVictoryPoints", notNullValue());
    }

    @Test
    public void should_show_real_victory_points_for_all_users_when_game_is_finished() {
        //Given
        startNewGame(3);
        playPreparationStage()
                .buildSettlement(1).atNode(0, -2, "bottomLeft")
                .getGameDetails(1) //get game details for first user
                .statusIsFinished()

                        //Check details of current user
                .gameUser(1).check("achievements.realVictoryPoints", notNullValue())
                .gameUser(1).check("achievements.displayVictoryPoints", notNullValue())

                //Check details of other user
                .gameUser(2).check("achievements.realVictoryPoints", notNullValue())
                .gameUser(2).check("achievements.displayVictoryPoints", notNullValue())

                .getGameDetails(2) //get game details for second user

                        //Check details of current user
                .gameUser(2).check("achievements.realVictoryPoints", notNullValue())
                .gameUser(2).check("achievements.displayVictoryPoints", notNullValue())

                //Check details of other user
                .gameUser(1).check("achievements.realVictoryPoints", notNullValue())
                .gameUser(1).check("achievements.displayVictoryPoints", notNullValue());
    }
}
