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

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;


@RunWith(SpringJUnit4ClassRunner.class)

//@SpringApplicationConfiguration(classes = {TestApplicationConfig.class, RequestResponseLogger.class})  // if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class BuildSettlementTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_BuildSettlementTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_BuildSettlementTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_BuildSettlementTest";
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
    public void should_not_take_resources_from_player_when_build_settlement_in_preparation_stage() {
        startNewGame()
                .startTrackResourcesQuantity()

                .BUILD_SETTLEMENT(1).atNode(2, -2, "topLeft").successfully()
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_successfully_take_resources_from_player_when_build_settlement_in_main_stage() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForRoadBuilding(1, 1);
        giveResourcesToPlayerForSettlementBuilding(1, 1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)
                .BUILD_ROAD(1).atEdge(2, -2, "topRight")

                .startTrackResourcesQuantity()

                .BUILD_SETTLEMENT(1).atNode(2, -2, "topRight").successfully()
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(-1, -1, -1, -1, 0);
    }

    @Test
    public void should_successfully_build_settlement_even_if_user_does_not_have_resources_in_preparation_stage() {
        startNewGame()
                .getGameDetails(1).gameUser(1).check("resources.brick", is(0))
                .getGameDetails(1).gameUser(1).check("resources.wood", is(0))
                .getGameDetails(1).gameUser(1).check("resources.wheat", is(0))
                .getGameDetails(1).gameUser(1).check("resources.sheep", is(0))

                .BUILD_SETTLEMENT(1).atNode(2, -2, "topLeft").successfully();
    }

    @Test
    public void should_successfully_build_settlement_if_user_has_enough_resources_in_main_stage() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForRoadBuilding(1, 1);
        giveResourcesToPlayerForSettlementBuilding(1, 1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)
                .BUILD_ROAD(1).atEdge(2, -2, "topRight");

        Set<Integer> availableNodeIds = new HashSet<Integer>();
        availableNodeIds.add(scenario.node(2, -2, "topRight").getMapElementId());      // has neighbour road on left edge

        scenario
                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("BUILD_SETTLEMENT").withParameters("nodeIds=" + availableNodeIds).and().withoutNotification()
                .gameUser(1).check("resources.brick", greaterThanOrEqualTo(1))
                .gameUser(1).check("resources.wood", greaterThanOrEqualTo(1))
                .gameUser(1).check("resources.wheat", greaterThanOrEqualTo(1))
                .gameUser(1).check("resources.sheep", greaterThanOrEqualTo(1))

                .BUILD_SETTLEMENT(1).atNode(2, -2, "topRight").successfully();
    }

    @Test
    public void should_fail_when_build_settlement_if_user_does_not_have_enough_resources_in_main_stage() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForRoadBuilding(1, 1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)
                .BUILD_ROAD(1).atEdge(2, -2, "topRight")

                .getGameDetails(1).gameUser(1).check("resources.brick", is(0))
                .getGameDetails(1).gameUser(1).check("resources.wood", is(0))
                .getGameDetails(1).gameUser(1).check("resources.wheat", is(1))
                .getGameDetails(1).gameUser(1).check("resources.sheep", is(1))
                .getGameDetails(1).gameUser(1).doesntHaveAvailableAction("BUILD_SETTLEMENT")

                .BUILD_SETTLEMENT(1).atNode(2, -2, "topRight").failsWithError("ERROR");
    }

    @Test
    public void should_successfully_build_settlement_on_any_empty_node_in_preparation_stage() {
        startNewGame();
        Set<Integer> allNodeIds = scenario.getAllNodeIds();

        scenario
                //Given
                .gameUser(1).hasAvailableAction("BUILD_SETTLEMENT").withParameters("nodeIds=" + allNodeIds).and().withoutNotification()
                .statusIsPlaying().and().node(0, 0, "topLeft").buildingIsEmpty()

                //When
                .BUILD_SETTLEMENT(1).atNode(0, 0, "topLeft")

                //Then
                .getGameDetails(1).node(0, 0, "topLeft").buildingBelongsToPlayer(1);
    }

    @Test
    public void should_fail_if_try_to_build_settlement_on_existing_settlement_in_preparation_stage() {
        startNewGame();
        Set<Integer> allNodeIds = scenario.getAllNodeIds();
        Set<Integer> allNodeIdsExcludeUnavailable = new HashSet<Integer>(allNodeIds);

        scenario
                //Given
                .gameUser(1).hasAvailableAction("BUILD_SETTLEMENT").withParameters("nodeIds=" + allNodeIds).and().withoutNotification()
                .BUILD_SETTLEMENT(1).atNode(0, 0, "topLeft")
                .BUILD_ROAD(1).atEdge(0, 0, "topLeft")
                .END_TURN(1);

        allNodeIdsExcludeUnavailable.remove(scenario.node(0, 0, "topLeft").getMapElementId());      // has building
        allNodeIdsExcludeUnavailable.remove(scenario.node(0, 0, "bottomLeft").getMapElementId());   // bottom neighbour
        allNodeIdsExcludeUnavailable.remove(scenario.node(0, 0, "top").getMapElementId());          // right neighbour
        allNodeIdsExcludeUnavailable.remove(scenario.node(-1, 0, "top").getMapElementId());         // left neighbour

        scenario
                .getGameDetails(2).gameUser(2).hasAvailableAction("BUILD_SETTLEMENT").withParameters("nodeIds=" + allNodeIdsExcludeUnavailable).and().withoutNotification()
                
                //When                              //Then
                .BUILD_SETTLEMENT(2).atNode(0, 0, "topLeft").failsWithError("ERROR")

                //Check that this player still can build settlement on empty node
                .getGameDetails(2).node(0, 0, "topRight").buildingIsEmpty()
                .BUILD_SETTLEMENT(2).atNode(0, 0, "topRight")
                .getGameDetails(2).node(0, 0, "topRight").buildingBelongsToPlayer(2);
    }

    @Test
    public void should_fail_if_try_to_build_settlement_close_to_another_settlement_less_than_2_roads_in_preparation_stage() {
        startNewGame()
                //Given
                .BUILD_SETTLEMENT(1).atNode(0, 0, "topLeft")
                .BUILD_ROAD(1).atEdge(0, 0, "topLeft")
                .END_TURN(1)

                //When                          //Then
                .BUILD_SETTLEMENT(2).atNode(0, 0, "top").failsWithError("ERROR")

                //Check that this player still can build settlement on empty node
                .getGameDetails(2).node(0, 0, "topRight").buildingIsEmpty()
                .BUILD_SETTLEMENT(2).atNode(0, 0, "topRight")
                .getGameDetails(2).node(0, 0, "topRight").buildingBelongsToPlayer(2);
    }

    @Test
    public void should_fail_if_try_to_build_settlement_and_there_are_no_neighbour_roads_that_belongs_to_this_player_in_main_stage() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForSettlementBuilding(1, 1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .BUILD_SETTLEMENT(1).atNode(-2, 2, "bottomLeft").failsWithError("ERROR");
    }

    @Test
    public void should_successfully_give_resources_to_player_when_build_last_initial_settlement_in_preparation_stage_before_last_road() {
        startNewGame()
                .startTrackResourcesQuantity()
                .BUILD_SETTLEMENT(1).atNode(2, -2, "topLeft")
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(2).gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(3).gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .BUILD_ROAD(1).atEdge(2, -2, "topLeft")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(2, -1, "bottomRight")
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(2).gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(3).gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .BUILD_ROAD(2).atEdge(2, -1, "bottomRight")
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(0, 2, "topRight")
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(2).gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(3).gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .BUILD_ROAD(3).atEdge(0, 2, "topRight")
                .END_TURN(3)

                .BUILD_SETTLEMENT(3).atNode(0, 0, "bottomRight")
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(2).gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(3).gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 1, 1)
                .BUILD_ROAD(3).atEdge(0, 0, "bottomRight")
                .END_TURN(3)

                .BUILD_SETTLEMENT(2).atNode(0, 0, "bottomLeft")
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(2).gameUser(2).resourcesQuantityChangedBy(0, 0, 1, 1, 0)
                .getGameDetails(3).gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .BUILD_ROAD(2).atEdge(0, 0, "bottomLeft")
                .END_TURN(2)

                .BUILD_SETTLEMENT(1).atNode(0, -1, "top")
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(1, 0, 0, 1, 1)
                .getGameDetails(2).gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(3).gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_successfully_give_resources_to_player_when_build_last_initial_settlement_in_preparation_stage_if_it_is_last_building() {
        startNewGame(12, 4)
                .startTrackResourcesQuantity()

                .BUILD_SETTLEMENT(1).atNode(0, 0, "bottomRight")
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 1, 1)
                .getGameDetails(2).gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(3).gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(0, 0, "bottomLeft")
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(2).gameUser(2).resourcesQuantityChangedBy(0, 0, 1, 1, 0)
                .getGameDetails(3).gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(0, -1, "top")
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(2).gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(3).gameUser(3).resourcesQuantityChangedBy(1, 0, 0, 1, 1);
    }

    @Test
    public void should_successfully_build_five_settlements_then_one_city_and_one_more_settlement_on_default_map() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForSettlementBuilding(1, 5);
        giveResourcesToPlayerForCityBuilding(1, 1);
        giveResourcesToPlayerForRoadBuilding(1, 8)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)
                .BUILD_ROAD(1).atEdge(2, -2, "topRight")
                .BUILD_SETTLEMENT(1).atNode(2, -2, "topRight").settlementsLimitNotReached()
                .BUILD_ROAD(1).atEdge(-2, 0, "bottomLeft")
                .BUILD_SETTLEMENT(1).atNode(-2, 0, "bottomLeft").settlementsLimitNotReached()
                .BUILD_ROAD(1).atEdge(2, -2, "right")
                .BUILD_ROAD(1).atEdge(2, -2, "bottomRight")
                .BUILD_SETTLEMENT(1).atNode(2, -2, "bottom").settlementsLimitReached()
                .BUILD_CITY(1).atNode(2, -2, "bottom").successfully()
                .BUILD_ROAD(1).atEdge(-2, 0, "left")
                .BUILD_ROAD(1).atEdge(-2, 0, "topLeft")
                .BUILD_SETTLEMENT(1).atNode(-2, 0, "top").settlementsLimitReached()
                .BUILD_ROAD(1).atEdge(1, -2, "topRight")
                .BUILD_ROAD(1).atEdge(1, -2, "topLeft")
                .BUILD_SETTLEMENT(1).atNode(1, -2, "topLeft").failsWithError("SETTLEMENTS_LIMIT_IS_REACHED");

    }

    @Test
    public void should_fail_to_build_sixth_settlement_on_default_map() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForSettlementBuilding(1, 4);
        giveResourcesToPlayerForRoadBuilding(1, 6)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)
                .BUILD_ROAD(1).atEdge(2, -2, "topRight")
                .BUILD_SETTLEMENT(1).atNode(2, -2, "topRight").settlementsLimitNotReached()
                .BUILD_ROAD(1).atEdge(-2, 0, "bottomLeft")
                .BUILD_SETTLEMENT(1).atNode(-2, 0, "bottomLeft").settlementsLimitNotReached()
                .BUILD_ROAD(1).atEdge(2, -2, "right")
                .BUILD_ROAD(1).atEdge(2, -2, "bottomRight")
                .BUILD_SETTLEMENT(1).atNode(2, -2, "bottom").settlementsLimitReached()
                .BUILD_ROAD(1).atEdge(-2, 0, "left")
                .BUILD_ROAD(1).atEdge(-2, 0, "topLeft")
                .BUILD_SETTLEMENT(1).atNode(-2, 0, "top").failsWithError("SETTLEMENTS_LIMIT_IS_REACHED");

    }

    private Scenario giveResourcesToPlayerForRoadBuilding(int moveOrder, int quantity) {
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

    private Scenario giveResourcesToPlayerForCityBuilding(int moveOrder, int quantity) {
        for (int i = 0; i < 3; i++) {
            giveResourcesToPlayerForSettlementBuilding(moveOrder, quantity);
        }
        return scenario;
    }

    private Scenario giveResourcesToPlayerForSettlementBuilding(int moveOrder, int quantity) {
        for (int i = 0; i < quantity; i++) {
            scenario
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
        return scenario;
    }

    private Scenario startNewGame() {
        return startNewGame(12, 1);
    }

    private Scenario startNewGame(int targetVictoryPoints, int initialBuildingSet) {
        return scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                .setHex(HexType.STONE, 8).atCoordinates(0, -2)
                .setHex(HexType.BRICK, 2).atCoordinates(1, -2)
                .setHex(HexType.WOOD, 2).atCoordinates(2, -2)

                .setHex(HexType.SHEEP, 8).atCoordinates(-1, -1)
                .setHex(HexType.WHEAT, 8).atCoordinates(0, -1)
                .setHex(HexType.SHEEP, 11).atCoordinates(1, -1)
                .setHex(HexType.BRICK, 4).atCoordinates(2, -1)

                .setHex(HexType.STONE, 3).atCoordinates(-2, 0)
                .setHex(HexType.WHEAT, 3).atCoordinates(-1, 0)
                .setHex(HexType.EMPTY, null).atCoordinates(0, 0)
                .setHex(HexType.WHEAT, 11).atCoordinates(1, 0)
                .setHex(HexType.WOOD, 4).atCoordinates(2, 0)

                .setHex(HexType.SHEEP, 3).atCoordinates(-2, 1)
                .setHex(HexType.SHEEP, 5).atCoordinates(-1, 1)
                .setHex(HexType.STONE, 5).atCoordinates(0, 1)
                .setHex(HexType.WOOD, 6).atCoordinates(1, 1)

                .setHex(HexType.WOOD, 10).atCoordinates(-2, 2)
                .setHex(HexType.WHEAT, 5).atCoordinates(-1, 2)
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
                .BUILD_SETTLEMENT(1).atNode(2, -2, "topLeft")
                .BUILD_ROAD(1).atEdge(2, -2, "topLeft")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(2, -1, "bottomRight")
                .BUILD_ROAD(2).atEdge(2, -1, "bottomRight")
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(0, 2, "topRight")
                .BUILD_ROAD(3).atEdge(0, 2, "topRight")
                .END_TURN(3)

                .BUILD_SETTLEMENT(3).atNode(0, -2, "bottom")
                .BUILD_ROAD(3).atEdge(0, -2, "bottomRight")
                .END_TURN(3)

                .BUILD_SETTLEMENT(2).atNode(0, 1, "bottomLeft")
                .BUILD_ROAD(2).atEdge(0, 1, "bottomLeft")
                .END_TURN(2)

                .BUILD_SETTLEMENT(1).atNode(-2, 1, "top")
                .BUILD_ROAD(1).atEdge(-2, 1, "topLeft")
                .END_TURN(1);
    }

    /*
    *          (X, Y) coordinates of generated map:                          Node position at hex:
    *
    *           *----*----*----*---(1)===*----*                                      top
    *           |    8    |    2    |     2   |                          topLeft *----*----* topRight
    *           |  STONE  |  BRICK  |   WOOD  |                                  |         |
    *           | ( 0,-2) | ( 1,-2) | ( 2,-2) |                       bottomLeft *----*----* bottomRight
    *      *----*---(3)===*----*----*----*----*----*                                bottom
    *      |    8    |    8    |    11   |    4    |
    *      |  SHEEP  |  WHEAT  |  SHEEP  |  BRICK  |
    *      | (-1,-1) | ( 0,-1) | ( 1,-1) | ( 2,-1) |                        Edge position at hex:
    * *----*----*----*----*----*----*----*----*===(2)---*
    * |    3    |    3    |         |    11   |    4    |                      topLeft topRight
    * |  STONE  |  WHEAT  |  EMPTY  |  WHEAT  |   WOOD  |                        .====.====.
    * | (-2, 0) | (-1, 0) | ( 0, 0) | ( 1, 0) | ( 2, 0) |                  left ||         || right
    * *----*===(1)---*----*----*----*----*----*----*----*                        .====.====.
    *      |    3    |    5    |    5    |    6    |                        bottomLeft bottomRight
    *      |  SHEEP  |  SHEEP  |  STONE  |   WOOD  |
    *      | (-2, 1) | (-1, 1) | ( 0, 1) | ( 1, 1) |
    *      *----*----*----*---(2)===*----*===(3)---*
    *           |    10   |    5    |    6    |
    *           |   WOOD  |  WHEAT  |  BRICK  |
    *           | (-2, 2) | (-1, 2) | ( 0, 2) |
    *           *----*----*----*----*----*----*
    *
    *
    */
}