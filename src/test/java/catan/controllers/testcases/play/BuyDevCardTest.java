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
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
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
    public void should_successfully_take_resources_from_player_when_he_buy_card() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForDevCardBuying(1, 1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .startTrackResourcesQuantity()

                .BUY_CARD(1).successfully()
                .getGameDetails(1).gameUser(1).resourcesQuantityChangedBy(0, 0, -1, -1, -1);
    }

    @Test
    public void should_fail_when_trying_to_buy_card_if_user_does_not_have_resources() {
        startNewGame();
        playPreparationStage()
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .getGameDetails(1).gameUser(1).check("resources.wheat", is(0))
                .getGameDetails(1).gameUser(1).check("resources.sheep", is(0))
                .getGameDetails(1).gameUser(1).check("resources.stone", is(0))
                .getGameDetails(1).gameUser(1).doesntHaveAvailableAction("BUY_CARD")

                .BUY_CARD(1).failsWithError("ERROR");
    }

    @Test
    public void should_successfully_buy_card_if_user_has_enough_resources() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForDevCardBuying(1, 1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .getGameDetails(1).gameUser(1).check("resources.wheat", greaterThanOrEqualTo(1))
                .getGameDetails(1).gameUser(1).check("resources.sheep", greaterThanOrEqualTo(1))
                .getGameDetails(1).gameUser(1).check("resources.stone", greaterThanOrEqualTo(1))

                .BUY_CARD(1).successfully();
    }

    @Test
    public void should_fail_when_trying_to_buy_card_before_dice_is_thrown() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForDevCardBuying(1, 1)

                .getGameDetails(1).gameUser(1).hasAvailableAction("THROW_DICE")
                .getGameDetails(1).gameUser(1).doesntHaveAvailableAction("BUY_CARD")

                .BUY_CARD(1).failsWithError("ERROR");
    }

    @Test
    public void should_fail_when_trying_to_buy_card_not_in_players_turn() {
        startNewGame();
        playPreparationStage()

                .BUY_CARD(2).failsWithError("ERROR");
    }

    @Test
    public void should_successfully_buy_card_after_dice_was_thrown() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForDevCardBuying(1, 1)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)
                .getGameDetails(1).gameUser(1).hasAvailableAction("BUY_CARD")

                .BUY_CARD(1).successfully();
    }

    @Test
    public void should_successfully_buy_second_card_after_first_card_was_already_bought_in_current_turn() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForDevCardBuying(1, 2)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .BUY_CARD(1).successfully()
                .BUY_CARD(1).successfully();
    }

    @Test
    public void should_return_bought_card_in_response() {
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForDevCardBuying(1, 5)
                .nextRandomDiceValues(asList(6, 6))
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
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForDevCardBuying(1, 6)
                .nextRandomDiceValues(asList(6, 6))
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
        startNewGame();
        playPreparationStage();
        giveResourcesToPlayerForDevCardBuying(1, 26)
                .nextRandomDiceValues(asList(6, 6))
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

    private Scenario startNewGame() {
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

                .createNewPublicGameByUser(USER_NAME_1)
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

    /*
    *          (X, Y) coordinates of generated map:                          Node position at hex:
    *
    *           *----*----*----*----*----*----*                                      top
    *           |    2    |    2    |    4    |                          topLeft *----*----* topRight
    *           |  STONE  |  WHEAT  |  WHEAT  |                                  |         |
    *           | ( 0,-2) | ( 1,-2) | ( 2,-2) |                       bottomLeft *----*----* bottomRight
    *      *----*----*----*----*----*----*----*----*                                bottom
    *      |    6    |    2    |    4    |    4    |
    *      |  WHEAT  |  SHEEP  |  SHEEP  |  STONE  |
    *      | (-1,-1) | ( 0,-1) | ( 1,-1) | ( 2,-1) |                        Edge position at hex:
    * *----*----*----*----*----*----*----*----*----*----*
    * |    6    |    6    |         |    3    |    11   |                      topLeft topRight
    * |  SHEEP  |  STONE  |  EMPTY  |  SHEEP  |  STONE  |                        .====.====.
    * | (-2, 0) | (-1, 0) | ( 0, 0) | ( 1, 0) | ( 2, 0) |                  left ||         || right
    * *----*----*----*----*----*----*----*----*----*----*                        .====.====.
    *      |    9    |    9    |    11   |    5    |                        bottomLeft bottomRight
    *      |  SHEEP  |  BRICK  |  SHEEP  |   WOOD  |
    *      | (-2, 1) | (-1, 1) | ( 0, 1) | ( 1, 1) |
    *      *----*----*----*----*----*----*----*----*
    *           |    10   |    3    |    5    |
    *           |   WOOD  |  WHEAT  |  WHEAT  |
    *           | (-2, 2) | (-1, 2) | ( 0, 2) |
    *           *----*----*----*----*----*----*
    *
    *
    */

}