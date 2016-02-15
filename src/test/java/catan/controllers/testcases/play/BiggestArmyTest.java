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

import static catan.domain.model.game.types.DevelopmentCard.KNIGHT;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)

//@SpringApplicationConfiguration(classes = {TestApplicationConfig.class, RequestResponseLogger.class})
@SpringApplicationConfiguration(classes = TestApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class BiggestArmyTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_BiggestArmyTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_BiggestArmyTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_BiggestArmyTest";
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
    public void should_successfully_achieve_biggest_army_owner_if_no_one_had_it_before() {
        startNewGameAndPlayPreparationStage();
        giveResourcesAndBuyKnightsAndPassCycle(1, 3)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .getGameDetails(1)
                    .gameUser(1).hasUsedKnights(0)          //user 1 has no used knights
                    .gameUser(1).hasVictoryPoints(2)        //user 1 did not get 2 VP for biggest army
                    .game().doesNotHaveBiggestArmyOwner()   //there is no biggest army owner in game

                .USE_CARD_KNIGHT(1)                         //got first used knight
                .MOVE_ROBBER(1).toCoordinates(1, 1)

                .getGameDetails(1)
                    .gameUser(1).hasUsedKnights(1)          //user 1 has 1 used knight
                    .gameUser(1).hasVictoryPoints(2)        //user 1 did not get 2 VP for biggest army
                    .game().doesNotHaveBiggestArmyOwner()   //there is no biggest army owner in game

                .END_TURN(1)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .USE_CARD_KNIGHT(1)                         //got second used knight
                .MOVE_ROBBER(1).toCoordinates(2, 0)

                .getGameDetails(1)
                    .gameUser(1).hasUsedKnights(2)          //user 1 has 2 used knights
                    .gameUser(1).hasVictoryPoints(2)        //user 1 did not get 2 VP for biggest army
                    .game().doesNotHaveBiggestArmyOwner()   //there is no biggest army owner in game

                .END_TURN(1)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .USE_CARD_KNIGHT(1)                         //got third used knight

                .getGameDetails(1)
                    .gameUser(1).hasUsedKnights(3)          //user 1 has 3 used knights
                    .gameUser(1).hasVictoryPoints(4)        //user GOT 2 VP for biggest army
                    .game().hasBiggestArmyOwner(1);         //there is no biggest army owner in game
    }

    @Test
    public void should_successfully_change_biggest_army_owner_if_someone_got_more_used_knights() {
        startNewGameAndPlayPreparationStage();
        giveResourcesAndBuyKnightsAndPassCycle(1, 3)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)
                .USE_CARD_KNIGHT(1)                         //got first used knight by user 1
                .MOVE_ROBBER(1).toCoordinates(1, 1)
                .END_TURN(1);

        giveResourcesAndBuyKnightsAndPassCycle(2, 4)
                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(2)
                .USE_CARD_KNIGHT(2)                         //got first used knight by user 2
                .MOVE_ROBBER(2).toCoordinates(2, 0)
                .END_TURN(2)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)
                .USE_CARD_KNIGHT(1)                         //got second used knight by user 1
                .MOVE_ROBBER(1).toCoordinates(1, 1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(2)
                .USE_CARD_KNIGHT(2)                         //got second used knight by user 2
                .MOVE_ROBBER(2).toCoordinates(2, 0)
                .END_TURN(2)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)

                .USE_CARD_KNIGHT(1)                         //got third used knight by user 1
                .MOVE_ROBBER(1).toCoordinates(1, 1)

                .getGameDetails(1)
                    .gameUser(1).hasUsedKnights(3)          //user 1 has 3 used knights
                    .gameUser(1).hasVictoryPoints(4)        //user 1 GOT 2 VP for biggest army
                    .game().hasBiggestArmyOwner(1)          //user 1 is the biggest army owner

                .END_TURN(1)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(2)

                .USE_CARD_KNIGHT(2)                         //got third used knight by user 2
                .MOVE_ROBBER(2).toCoordinates(2, 0)

                .getGameDetails(2)
                    .gameUser(2).hasUsedKnights(3)          //user 2 has 3 used knights
                    .gameUser(2).hasVictoryPoints(2)        //user 2 did not get 2 VP for biggest army
                    .game().hasBiggestArmyOwner(1)          //user 1 is the biggest army owner

                .END_TURN(2)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(6, 6))
                .THROW_DICE(2)

                .USE_CARD_KNIGHT(2)                         //got forth used knight by user 2

                .getGameDetails(2)
                    .gameUser(2).hasUsedKnights(4)          //user 2 has 4 used knights
                    .gameUser(2).hasVictoryPoints(4)        //user 2 GOT 2 VP for biggest army
                    .gameUser(1).hasUsedKnights(3)          //user 1 has 3 used knights
                    .gameUser(1).hasVictoryPoints(2)        //user 1 LOST 2 VP for biggest army
                    .game().hasBiggestArmyOwner(2);         //user 2 is the biggest army owner
    }

    private Scenario giveResourcesAndBuyKnightsAndPassCycle(int moveOrder, int quantity) {
        for (int i = 0; i < quantity; i++) {
            scenario
                    .nextRandomDiceValues(asList(moveOrder, moveOrder))
                    .THROW_DICE(moveOrder)
                    .nextRandomDevelopmentCards(asList(KNIGHT))
                    .BUY_CARD(moveOrder)
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

    private Scenario startNewGameAndPlayPreparationStage() {
        return scenario
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

                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)
                .createNewPublicGameByUser(USER_NAME_1, 12, 1)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)
                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3)

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