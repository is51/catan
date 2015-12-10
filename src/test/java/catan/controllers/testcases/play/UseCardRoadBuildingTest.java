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
import static catan.domain.model.game.types.DevelopmentCard.ROAD_BUILDING;
import static java.util.Arrays.asList;

@RunWith(SpringJUnit4ClassRunner.class)

//@SpringApplicationConfiguration(classes = {TestApplicationConfig.class, RequestResponseLogger.class})  // if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class UseCardRoadBuildingTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_UseCardRoadBuildingTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_UseCardRoadBuildingTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_UseCardRoadBuildingTest";
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
    public void should_successfully_build_2_available_roads_that_can_be_built_separately() {
        playPreparationStageAndBuyCardRoadBuildingAndPassCycle()
                .startTrackResourcesQuantity().and().startTrackDevCardsQuantity()
                .getGameDetails(1)
                    .gameUser(1).doesntHaveAvailableAction("BUILD_ROAD")

                .USE_CARD_ROAD_BUILDING(1).successfully()
                .roadsToBuildQuantityIs(2)

                .getGameDetails(1)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, -1, 0, 0)
                    .gameUser(1).hasAvailableAction("BUILD_ROAD")

                .BUILD_ROAD(1).atEdge(2, -2, "topLeft")
                .getGameDetails(1)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).hasAvailableAction("BUILD_ROAD")

                .BUILD_ROAD(1).atEdge(2, -2, "left")
                .getGameDetails(1)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).doesntHaveAvailableAction("BUILD_ROAD");
    }

    @Test
    public void should_successfully_build_2_available_roads_that_should_be_build_one_by_one() {
            playPreparationStageAndBuyCardRoadBuilding()
                    .END_TURN(1).nextRandomDiceValues(asList(4, 4))
                    .THROW_DICE(2)
                    .END_TURN(2).nextRandomDiceValues(asList(4, 4))
                    .THROW_DICE(3)
                    .END_TURN(3).nextRandomDiceValues(asList(4, 4))
                    .THROW_DICE(1)
                    .END_TURN(1).nextRandomDiceValues(asList(4, 4))
                    .THROW_DICE(2)
                    .END_TURN(2).nextRandomDiceValues(asList(4, 4))
                    .THROW_DICE(3)
                    .END_TURN(3).nextRandomDiceValues(asList(4, 4))
                    .THROW_DICE(1)
                    .END_TURN(1).nextRandomDiceValues(asList(4, 4))
                    .THROW_DICE(2)
                    .END_TURN(2).nextRandomDiceValues(asList(4, 4))
                    .THROW_DICE(3)
                    .END_TURN(3).nextRandomDiceValues(asList(4, 4))
                    .THROW_DICE(1)                                 // P2: 4brick, 11 wood
                    .END_TURN(1).nextRandomDiceValues(asList(3, 3))
                    .THROW_DICE(2)
                    .END_TURN(2).nextRandomDiceValues(asList(3, 3))
                    .THROW_DICE(3)
                    .END_TURN(3).nextRandomDiceValues(asList(3, 3))
                    .THROW_DICE(1)
                    .END_TURN(1).nextRandomDiceValues(asList(3, 3))
                    .THROW_DICE(2)                                 // P2: 12brick, 11 wood

                    .BUILD_ROAD(2).atEdge(0, -2, "right")
                    .BUILD_ROAD(2).atEdge(0, -2, "bottomLeft")
                    .BUILD_ROAD(2).atEdge(0, -2, "left")
                    .BUILD_ROAD(2).atEdge(0, -2, "topLeft")
                    //.BUILD_ROAD(2).atEdge(0, -2, "topRight")
                    .BUILD_ROAD(2).atEdge(2, -2, "left")
                    .BUILD_ROAD(2).atEdge(2, -2, "bottomLeft")
                    .BUILD_ROAD(2).atEdge(2, -2, "bottomRight")
                    .BUILD_ROAD(2).atEdge(2, -2, "right")
                    .BUILD_ROAD(2).atEdge(2, -2, "topRight")
                    //.BUILD_ROAD(2).atEdge(2, -2, "topLeft")

                    .END_TURN(2).nextRandomDiceValues(asList(1, 1))
                    .THROW_DICE(3)
                    .END_TURN(3).nextRandomDiceValues(asList(1, 1))
                    .THROW_DICE(1)


                    .startTrackResourcesQuantity().and().startTrackDevCardsQuantity()
                    .getGameDetails(1)
                        .gameUser(1).doesntHaveAvailableAction("BUILD_ROAD")

                    .USE_CARD_ROAD_BUILDING(1).successfully()
                    .roadsToBuildQuantityIs(2)

                    .getGameDetails(1)
                        .gameUser(1).devCardsQuantityChangedBy(0, 0, -1, 0, 0)
                        .gameUser(1).hasAvailableAction("BUILD_ROAD")

                    .BUILD_ROAD(1).atEdge(2, -2, "topLeft")
                    .getGameDetails(1)
                        .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0)
                        .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                        .gameUser(1).hasAvailableAction("BUILD_ROAD")

                    .BUILD_ROAD(1).atEdge(0, -2, "topRight")
                    .getGameDetails(1)
                        .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0)
                        .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                        .gameUser(1).doesntHaveAvailableAction("BUILD_ROAD");

    }

    @Test
    public void should_successfully_build_1_available_road() {
        playPreparationStageAndBuyCardRoadBuilding()
            .END_TURN(1).nextRandomDiceValues(asList(4, 4))
            .THROW_DICE(2)
            .END_TURN(2).nextRandomDiceValues(asList(4, 4))
            .THROW_DICE(3)
            .END_TURN(3).nextRandomDiceValues(asList(4, 4))
            .THROW_DICE(1)
            .END_TURN(1).nextRandomDiceValues(asList(4, 4))
            .THROW_DICE(2)
            .END_TURN(2).nextRandomDiceValues(asList(4, 4))
            .THROW_DICE(3)
            .END_TURN(3).nextRandomDiceValues(asList(4, 4))
            .THROW_DICE(1)
            .END_TURN(1).nextRandomDiceValues(asList(4, 4))
            .THROW_DICE(2)
            .END_TURN(2).nextRandomDiceValues(asList(4, 4))
            .THROW_DICE(3)
            .END_TURN(3).nextRandomDiceValues(asList(4, 4))
            .THROW_DICE(1)                                 // P2: 4brick, 11 wood
            .END_TURN(1).nextRandomDiceValues(asList(3, 3))
            .THROW_DICE(2)
            .END_TURN(2).nextRandomDiceValues(asList(3, 3))
            .THROW_DICE(3)
            .END_TURN(3).nextRandomDiceValues(asList(3, 3))
            .THROW_DICE(1)
            .END_TURN(1).nextRandomDiceValues(asList(3, 3))
            .THROW_DICE(2)                                 // P2: 12brick, 11 wood

            .BUILD_ROAD(2).atEdge(0, -2, "right")
            .BUILD_ROAD(2).atEdge(0, -2, "bottomLeft")
            .BUILD_ROAD(2).atEdge(0, -2, "left")
            .BUILD_ROAD(2).atEdge(0, -2, "topLeft")
            .BUILD_ROAD(2).atEdge(0, -2, "topRight")
            .BUILD_ROAD(2).atEdge(2, -2, "left")
            .BUILD_ROAD(2).atEdge(2, -2, "bottomLeft")
            .BUILD_ROAD(2).atEdge(2, -2, "bottomRight")
            .BUILD_ROAD(2).atEdge(2, -2, "right")
            .BUILD_ROAD(2).atEdge(2, -2, "topRight")
                    //.BUILD_ROAD(2).atEdge(2, -2, "topLeft")

            .END_TURN(2).nextRandomDiceValues(asList(1, 1))
            .THROW_DICE(3)
            .END_TURN(3).nextRandomDiceValues(asList(1, 1))
            .THROW_DICE(1)


            .startTrackResourcesQuantity().and().startTrackDevCardsQuantity()
            .getGameDetails(1)
            .gameUser(1).doesntHaveAvailableAction("BUILD_ROAD")

            .USE_CARD_ROAD_BUILDING(1).successfully()
            .roadsToBuildQuantityIs(1)

            .getGameDetails(1)
            .gameUser(1).devCardsQuantityChangedBy(0, 0, -1, 0, 0)
            .gameUser(1).hasAvailableAction("BUILD_ROAD")

            .BUILD_ROAD(1).atEdge(2, -2, "topLeft")
            .getGameDetails(1)
            .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0)
            .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
            .gameUser(1).doesntHaveAvailableAction("BUILD_ROAD");

    }

    @Test
    public void should_successfully_use_the_card_if_player_bought_one_in_the_previous_move_and_one_in_the_current() {
        playPreparationStageAndBuyCardRoadBuildingAndPassCycle()
                .nextRandomDevelopmentCards(asList(ROAD_BUILDING))
                .BUY_CARD(1)

                .startTrackResourcesQuantity().and().startTrackDevCardsQuantity()
                .getGameDetails(1)
                    .gameUser(1).doesntHaveAvailableAction("BUILD_ROAD")

                .USE_CARD_ROAD_BUILDING(1).successfully()
                .getGameDetails(1)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, -1, 0, 0)
                    .gameUser(1).hasAvailableAction("BUILD_ROAD");
    }

    @Test
    public void should_fail_if_player_tries_to_use_card_and_does_not_have_place_to_build_any_road() {
        playPreparationStageAndBuyCardRoadBuilding()
                .END_TURN(1).nextRandomDiceValues(asList(4, 4))
                .THROW_DICE(2)
                .END_TURN(2).nextRandomDiceValues(asList(4, 4))
                .THROW_DICE(3)
                .END_TURN(3).nextRandomDiceValues(asList(4, 4))
                .THROW_DICE(1)
                .END_TURN(1).nextRandomDiceValues(asList(4, 4))
                .THROW_DICE(2)
                .END_TURN(2).nextRandomDiceValues(asList(4, 4))
                .THROW_DICE(3)
                .END_TURN(3).nextRandomDiceValues(asList(4, 4))
                .THROW_DICE(1)
                .END_TURN(1).nextRandomDiceValues(asList(4, 4))
                .THROW_DICE(2)
                .END_TURN(2).nextRandomDiceValues(asList(4, 4))
                .THROW_DICE(3)
                .END_TURN(3).nextRandomDiceValues(asList(4, 4))
                .THROW_DICE(1)                                 // P2: 4brick, 11 wood
                .END_TURN(1).nextRandomDiceValues(asList(3, 3))
                .THROW_DICE(2)
                .END_TURN(2).nextRandomDiceValues(asList(3, 3))
                .THROW_DICE(3)
                .END_TURN(3).nextRandomDiceValues(asList(3, 3))
                .THROW_DICE(1)
                .END_TURN(1).nextRandomDiceValues(asList(3, 3))
                .THROW_DICE(2)                                 // P2: 12brick, 11 wood

                .BUILD_ROAD(2).atEdge(0, -2, "right")
                .BUILD_ROAD(2).atEdge(0, -2, "bottomLeft")
                .BUILD_ROAD(2).atEdge(0, -2, "left")
                .BUILD_ROAD(2).atEdge(0, -2, "topLeft")
                .BUILD_ROAD(2).atEdge(0, -2, "topRight")
                .BUILD_ROAD(2).atEdge(2, -2, "left")
                .BUILD_ROAD(2).atEdge(2, -2, "bottomLeft")
                .BUILD_ROAD(2).atEdge(2, -2, "bottomRight")
                .BUILD_ROAD(2).atEdge(2, -2, "right")
                .BUILD_ROAD(2).atEdge(2, -2, "topRight")
                .BUILD_ROAD(2).atEdge(2, -2, "topLeft")

                .END_TURN(2).nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(3)
                .END_TURN(3).nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)


                    .startTrackResourcesQuantity().and().startTrackDevCardsQuantity()

                .USE_CARD_ROAD_BUILDING(1)
                    .failsWithError("ROAD_CANNOT_BE_BUILT")

                .getGameDetails(1)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).doesntHaveAvailableAction("BUILD_ROAD");
    }

    @Test
    public void should_fail_if_player_bought_the_card_in_the_current_move() {
        playPreparationStageAndBuyCardRoadBuilding()
                .startTrackResourcesQuantity()
                .startTrackDevCardsQuantity()
                .USE_CARD_ROAD_BUILDING(1).failsWithError("CARD_BOUGHT_IN_CURRENT_TURN")
                .getGameDetails(1)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_if_player_already_used_some_card_in_the_current_move() {
        playPreparationStageAndBuyCardRoadBuilding()
                .nextRandomDevelopmentCards(asList(ROAD_BUILDING))
                .BUY_CARD(1)

                .END_TURN(1).nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(2)
                .END_TURN(2).nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(3)
                .END_TURN(3).nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1)

                .USE_CARD_ROAD_BUILDING(1).successfully()
                .BUILD_ROAD(1).atEdge(2, -2, "topLeft")
                .BUILD_ROAD(1).atEdge(2, -2, "left")

                .startTrackResourcesQuantity().and().startTrackDevCardsQuantity()

                .USE_CARD_ROAD_BUILDING(1).failsWithError("CARD_ALREADY_USED_IN_CURRENT_TURN")

                .getGameDetails(1)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).doesntHaveAvailableAction("BUILD_ROAD");
    }

    @Test
    public void should_fail_if_player_does_not_have_the_card() {
        playPreparationStage()
                .startTrackResourcesQuantity()
                .startTrackDevCardsQuantity()
                .USE_CARD_ROAD_BUILDING(1).failsWithError("ERROR")
                .getGameDetails(1)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_if_player_has_not_thrown_the_dice() {
        playPreparationStageAndBuyCardRoadBuilding()
                .END_TURN(1)
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(2)
                .END_TURN(2)
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(3)
                .END_TURN(3)
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --

                .startTrackResourcesQuantity()
                .startTrackDevCardsQuantity()
                .USE_CARD_ROAD_BUILDING(1).failsWithError("ERROR")
                .getGameDetails(1)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_if_it_is_not_players_turn() {
        playPreparationStageAndBuyCardRoadBuildingAndPassCycle()
                .END_TURN(1)
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(2)

                .startTrackResourcesQuantity()
                .startTrackDevCardsQuantity()
                .USE_CARD_ROAD_BUILDING(1).failsWithError("ERROR")
                .getGameDetails(1)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0);
    }

    private Scenario playPreparationStageAndBuyCardRoadBuildingAndPassCycle() {
        return playPreparationStageAndBuyCardRoadBuilding()
                .END_TURN(1)

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1);
    }

    private Scenario playPreparationStageAndBuyCardRoadBuilding() {

        return playPreparationStage()
                .nextRandomDiceValues(asList(3, 3)) // P1: +1stone
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(4, 4)) // P1: +2sheep
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(6, 6)) // P1: +1wheat
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(3, 3)) // P1: +1stone
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(4, 4)) // P1: +2sheep
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(6, 6)) // P1: +1wheat
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1)

                .nextRandomDevelopmentCards(asList(ROAD_BUILDING))
                .BUY_CARD(1);
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
                .setHex(STONE, 6) .atCoordinates(0, -2)
                .setHex(SHEEP, 8) .atCoordinates(1, -2)
                .setHex(WHEAT, 12).atCoordinates(2, -2)

                .setHex(WHEAT, 5).atCoordinates(-1, -1)
                .setHex(BRICK, 6).atCoordinates(0, -1)
                .setHex(WOOD,  8).atCoordinates(1, -1)
                .setHex(BRICK, 3).atCoordinates(2, -1)

                .setHex(WOOD,  10) .atCoordinates(-2, 0)
                .setHex(STONE, 4) .atCoordinates(-1, 0)
                .setHex(EMPTY, null).atCoordinates(0, 0)
                .setHex(SHEEP, 3) .atCoordinates(1, 0)
                .setHex(STONE, 11).atCoordinates(2, 0)

                .setHex(SHEEP, 9) .atCoordinates(-2, 1)
                .setHex(BRICK, 9) .atCoordinates(-1, 1)
                .setHex(SHEEP, 11).atCoordinates(0, 1)
                .setHex(WOOD,  4) .atCoordinates(1, 1)

                .setHex(WOOD, 10).atCoordinates(-2, 2)
                .setHex(WHEAT, 2).atCoordinates(-1, 2)
                .setHex(WHEAT, 5).atCoordinates(0, 2)

                .createNewPublicGameByUser(USER_NAME_1)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)

                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3)

                .BUILD_SETTLEMENT(1).atNode(1, -2, "topLeft") // P2: +1brick (after US-69)
                .BUILD_ROAD(1).atEdge(1, -2, "topLeft")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(1, -2, "bottom")
                .BUILD_ROAD(2).atEdge(1, -2, "bottomRight")
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(0, 0, "bottom")
                .BUILD_ROAD(3).atEdge(0, 0, "bottomLeft")
                .END_TURN(3)

                .BUILD_SETTLEMENT(3).atNode(0, 0, "topRight")
                .BUILD_ROAD(3).atEdge(0, 0, "right")
                .END_TURN(3)

                .BUILD_SETTLEMENT(2).atNode(0, -2, "bottom") // P1: +1stone +1wood +1wheat (after US-69)
                .BUILD_ROAD(2).atEdge(0, -2, "bottomRight")
                .END_TURN(2)

                .BUILD_SETTLEMENT(1).atNode(1, -2, "topRight") // P2: +1brick +1stone (after US-69)
                .BUILD_ROAD(1).atEdge(1, -2, "topRight")
                .END_TURN(1);
    }

    /*
    *          (X, Y) coordinates of generated map:                          Node position at hex:
    *
    *           *----*---(1)xxx*xxx(1)---*----*                                      top
    *           |    6    |    8    |    12   |                          topLeft *----*----* topRight
    *           |  STONE  |  SHEEP  |  WHEAT  |                                  |         |
    *           | ( 0,-2) | ( 1,-2) | ( 2,-2) |                       bottomLeft *----*----* bottomRight
    *      *----*---(2)xxx*---(2)xxx*----*----*----*                                bottom
    *      |    5    |    6    |    8    |    3    |
    *      |  WHEAT  |  BRICK  |  WOOD   |  BRICK  |
    *      | (-1,-1) | ( 0,-1) | ( 1,-1) | ( 2,-1) |                        Edge position at hex:
    * *----*----*----*----*----*---(3)---*----*----*----*
    * |    10   |    4    |         X    3    |    11   |                      topLeft topRight
    * |   WOOD  |  STONE  |  EMPTY  X  SHEEP  |  STONE  |                        .====.====.
    * | (-2, 0) | (-1, 0) | ( 0, 0) X ( 1, 0) | ( 2, 0) |                  left ||         || right
    * *----*----*----*----*xxx(3)---*----*----*----*----*                        .====.====.
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