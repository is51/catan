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
import static catan.domain.model.game.types.DevelopmentCard.KNIGHT;
import static catan.domain.model.game.types.DevelopmentCard.MONOPOLY;
import static catan.domain.model.game.types.DevelopmentCard.ROAD_BUILDING;
import static catan.domain.model.game.types.DevelopmentCard.VICTORY_POINT;
import static catan.domain.model.game.types.DevelopmentCard.YEAR_OF_PLENTY;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)

//@SpringApplicationConfiguration(classes = {TestApplicationConfig.class, RequestResponseLogger.class})
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class BuyDevCardTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_BuyDevCardTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_BuyDevCardTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_BuyDevCardTest";
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
    public void should_fail_when_trying_to_buy_card_before_dice_is_thrown() {
        playPreparationStage()

                .getGameDetails(1).gameUser(1).hasAvailableAction("THROW_DICE")
                .getGameDetails(1).gameUser(1).doesntHaveAvailableAction("BUY_CARD")

                .BUY_CARD(1).failsWithError("ERROR");
    }

    @Test
    public void should_fail_when_trying_to_buy_card_not_in_players_turn() {
        playPreparationStage()

                .BUY_CARD(2).failsWithError("ERROR");
    }

    @Test
    public void should_successfully_buy_card_after_dice_was_thrown() {
        playPreparationStage()

                .THROW_DICE(1)
                .getGameDetails(1).gameUser(1).hasAvailableAction("BUY_CARD")

                .BUY_CARD(1).successfully();
    }

    @Test
    public void should_successfully_buy_second_card_after_first_card_was_already_bought_in_current_turn() {
        playPreparationStage()

                .THROW_DICE(1)

                .BUY_CARD(1).successfully()
                .BUY_CARD(1).successfully();
    }

    @Test
    public void should_return_bought_card_in_response() {
        playPreparationStage()

                .THROW_DICE(1)
                .nextRandomDevelopmentCards(asList(KNIGHT, VICTORY_POINT, ROAD_BUILDING, MONOPOLY, YEAR_OF_PLENTY))

                .BUY_CARD(1).boughtCardIs(KNIGHT)
                .BUY_CARD(1).boughtCardIs(VICTORY_POINT)
                .BUY_CARD(1).boughtCardIs(ROAD_BUILDING)
                .BUY_CARD(1).boughtCardIs(MONOPOLY)
                .BUY_CARD(1).boughtCardIs(YEAR_OF_PLENTY);
    }

    @Test
    public void should_increase_development_cards_when_user_buys_a_new_one() {
        playPreparationStage()

                .THROW_DICE(1)
                .nextRandomDevelopmentCards(asList(KNIGHT, KNIGHT, ROAD_BUILDING, KNIGHT, YEAR_OF_PLENTY, YEAR_OF_PLENTY))
                .startTrackDevCardsQuantity()

                .BUY_CARD(1)
                .getGameDetails(1).gameUser(1).devCardsQuantityChangedBy(1, 0, 0, 0, 0)
                .getGameDetails(2).gameUser(2).devCardsQuantityChangedBy(0, 0, 0, 0, 0)  // no cards should be increased for other players
                .getGameDetails(3).gameUser(3).devCardsQuantityChangedBy(0, 0, 0, 0, 0)  // no cards should be increased for other players

                .BUY_CARD(1)
                .getGameDetails(1).gameUser(1).devCardsQuantityChangedBy(1, 0, 0, 0, 0)

                .BUY_CARD(1)
                .getGameDetails(1).gameUser(1).devCardsQuantityChangedBy(0, 0, 1, 0, 0)

                .BUY_CARD(1)
                .getGameDetails(1).gameUser(1).devCardsQuantityChangedBy(1, 0, 0, 0, 0)

                .BUY_CARD(1)
                .getGameDetails(1).gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 1)

                .BUY_CARD(1)
                .getGameDetails(1).gameUser(1).devCardsQuantityChangedBy(0, 0, 0, 0, 1);
    }

    @Test
    public void should_fail_when_cards_are_over() {
        playPreparationStage()

                .THROW_DICE(1)

                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1)
                .BUY_CARD(1).successfully()

                .BUY_CARD(1).failsWithError("CARDS_ARE_OVER");
    }

    //TODO: use this method when user will require resources for buying cards
    private Scenario playPreparationStageAndBuildCity() {

        return playPreparationStage()
                .nextRandomDiceValues(asList(2, 4)) // P1: +1stone +2wood
                .THROW_DICE(1)                      // P2: +1stone
                .END_TURN(1)                        // P3: +1stone

                .nextRandomDiceValues(asList(2, 4)) // P1: +1stone +2wood
                .THROW_DICE(2)                      // P2: +1stone
                .END_TURN(2)                        // P3: +1stone

                .nextRandomDiceValues(asList(2, 4)) // P1: +1stone +2wood
                .THROW_DICE(3)                      // P2: +1stone
                .END_TURN(3)                        // P3: +1stone

                .nextRandomDiceValues(asList(2, 6)) // P1: +1brick
                .THROW_DICE(1)                      // P2: +1brick
                .END_TURN(1)                        // P3: --

                .nextRandomDiceValues(asList(2, 6)) // P1: +1brick
                .THROW_DICE(2)                      // P2: +1brick
                .END_TURN(2)                        // P3: --

                .nextRandomDiceValues(asList(2, 3)) // P1: +1wheat
                .THROW_DICE(3)                      // P2: --
                .END_TURN(3)                        // P3: --

                .nextRandomDiceValues(asList(2, 3)) // P1: +1wheat
                .THROW_DICE(1)                      // P2: --
                .END_TURN(1)                        // P3: --

                .nextRandomDiceValues(asList(2, 3)) // P1: +1wheat
                .THROW_DICE(2)                      // P2: --
                .END_TURN(2)                        // P3: --

                .nextRandomDiceValues(asList(2, 8)) // P1: +1sheep
                .THROW_DICE(3)                      // P2: +1sheep
                .END_TURN(3)                        // P3: --

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1)
                .BUILD_ROAD(1).atEdge(0, 0, "topLeft")          // P1: -1brick -1wood
                .BUILD_SETTLEMENT(1).atNode(0, 0, "topLeft")    // P1: -1brick -1wood -1wheat -1sheep
                .BUILD_CITY(1).atNode(0, 0, "topLeft")           // P1: -2wheat -3stone
                .END_TURN(1)

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(3)
                .END_TURN(3)
                .startTrackResourcesQuantity();
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
    *           *----*----*----*----*----*----*                                      top
    *           |    6    |    8    |    12   |                          topLeft *----*----* topRight
    *           |  STONE  |  BRICK  |  WHEAT  |                                  |         |
    *           | ( 0,-2) | ( 1,-2) | ( 2,-2) |                       bottomLeft *----*----* bottomRight
    *      *----*----*----*----*----*----*----*----*                                bottom
    *      |    5    |    6    |    10   |    3    |
    *      |  WHEAT  |   WOOD  |  SHEEP  |  BRICK  |
    *      | (-1,-1) | ( 0,-1) | ( 1,-1) | ( 2,-1) |                        Edge position at hex:
    * *----*----*----*----*----*----*----*----*----*----*
    * |    8    |    4    |         |    3    |    11   |                      topLeft topRight
    * |   WOOD  |  STONE  |  EMPTY  |  SHEEP  |  STONE  |                        .====.====.
    * | (-2, 0) | (-1, 0) | ( 0, 0) | ( 1, 0) | ( 2, 0) |                  left ||         || right
    * *----*----*----*----*----*----*----*----*----*----*                        .====.====.
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