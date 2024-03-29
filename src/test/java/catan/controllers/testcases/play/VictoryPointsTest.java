package catan.controllers.testcases.play;

import catan.config.ApplicationConfig;
import catan.controllers.ctf.Scenario;
import catan.controllers.util.PlayTestUtil;
import catan.domain.model.dashboard.types.HexType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static catan.domain.model.game.types.DevelopmentCard.VICTORY_POINT;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
//Add it if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = {ApplicationConfig.class, RequestResponseLogger.class})
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class VictoryPointsTest extends PlayTestUtil {
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

    @Test
    public void should_increase_only_real_victory_points_when_buy_VICTORY_POINT_card() {
        startNewGameWithMapForBuyingDevCards(4, 1);
        playPreparationStageOnMapForBuyingDevCards();
        giveResourcesToPlayerForDevCardBuying(1, 1)
                .nextRandomDiceValues(asList(6, 6))
                .nextRandomDevelopmentCards(asList(VICTORY_POINT))

                //Check details before buying card
                .getGameDetails(1)
                .gameUser(1).check("achievements.realVictoryPoints",    is(2))
                .gameUser(1).check("achievements.displayVictoryPoints", is(2))

                .THROW_DICE(1)
                .BUY_CARD(1)

                //Check details after buying card
                .getGameDetails(1)
                .gameUser(1).check("achievements.realVictoryPoints",    is(3))
                .gameUser(1).check("achievements.displayVictoryPoints", is(2));
    }

    @Test
    public void should_show_real_victory_points_only_for_current_user_when_game_is_playing() {
        startNewGame(12, 1);
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
        startNewGame(3, 1);
        playPreparationStage();
        giveResourcesToPlayerForRoadBuilding(1);
        giveResourcesToPlayerForSettlementBuilding(1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)
                .BUILD_ROAD(1).atEdge(2, -2, "topRight")
                .BUILD_SETTLEMENT(1).atNode(2, -2, "topRight")

                .getGameDetails(1).statusIsFinished()

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

    @Test
    public void should_successfully_calculate_victory_points_when_build_settlement() {
        startNewGame(12, 1)
                .getGameDetails(1)
                .gameUser(1).check("achievements.displayVictoryPoints", is(0))
                .gameUser(2).check("achievements.displayVictoryPoints", is(0))
                .gameUser(3).check("achievements.displayVictoryPoints", is(0))

                .BUILD_SETTLEMENT(1).atNode(2, -2, "topLeft")

                .getGameDetails(1)
                .gameUser(1).check("achievements.displayVictoryPoints", is(1))
                .gameUser(2).check("achievements.displayVictoryPoints", is(0))
                .gameUser(3).check("achievements.displayVictoryPoints", is(0));
    }

    @Test
    public void should_successfully_calculate_victory_points_when_build_city_in_preparation_stage() {
        startNewGame(12, 2)
                .getGameDetails(1)
                .gameUser(1).check("achievements.displayVictoryPoints", is(0))
                .gameUser(2).check("achievements.displayVictoryPoints", is(0))
                .gameUser(3).check("achievements.displayVictoryPoints", is(0))

                .BUILD_SETTLEMENT(1).atNode(2, -2, "topLeft")
                .BUILD_ROAD(1).atEdge(2, -2, "topLeft")
                .END_TURN(1)

                .getGameDetails(1)
                .gameUser(1).check("achievements.displayVictoryPoints", is(1))
                .gameUser(2).check("achievements.displayVictoryPoints", is(0))
                .gameUser(3).check("achievements.displayVictoryPoints", is(0))

                .BUILD_SETTLEMENT(2).atNode(2, -1, "bottomRight")
                .BUILD_ROAD(2).atEdge(2, -1, "bottomRight")
                .END_TURN(2)

                .getGameDetails(1)
                .gameUser(1).check("achievements.displayVictoryPoints", is(1))
                .gameUser(2).check("achievements.displayVictoryPoints", is(1))
                .gameUser(3).check("achievements.displayVictoryPoints", is(0))

                .BUILD_SETTLEMENT(3).atNode(0, 2, "topRight")
                .BUILD_ROAD(3).atEdge(0, 2, "topRight")
                .END_TURN(3)

                .getGameDetails(1)
                .gameUser(1).check("achievements.displayVictoryPoints", is(1))
                .gameUser(2).check("achievements.displayVictoryPoints", is(1))
                .gameUser(3).check("achievements.displayVictoryPoints", is(1))

                .BUILD_CITY(3).atNode(0, 0, "bottomRight")

                .getGameDetails(1)
                .gameUser(1).check("achievements.displayVictoryPoints", is(1))
                .gameUser(2).check("achievements.displayVictoryPoints", is(1))
                .gameUser(3).check("achievements.displayVictoryPoints", is(3));
    }

    @Test
    public void should_successfully_calculate_victory_points_when_build_city_in_main_stage() {
        startNewGame(12, 1);
        playPreparationStage();
        giveResourcesToFirstPlayerForCityBuilding(1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .getGameDetails(1)
                .gameUser(1).check("achievements.displayVictoryPoints", is(2))
                .gameUser(2).check("achievements.displayVictoryPoints", is(2))
                .gameUser(3).check("achievements.displayVictoryPoints", is(2))

                .BUILD_CITY(1).atNode(2, -2, "topLeft")

                .getGameDetails(1)
                .gameUser(1).check("achievements.displayVictoryPoints", is(3))
                .gameUser(2).check("achievements.displayVictoryPoints", is(2))
                .gameUser(3).check("achievements.displayVictoryPoints", is(2));
    }

    private Scenario giveResourcesToPlayerForDevCardBuying(int moveOrder, int quantity) {
        for (int i = 0; i < quantity; i++) {
            scenario
                    .nextRandomDiceValues(asList(moveOrder, moveOrder))
                    .THROW_DICE(moveOrder)
                    .END_TURN(moveOrder)

                    .nextRandomDiceValues(asList(6, 6))
                    .THROW_DICE(moveOrder == 1 ? 2 : moveOrder == 2 ? 3 : 1)
                    .END_TURN(moveOrder == 1 ? 2 : moveOrder == 2 ? 3 : 1)

                    .nextRandomDiceValues(asList(6, 6))
                    .THROW_DICE(moveOrder == 1 ? 3 : moveOrder == 2 ? 1 : 2)
                    .END_TURN(moveOrder == 1 ? 3 : moveOrder == 2 ? 1 : 2);
        }
        return scenario;
    }

    private Scenario giveResourcesToPlayerForRoadBuilding(int moveOrder) {
        return scenario
                .nextRandomDiceValues(asList(moveOrder, moveOrder))
                .THROW_DICE(moveOrder)
                .END_TURN(moveOrder)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(moveOrder == 1 ? 2 : moveOrder == 2 ? 3 : 1)
                .END_TURN(moveOrder == 1 ? 2 : moveOrder == 2 ? 3 : 1)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(moveOrder == 1 ? 3 : moveOrder == 2 ? 1 : 2)
                .END_TURN(moveOrder == 1 ? 3 : moveOrder == 2 ? 1 : 2);
    }

    private Scenario giveResourcesToPlayerForSettlementBuilding(int moveOrder) {
        return scenario
                .nextRandomDiceValues(asList(moveOrder, moveOrder))
                .THROW_DICE(moveOrder)
                .END_TURN(moveOrder)

                .nextRandomDiceValues(asList(moveOrder, moveOrder == 3 ? moveOrder + 2 : moveOrder + 1))
                .THROW_DICE(moveOrder == 1 ? 2 : moveOrder == 2 ? 3 : 1)
                .END_TURN(moveOrder == 1 ? 2 : moveOrder == 2 ? 3 : 1)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(moveOrder == 1 ? 3 : moveOrder == 2 ? 1 : 2)
                .END_TURN(moveOrder == 1 ? 3 : moveOrder == 2 ? 1 : 2);
    }

    private Scenario giveResourcesToFirstPlayerForCityBuilding(int moveOrder) {
        return scenario
                .nextRandomDiceValues(asList(1, 2))
                .THROW_DICE(moveOrder)
                .END_TURN(moveOrder)

                .nextRandomDiceValues(asList(1, 2))
                .THROW_DICE(moveOrder == 1 ? 2 : moveOrder == 2 ? 3 : 1)
                .END_TURN(moveOrder == 1 ? 2 : moveOrder == 2 ? 3 : 1)

                .nextRandomDiceValues(asList(1, 2))
                .THROW_DICE(moveOrder == 1 ? 3 : moveOrder == 2 ? 1 : 2)
                .END_TURN(moveOrder == 1 ? 3 : moveOrder == 2 ? 1 : 2);
    }

    private Scenario startNewGame(int targetVictoryPoints, int initialBuildingSet) {
        return scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                .setHex(HexType.SHEEP, 3).atCoordinates(0, -2)
                .setHex(HexType.BRICK, 2).atCoordinates(1, -2)
                .setHex(HexType.WOOD, 2).atCoordinates(2, -2)

                .setHex(HexType.STONE, 3).atCoordinates(-1, -1)
                .setHex(HexType.WHEAT, 3).atCoordinates(0, -1)
                .setHex(HexType.SHEEP, 11).atCoordinates(1, -1)
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

    private Scenario startNewGameWithMapForBuyingDevCards(int targetVictoryPoints, int initialBuildingSet) {
        return scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                .setHex(HexType.STONE, 2).atCoordinates(0, -2)
                .setHex(HexType.WHEAT, 2).atCoordinates(1, -2)
                .setHex(HexType.WHEAT, 4).atCoordinates(2, -2)

                .setHex(HexType.WHEAT, 6).atCoordinates(-1, -1)
                .setHex(HexType.SHEEP, 2).atCoordinates(0, -1)
                .setHex(HexType.SHEEP, 4).atCoordinates(1, -1)
                .setHex(HexType.STONE, 4).atCoordinates(2, -1)

                .setHex(HexType.SHEEP, 6).atCoordinates(-2, 0)
                .setHex(HexType.STONE, 6).atCoordinates(-1, 0)
                .setHex(HexType.EMPTY, null).atCoordinates(0, 0)
                .setHex(HexType.SHEEP, 3).atCoordinates(1, 0)
                .setHex(HexType.STONE, 11).atCoordinates(2, 0)

                .setHex(HexType.SHEEP, 9).atCoordinates(-2, 1)
                .setHex(HexType.BRICK, 9).atCoordinates(-1, 1)
                .setHex(HexType.SHEEP, 11).atCoordinates(0, 1)
                .setHex(HexType.WOOD, 5).atCoordinates(1, 1)

                .setHex(HexType.WOOD, 10).atCoordinates(-2, 2)
                .setHex(HexType.WHEAT, 3).atCoordinates(-1, 2)
                .setHex(HexType.BRICK, 5).atCoordinates(0, 2)

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
                .BUILD_SETTLEMENT(1).atNode(2, -2, "topLeft")
                .BUILD_ROAD(1).atEdge(2, -2, "topLeft")
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

                .BUILD_SETTLEMENT(1).atNode(0, -2, "bottom")
                .BUILD_ROAD(1).atEdge(0, -2, "bottomLeft")
                .END_TURN(1);
    }

    /*
    *          (X, Y) coordinates of generated map:                          Node position at hex:
    *
    *           *----*----*----*----*----*----*                                      top
    *           |    3    |    2    |     2   |                          topLeft *----*----* topRight
    *           |  SHEEP  |  BRICK  |   WOOD  |                                  |         |
    *           | ( 0,-2) | ( 1,-2) | ( 2,-2) |                       bottomLeft *----*----* bottomRight
    *      *----*----*----*----*----*----*----*----*                                bottom
    *      |    3    |    3    |    11    |    4    |
    *      |  STONE  |  WHEAT  |  SHEEP  |  BRICK  |
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

    private Scenario playPreparationStageOnMapForBuyingDevCards() {
        return scenario
                .BUILD_SETTLEMENT(1).atNode(0, -1, "top")
                .BUILD_ROAD(1).atEdge(0, -1, "topRight")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(2, -2, "bottom")
                .BUILD_ROAD(2).atEdge(2, -2, "bottomRight")
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(-1, -1, "bottom")
                .BUILD_ROAD(3).atEdge(-1, -1, "bottomRight")
                .END_TURN(3)

                .BUILD_SETTLEMENT(3).atNode(-2, 2, "topLeft")
                .BUILD_ROAD(3).atEdge(-2, 2, "topLeft")
                .END_TURN(3)

                .BUILD_SETTLEMENT(2).atNode(-1, 2, "topLeft")
                .BUILD_ROAD(2).atEdge(-1, 2, "topLeft")
                .END_TURN(2)

                .BUILD_SETTLEMENT(1).atNode(0, 2, "topLeft")
                .BUILD_ROAD(1).atEdge(0, 2, "topLeft")
                .END_TURN(1);
    }
}
