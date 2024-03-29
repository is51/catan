package catan.controllers.testcases.play;

import catan.config.ApplicationConfig;
import catan.controllers.ctf.Scenario;
import catan.controllers.util.PlayTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static catan.domain.model.dashboard.types.HexType.BRICK;
import static catan.domain.model.dashboard.types.HexType.EMPTY;
import static catan.domain.model.dashboard.types.HexType.SHEEP;
import static catan.domain.model.dashboard.types.HexType.STONE;
import static catan.domain.model.dashboard.types.HexType.WHEAT;
import static catan.domain.model.dashboard.types.HexType.WOOD;
import static catan.domain.model.game.types.DevelopmentCard.YEAR_OF_PLENTY;
import static java.util.Arrays.asList;

@RunWith(SpringJUnit4ClassRunner.class)

//@SpringApplicationConfiguration(classes = {ApplicationConfig.class, RequestResponseLogger.class})  // if needed initial request and JSON response logging:
//@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class UseCardYearOfPlentyTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_UseCardYearOfPlentyTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_UseCardYearOfPlentyTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_UseCardYearOfPlentyTest";
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
    public void should_successfully_give_two_different_resources() {
        playPreparationStageAndBuyCardYearOfPlentyAndPassCycle()
                .getGameDetails(1)
                    .gameUser(1).hasAvailableAction("USE_CARD_YEAR_OF_PLENTY").withoutNotification()

                .startTrackResourcesQuantity().and().startTrackDevCardsQuantity()

                .USE_CARD_YEAR_OF_PLENTY(1, "BRICK", "STONE").successfully()
                .getGameDetails(1)
                    .gameUser(1).resourcesQuantityChangedBy(1, 0, 0, 0, 1)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, -1)
                    .gameUser(1).hasLogWithCode("USE_CARD_YEAR_OF_PLENTY").hasMessage("You used startup bonus and took server, consultant").isHidden()

                .getGameDetails(2)
                    .gameUser(2).hasLogWithCode("USE_CARD_YEAR_OF_PLENTY").hasMessage(scenario.getUsername(1) + " used startup bonus").isDisplayedOnTop()

                .getGameDetails(3)
                    .gameUser(3).hasLogWithCode("USE_CARD_YEAR_OF_PLENTY").hasMessage(scenario.getUsername(1) + " used startup bonus").isDisplayedOnTop()
        ;
    }

    @Test
    public void should_successfully_give_two_same_resources() {
        playPreparationStageAndBuyCardYearOfPlentyAndPassCycle()
                .startTrackResourcesQuantity()
                .startTrackDevCardsQuantity()
                .USE_CARD_YEAR_OF_PLENTY(1, "WHEAT", "WHEAT").successfully()
                .getGameDetails(1)
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 2, 0)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, -1);
    }

    @Test
    public void should_successfully_use_the_card_if_player_bought_one_in_the_previous_move_and_one_in_the_current() {
        playPreparationStageAndBuyCardYearOfPlentyAndPassCycle()
                .nextRandomDevelopmentCards(asList(YEAR_OF_PLENTY))
                .BUY_CARD(1)

                .startTrackResourcesQuantity()
                .startTrackDevCardsQuantity()
                .USE_CARD_YEAR_OF_PLENTY(1, "SHEEP", "WOOD").successfully()
                .getGameDetails(1)
                    .gameUser(1).resourcesQuantityChangedBy(0, 1, 1, 0, 0)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, -1);
    }

    @Test
    public void should_fail_if_player_bought_the_card_in_the_current_move() {
        playPreparationStageAndBuyCardYearOfPlenty()
                .startTrackResourcesQuantity()
                .startTrackDevCardsQuantity()
                .USE_CARD_YEAR_OF_PLENTY(1, "BRICK", "STONE").failsWithError("CARD_BOUGHT_IN_CURRENT_TURN")
                .getGameDetails(1)
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_if_player_already_used_some_card_in_the_current_move() {
        playPreparationStageAndBuyCardYearOfPlenty()
                .nextRandomDevelopmentCards(asList(YEAR_OF_PLENTY))
                .BUY_CARD(1)

                .END_TURN(1)
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(2)
                .END_TURN(2)
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(3)
                .END_TURN(3)
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1)

                .USE_CARD_YEAR_OF_PLENTY(1, "BRICK", "STONE").successfully()

                .startTrackResourcesQuantity()
                .startTrackDevCardsQuantity()
                .USE_CARD_YEAR_OF_PLENTY(1, "BRICK", "STONE").failsWithError("CARD_ALREADY_USED_IN_CURRENT_TURN")
                .getGameDetails(1)
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_if_player_does_not_have_the_card() {
        playPreparationStage()
                .startTrackResourcesQuantity()
                .startTrackDevCardsQuantity()
                .USE_CARD_YEAR_OF_PLENTY(1, "BRICK", "STONE").failsWithError("ERROR")
                .getGameDetails(1)
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_if_player_has_not_thrown_the_dice() {
        playPreparationStageAndBuyCardYearOfPlenty()
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
                .USE_CARD_YEAR_OF_PLENTY(1, "BRICK", "STONE").failsWithError("ERROR")
                .getGameDetails(1)
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_if_it_is_not_players_turn() {
        playPreparationStageAndBuyCardYearOfPlentyAndPassCycle()
                .END_TURN(1)
                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(2)

                .startTrackResourcesQuantity()
                .startTrackDevCardsQuantity()
                .USE_CARD_YEAR_OF_PLENTY(1, "BRICK", "STONE").failsWithError("ERROR")
                .getGameDetails(1)
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_fail_when_resource_type_is_incorrect() {
        playPreparationStageAndBuyCardYearOfPlentyAndPassCycle()
                .startTrackResourcesQuantity()
                .startTrackDevCardsQuantity()

                .USE_CARD_YEAR_OF_PLENTY(1, "XXX", "STONE").failsWithError("ERROR")
                .getGameDetails(1)
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0)

                .USE_CARD_YEAR_OF_PLENTY(1, "STONE", "YYY").failsWithError("ERROR")
                .getGameDetails(1)
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0)

                .USE_CARD_YEAR_OF_PLENTY(1, "XXX", "YYY").failsWithError("ERROR")
                .getGameDetails(1)
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0)

                .USE_CARD_YEAR_OF_PLENTY(1, "", "").failsWithError("ERROR")
                .getGameDetails(1)
                    .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                    .gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 0);
    }

    private Scenario playPreparationStageAndBuyCardYearOfPlentyAndPassCycle() {
        return playPreparationStageAndBuyCardYearOfPlenty()
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

    private Scenario playPreparationStageAndBuyCardYearOfPlenty() {

        return playPreparationStage()
                .nextRandomDiceValues(asList(4, 6)) // P1: +1sheep
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(2, 3)) // P1: +1wheat
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(2, 4)) // P1: +1stone (and +2wood)
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(4, 6)) // P1: +1sheep
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(2, 3)) // P1: +1wheat
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(2, 4)) // P1: +1stone (and +2wood)
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1)

                .nextRandomDevelopmentCards(asList(YEAR_OF_PLENTY))
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
    *           |   WOOD  |  WHEAT  |  WHEAT  |
    *           | (-2, 2) | (-1, 2) | ( 0, 2) |
    *           *----*----*----*----*----*----*
    *
    *
    */

}