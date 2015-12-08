package catan.controllers.testcases.game;

import catan.controllers.ctf.TestApplicationConfig;
import catan.controllers.ctf.Scenario;
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

import static catan.domain.model.game.types.DevelopmentCard.VICTORY_POINT;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
//Add it if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = {TestApplicationConfig.class, RequestResponseLogger.class})
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class FinishGameTest extends PlayTestUtil {
    public static final String USER_NAME_1 = "user1_FinishGameTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_FinishGameTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_FinishGameTest";
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
    public void should_successfully_finish_game_when_target_victory_points_is_3_and_user_builds_3_settlements() {
        //Given
        startNewGame(3);
        playPreparationStage();
        giveResourcesToPlayerForRoadBuilding(1);
        giveResourcesToPlayerForSettlementBuilding(1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)
                .BUILD_ROAD(1).atEdge(2, -2, "topRight")

                //When
                .BUILD_SETTLEMENT(1).atNode(2, -2, "topRight")

                //Then
                .getGameDetails(1).statusIsFinished();
    }

    @Test
    public void should_not_finish_game_when_target_victory_points_is_4_and_user_builds_3_settlements() {
        //Given
        startNewGame(4);
        playPreparationStage();
        giveResourcesToPlayerForRoadBuilding(1);
        giveResourcesToPlayerForSettlementBuilding(1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)
                .BUILD_ROAD(1).atEdge(2, -2, "topRight")

                //When
                .BUILD_SETTLEMENT(1).atNode(2, -2, "topRight") //victory points of user should be less than target victory points

                //Then
                .getGameDetails(1).statusIsPlaying();
    }

    @Test
    public void should_successfully_finish_game_when_target_victory_points_is_4_and_user_builds_3_settlements_and_builds_longest_road() {
         //test case to check when real victory points is grated than target victory points
         //TODO: implement when add functionality of adding 2 victory points for longest way
    }

    @Test
    public void should_successfully_finish_game_when_target_victory_points_is_4_and_user_builds_2_settlements_and_has_2_victory_point_dev_card() {
        //Given
        startNewGameWithMapForBuyingDevCards(4, 1);
        playPreparationStageOnMapForBuyingDevCards();
        giveResourcesToPlayerOnMapForDevCardBuying(1, 2)
                .nextRandomDiceValues(asList(6, 6))
                .nextRandomDevelopmentCards(asList(VICTORY_POINT, VICTORY_POINT))
                .getGameDetails(1).statusIsPlaying()

                //when
                .THROW_DICE(1)
                .BUY_CARD(1)
                .BUY_CARD(1)

                 //Then
                .getGameDetails(1).statusIsFinished();
    }

    private Scenario giveResourcesToPlayerOnMapForDevCardBuying(int moveOrder, int quantity) {
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

    private Scenario startNewGame(int targetVictoryPoints) {
        return scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                .setHex(HexType.STONE, 11).atCoordinates(0, -2)
                .setHex(HexType.BRICK, 2).atCoordinates(1, -2)
                .setHex(HexType.WOOD, 2).atCoordinates(2, -2)

                .setHex(HexType.STONE, 11).atCoordinates(-1, -1)
                .setHex(HexType.WHEAT, 3).atCoordinates(0, -1)
                .setHex(HexType.SHEEP, 3).atCoordinates(1, -1)
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

                .createNewPublicGameByUser(USER_NAME_1, targetVictoryPoints)
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

                .BUILD_SETTLEMENT(1).atNode(0, 0, "top")
                .BUILD_ROAD(1).atEdge(0, 0, "topLeft")
                .END_TURN(1);
    }

    /*
    *          (X, Y) coordinates of generated map:                          Node position at hex:
    *
    *           *----*----*----*----*----*----*                                      top
    *           |    11   |    2    |     2   |                          topLeft *----*----* topRight
    *           |  STONE  |  BRICK  |   WOOD  |                                  |         |
    *           | ( 0,-2) | ( 1,-2) | ( 2,-2) |                       bottomLeft *----*----* bottomRight
    *      *----*----*----*----*----*----*----*----*                                bottom
    *      |    11   |    3    |    3    |    4    |
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
