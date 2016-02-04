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
//@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class MoveRobberTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_MoveRobberTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_MoveRobberTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_MoveRobberTest";
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
    public void should_successfully_move_robber_hex_without_buildings_and_dont_steal_resources() {
        playPreparationStage()
                .nextRandomDiceValues(asList(4, 3))
                .THROW_DICE(1)

                .getGameDetails(1)
                    .hex(-1, 2).isNotRobbed()
                    .hex(0, 0).isRobbed()
                    .gameUser(1).hasAvailableAction("MOVE_ROBBER")
                    .gameUser(1).doesntHaveAvailableAction("TRADE_PROPOSE")

                .startTrackResourcesQuantity()
                .MOVE_ROBBER(1).toCoordinates(-1, 2).successfully()

                .getGameDetails(1)
                    .hex(-1, 2).isRobbed()
                    .hex(0, 0).isNotRobbed()
                    .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                    .gameUser(1).doesntHaveAvailableAction("CHOOSE_PLAYER_TO_ROB")
                    .gameUser(1).hasAvailableAction("TRADE_PROPOSE")
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_successfully_move_robber_hex_with_1_building_and_dont_steal_resource_from_player_when_he_has_no_resources() {
        playPreparationStage()
                .nextRandomDiceValues(asList(4, 3))
                .THROW_DICE(1)

                .startTrackResourcesQuantity()
                .MOVE_ROBBER(1).toCoordinates(1, 0).successfully()

                .getGameDetails(1)
                .hex(1, 0).isRobbed()

                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).doesntHaveAvailableAction("CHOOSE_PLAYER_TO_ROB")
                .gameUser(1).hasAvailableAction("TRADE_PROPOSE")
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_successfully_move_robber_to_hex_with_1_building_and_steal_1_resource_from_player_when_he_has_1_resource() {
        playPreparationStage()
                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .END_TURN(1)
                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(2)
                .END_TURN(2)
                .nextRandomDiceValues(asList(1, 2)) //Give 1 sheep to second player
                .THROW_DICE(3)
                .END_TURN(3)
                .nextRandomDiceValues(asList(4, 3)) //Robbers action
                .THROW_DICE(1)

                .startTrackResourcesQuantity()
                .MOVE_ROBBER(1).toCoordinates(1, 0).successfully()

                .getGameDetails(1)
                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).doesntHaveAvailableAction("CHOOSE_PLAYER_TO_ROB")
                .gameUser(1).hasAvailableAction("TRADE_PROPOSE")
                .gameUser(1).hasAvailableAction("END_TURN")
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 1, 0, 0)

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, 0, -1, 0, 0);
    }

    @Test
    public void should_successfully_move_robber_to_hex_with_1_building_and_steal_1_resource_from_player_when_he_has_2_different_resource() {
        playPreparationStage()
                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .END_TURN(1)
                .startTrackResourcesQuantity()

                .nextRandomDiceValues(asList(4, 4))//Give 1 brick to second player
                .THROW_DICE(2)
                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(1, 0, 0, 0, 0)

                .END_TURN(2)
                .nextRandomDiceValues(asList(1, 2)) //Give 1 sheep to second player
                .THROW_DICE(3)
                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, 0, 1, 0, 0)

                .END_TURN(3)
                .nextRandomDiceValues(asList(4, 3)) //Robbers action
                .THROW_DICE(1)

                .startTrackResourcesQuantity()
                .nextRandomStolenResources(asList(BRICK))   //Resource to steal
                .nextRandomDiceValues(asList(4, 3))         //Robbers action
                .MOVE_ROBBER(1).toCoordinates(1, 0).successfully()

                .getGameDetails(1)
                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).doesntHaveAvailableAction("CHOOSE_PLAYER_TO_ROB")
                .gameUser(1).resourcesQuantityChangedBy(1, 0, 0, 0, 0)

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(-1, 0, 0, 0, 0);
    }

    @Test
    public void should_successfully_move_robber_to_hex_with_3_buildings_and_steal_1_resource_from_chosen_player_when_he_has_2_different_resources() {
        playPreparationStage()
                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .END_TURN(1)
                .startTrackResourcesQuantity()

                .nextRandomDiceValues(asList(4, 4))//Give 1 brick to second player
                .THROW_DICE(2)
                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(1, 0, 0, 0, 0)

                .END_TURN(2)
                .nextRandomDiceValues(asList(1, 2)) //Give 1 sheep to second player
                .THROW_DICE(3)
                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, 0, 1, 0, 0)

                .END_TURN(3)
                .nextRandomDiceValues(asList(4, 3)) //Robbers action
                .THROW_DICE(1)

                .MOVE_ROBBER(1).toCoordinates(0, -2).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).doesntHaveAvailableAction("END_TURN")
                .gameUser(1).hasAvailableAction("CHOOSE_PLAYER_TO_ROB")

                .nextRandomStolenResources(asList(BRICK))   //Resource to steal
                .CHOOSE_PLAYER_TO_ROB(1).stealResourceFromPlayer(2).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(1, 0, 0, 0, 0)
                .gameUser(1).hasAvailableAction("END_TURN")
                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).doesntHaveAvailableAction("CHOOSE_PLAYER_TO_ROB")

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(-1, 0, 0, 0, 0)

                .getGameDetails(3)
                .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_successfully_move_robber_to_hex_with_3_buildings_and_dont_steal_any_resources_from_chosen_player_when_he_has_no_resources() {
        playPreparationStage()
                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .END_TURN(1)
                .startTrackResourcesQuantity()

                .nextRandomDiceValues(asList(4, 4))//Give 1 brick to second player
                .THROW_DICE(2)
                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(1, 0, 0, 0, 0)

                .END_TURN(2)
                .nextRandomDiceValues(asList(1, 2)) //Give 1 sheep to second player
                .THROW_DICE(3)
                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, 0, 1, 0, 0)

                .END_TURN(3)
                .nextRandomDiceValues(asList(4, 3)) //Robbers action
                .THROW_DICE(1)

                .MOVE_ROBBER(1).toCoordinates(0, -2).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).doesntHaveAvailableAction("END_TURN")
                .gameUser(1).hasAvailableAction("CHOOSE_PLAYER_TO_ROB")

                .CHOOSE_PLAYER_TO_ROB(1).stealResourceFromPlayer(3).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .gameUser(1).hasAvailableAction("END_TURN")
                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).doesntHaveAvailableAction("CHOOSE_PLAYER_TO_ROB")

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .getGameDetails(3)
                .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_successfully_rob_player_when_he_is_only_one_rival_at_robbed_hex_and_current_user_has_his_own_building_at_robbed_hex() {
        playPreparationStage()
                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .END_TURN(1)
                .startTrackResourcesQuantity()

                .nextRandomDiceValues(asList(4, 4))//Give 1 brick to second player
                .THROW_DICE(2)
                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(1, 0, 0, 0, 0)

                .END_TURN(2)
                .nextRandomDiceValues(asList(1, 2)) //Give 1 sheep to second player
                .THROW_DICE(3)
                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, 0, 1, 0, 0)

                .END_TURN(3)
                .nextRandomDiceValues(asList(4, 3)) //Robbers action
                .THROW_DICE(1)

                .nextRandomStolenResources(asList(BRICK))   //Resource to steal
                .MOVE_ROBBER(1).toCoordinates(1, -2).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(1, 0, 0, 0, 0)
                .gameUser(1).hasAvailableAction("END_TURN")
                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).doesntHaveAvailableAction("CHOOSE_PLAYER_TO_ROB")

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(-1, 0, 0, 0, 0)

                .getGameDetails(3)
                .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_when_user_rob_himself() {
        playPreparationStage()
                .nextRandomDiceValues(asList(4, 3))
                .startTrackResourcesQuantity()
                .THROW_DICE(1)

                .MOVE_ROBBER(1).toCoordinates(0, -2).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).doesntHaveAvailableAction("END_TURN")
                .gameUser(1).hasAvailableAction("CHOOSE_PLAYER_TO_ROB")

                .CHOOSE_PLAYER_TO_ROB(1).stealResourceFromPlayer(1).failsWithError("ERROR")

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")
                .gameUser(1).doesntHaveAvailableAction("END_TURN")
                .gameUser(1).hasAvailableAction("CHOOSE_PLAYER_TO_ROB")

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)

                .getGameDetails(3)
                .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_move_robber_if_action_is_not_allowed() {
        playPreparationStage()
                .nextRandomDiceValues(asList(4, 4))
                .THROW_DICE(1)

                .getGameDetails(1)
                    .hex(0, -2).isNotRobbed()
                    .hex(0, 0).isRobbed()
                    .gameUser(1).doesntHaveAvailableAction("MOVE_ROBBER")

                .MOVE_ROBBER(1).toCoordinates(0, -2).failsWithError("ERROR");
    }

    @Test
    public void should_fail_move_robber_if_hex_is_empty() {
        playPreparationStage()
                .nextRandomDiceValues(asList(4, 3))
                .THROW_DICE(1)

                .getGameDetails(1)
                    .hex(0, 2).isNotRobbed()
                    .hex(0, 0).isRobbed()
                    .gameUser(1).hasAvailableAction("MOVE_ROBBER")
                    .gameUser(1).doesntHaveAvailableAction("TRADE_PROPOSE")

                .MOVE_ROBBER(1).toCoordinates(0, 2).failsWithError("ERROR");
    }

    @Test
    public void should_fail_move_robber_if_hex_is_already_robbed() {
        playPreparationStage()
                .nextRandomDiceValues(asList(4, 3))
                .THROW_DICE(1)

                .getGameDetails(1)
                    .hex(0, 0).isRobbed()
                    .gameUser(1).hasAvailableAction("MOVE_ROBBER")

                .MOVE_ROBBER(1).toCoordinates(0, 0).failsWithError("ERROR");
    }


    @Test
    public void should_not_give_resources_to_players_that_have_buildings_on_robbed_hex() {
        playPreparationStage()
                .startTrackResourcesQuantity()
                .nextRandomDiceValues(asList(3, 3))
                .THROW_DICE(1)
                .END_TURN(1)

                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 2, 0, 0, 1)
                .getGameDetails(2).gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 1)
                .getGameDetails(3).gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 1)

                .nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(4, 3))
                .THROW_DICE(3)
                .MOVE_ROBBER(3).toCoordinates(0, -2)
                .END_TURN(3)

                .nextRandomDiceValues(asList(3, 3))
                .THROW_DICE(1)
                .END_TURN(1)

                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(2).gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .getGameDetails(3).gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
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
                .setHex(EMPTY, null).atCoordinates(0, 2)

                .createNewPublicGameByUser(USER_NAME_1)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)

                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3)

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
    *          (3)xxx*---(2)---*----*----*----*                                      top
    *           |    6    X    8    |    12   |                          topLeft *----*----* topRight
    *           |  STONE  X  BRICK  |  WHEAT  |                                  |         |
    *           | ( 0,-2) X ( 1,-2) | ( 2,-2) |                       bottomLeft *----*----* bottomRight
    *      *----*xxx(1)---*---(1)---*----*----*----*                                bottom
    *      |    5    |    6    X    10   |    3    |
    *      |  WHEAT  |   WOOD  X  SHEEP  |  BRICK  |
    *      | (-1,-1) | ( 0,-1) X ( 1,-1) | ( 2,-1) |                        Edge position at hex:
    * *----*----*----*----*----*---(2)---*----*----*----*
    * |    8    |    4    |         X    3    |    11   |                      topLeft topRight
    * |   WOOD  |  STONE  |  EMPTY  X  SHEEP  |  STONE  |                        .====.====.
    * | (-2, 0) | (-1, 0) | ( 0, 0) X ( 1, 0) | ( 2, 0) |                  left ||         || right
    * *----*----*----*----*xxx(3)---*----*----*----*----*                        .====.====.
    *      |    9    |    9    |    11   |    4    |                        bottomLeft bottomRight
    *      |  SHEEP  |  BRICK  |  SHEEP  |   WOOD  |
    *      | (-2, 1) | (-1, 1) | ( 0, 1) | ( 1, 1) |
    *      *----*----*----*----*----*----*----*----*
    *           |    10   |    2    |    5    |
    *           |   WOOD  |  WHEAT  |  EMPTY  |
    *           | (-2, 2) | (-1, 2) | ( 0, 2) |
    *           *----*----*----*----*----*----*
    *
    *
    */

}