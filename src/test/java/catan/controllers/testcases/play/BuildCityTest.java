package catan.controllers.testcases.play;

import catan.controllers.ctf.Scenario;
import catan.controllers.ctf.TestApplicationConfig;
import catan.controllers.util.PlayTestUtil;
import catan.domain.model.dashboard.types.HexType;
import catan.services.util.random.RandomUtil;
import catan.services.util.random.RandomUtilMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;


@RunWith(SpringJUnit4ClassRunner.class)

//@SpringApplicationConfiguration(classes = {TestApplicationConfig.class, RequestResponseLogger.class})  // if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class BuildCityTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_BuildCityTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_BuildCityTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_BuildCityTest";
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
    public void should_successfully_build_city_even_if_user_does_not_have_resources_in_preparation_stage() {
        startNewGame(12, 2)
                .BUILD_SETTLEMENT(1).atNode(2, -2, "topLeft")
                .BUILD_ROAD(1).atEdge(2, -2, "topLeft")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(2, -1, "bottomRight")
                .BUILD_ROAD(2).atEdge(2, -1, "bottomRight")
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(0, 2, "topRight")
                .BUILD_ROAD(3).atEdge(0, 2, "topRight")
                .END_TURN(3)

                .getGameDetails(3).gameUser(3).check("resources.wheat", is(0))
                .getGameDetails(3).gameUser(3).check("resources.stone", is(0))

                .BUILD_CITY(3).atNode(0, 0, "bottomRight").successfully()

                .getGameDetails(3).gameUser(3).check("resources.wheat", is(0))
                .getGameDetails(3).gameUser(3).check("resources.stone", is(0));
    }

    @Test
    public void should_successfully_build_city_if_user_has_enough_resources_in_main_stage() {
        startNewGame(12, 1);
        playPreparationStage();
        giveResourcesToFirstPlayerForCityBuilding()
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .getGameDetails(1).gameUser(1).check("resources.wheat", is(2))
                .getGameDetails(1).gameUser(1).check("resources.stone", is(3))

                .BUILD_CITY(1).atNode(1, -1, "top").successfully()

                .getGameDetails(1).gameUser(1).check("resources.wheat", is(0))
                .getGameDetails(1).gameUser(1).check("resources.stone", is(0));
    }

    @Test
    public void should_fail_when_build_settlement_if_user_does_not_have_resources_in_main_stage() {
        startNewGame(12, 1);
        playPreparationStage()
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .BUILD_CITY(1).atNode(1, -1, "top").failsWithError("ERROR");
    }

    private Scenario startNewGame(int targetVictoryPoints, int initialBuildingSet) {
        return scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                .setHex(HexType.STONE, 11).atCoordinates(0, -2)
                .setHex(HexType.STONE, 10).atCoordinates(1, -2)
                .setHex(HexType.WOOD, 2).atCoordinates(2, -2)

                .setHex(HexType.STONE, 11).atCoordinates(-1, -1)
                .setHex(HexType.WHEAT, 11).atCoordinates(0, -1)
                .setHex(HexType.WHEAT, 10).atCoordinates(1, -1)
                .setHex(HexType.BRICK, 4).atCoordinates(2, -1)

                .setHex(HexType.STONE, 11).atCoordinates(-2, 0)
                .setHex(HexType.WHEAT, 5).atCoordinates(-1, 0)
                .setHex(HexType.EMPTY, null).atCoordinates(0, 0)
                .setHex(HexType.WHEAT, 8).atCoordinates(1, 0)
                .setHex(HexType.WOOD, 4).atCoordinates(2, 0)

                .setHex(HexType.SHEEP, 9).atCoordinates(-2, 1)
                .setHex(HexType.SHEEP, 5).atCoordinates(-1, 1)
                .setHex(HexType.SHEEP, 8).atCoordinates(0, 1)
                .setHex(HexType.WOOD, 6).atCoordinates(1, 1)

                .setHex(HexType.WOOD, 10).atCoordinates(-2, 2)
                .setHex(HexType.WHEAT, 2).atCoordinates(-1, 2)
                .setHex(HexType.BRICK, 6).atCoordinates(0, 2)

                .createNewPublicGameByUser(USER_NAME_1, targetVictoryPoints, initialBuildingSet)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)

                        // take last player from the list each time, when pulling move order from the list to have order: 3, 2, 1
                .nextRandomMoveOrderValues(asList(3, 2, 1))

                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3);
    }

    private Scenario playPreparationStage() {
        return scenario
                .BUILD_SETTLEMENT(1).atNode(0, -1, "topLeft")
                .BUILD_ROAD(1).atEdge(0, -1, "topLeft")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(2, -1, "bottomRight")
                .BUILD_ROAD(2).atEdge(2, -1, "bottomRight")
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(0, 2, "topRight")
                .BUILD_ROAD(3).atEdge(0, 2, "topRight")
                .END_TURN(3)

                .BUILD_SETTLEMENT(3).atNode(0, 0, "bottomRight")
                .BUILD_ROAD(3).atEdge(0, 0, "bottomRight")
                .END_TURN(3)

                .BUILD_SETTLEMENT(2).atNode(0, 0, "bottomLeft")
                .BUILD_ROAD(2).atEdge(0, 0, "bottomLeft")
                .END_TURN(2)

                .BUILD_SETTLEMENT(1).atNode(1, -1, "top")
                .BUILD_ROAD(1).atEdge(1, -1, "topLeft")
                .END_TURN(1);
    }

    private Scenario giveResourcesToFirstPlayerForCityBuilding() {
        return scenario
                .nextRandomDiceValues(asList(5, 6, 5, 5, 6, 6))
                .THROW_DICE(1)
                .END_TURN(1)
                .THROW_DICE(2)
                .END_TURN(2)
                .THROW_DICE(3)
                .END_TURN(3);
    }

    /*
    *          (X, Y) coordinates of generated map:                          Node position at hex:
    *
    *           *----*----*----*----*----*----*                                      top
    *           |    11   |   10    |     2   |                          topLeft *----*----* topRight
    *           |  STONE  |  STONE  |   WOOD  |                                  |         |
    *           | ( 0,-2) | ( 1,-2) | ( 2,-2) |                       bottomLeft *----*----* bottomRight
    *      *----*----*----*----*----*----*----*----*                                bottom
    *      |    11   |   11    |   10    |    4    |
    *      |  STONE  |  WHEAT  |  WHEAT  |  BRICK  |
    *      | (-1,-1) | ( 0,-1) | ( 1,-1) | ( 2,-1) |                        Edge position at hex:
    * *----*----*----*----*----*----*----*----*----*----*
    * |    11   |    5    |         |    8    |    4    |                      topLeft topRight
    * |  STONE  |  WHEAT  |  EMPTY  |  WHEAT  |   WOOD  |                        .====.====.
    * | (-2, 0) | (-1, 0) | ( 0, 0) | ( 1, 0) | ( 2, 0) |                  left ||         || right
    * *----*----*----*----*----*----*----*----*----*----*                        .====.====.
    *      |    9    |    5    |    8    |    6    |                        bottomLeft bottomRight
    *      |  SHEEP  |  SHEEP  |  SHEEP  |   WOOD  |
    *      | (-2, 1) | (-1, 1) | ( 0, 1) | ( 1, 1) |
    *      *----*----*----*----*----*----*----*----*
    *           |    10   |    2    |    6    |
    *           |   WOOD  |  WHEAT  |  BRICK  |
    *           | (-2, 2) | (-1, 2) | ( 0, 2) |
    *           *----*----*----*----*----*----*
    *
    *
    */
}