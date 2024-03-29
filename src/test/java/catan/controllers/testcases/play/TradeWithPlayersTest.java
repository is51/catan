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
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@RunWith(SpringJUnit4ClassRunner.class)

//@SpringApplicationConfiguration(classes = {ApplicationConfig.class, RequestResponseLogger.class})  // if needed initial request and JSON response logging:
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class TradeWithPlayersTest extends PlayTestUtil {

    public static final String USER_NAME_1 = "user1_TradeWithPlayersTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_TradeWithPlayersTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_TradeWithPlayersTest";
    public static final String USER_PASSWORD_3 = "password3";

    private static boolean initialized = false;

    private Scenario scenario;
    public static final int OFFER_ID = 555;
    public static final int INVALID_OFFER_ID = 21;
    public static final String NOTIFY_MESSAGE_TRADE_REPLY = "Trade proposition!";

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
    public void should_fail_trade_proposal_when_resource_quantity_is_incorrect() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("TRADE_PROPOSE").withoutParameters().and().withoutNotification()

                .TRADE_PROPOSE(1).withResources(0, 0, 0, 0, 0).failsWithError("ERROR")
                .TRADE_PROPOSE(1).withResources(0, 0, 0, 0, -2).failsWithError("ERROR")
                .TRADE_PROPOSE(1).withResources(0, 0, 0, -3, 0).failsWithError("ERROR")
                .TRADE_PROPOSE(1).withResources(0, 0, 0, -3, -2).failsWithError("ERROR")
                .TRADE_PROPOSE(1).withResources(0, -1, 0, -3, -2).failsWithError("ERROR")
                .TRADE_PROPOSE(1).withResources(0, -1, 1, -3, -2).failsWithError("ERROR")
                .TRADE_PROPOSE(1).withResources(0, -1, -1, -3, -2).failsWithError("ERROR")
                .TRADE_PROPOSE(1).withResources(0, 0, 0, 0, 1).failsWithError("ERROR")
                .TRADE_PROPOSE(1).withResources(0, 0, 0, 1, 1).failsWithError("ERROR")
                .TRADE_PROPOSE(1).withResources(0, 0, 1, 1, 1).failsWithError("ERROR")
                .TRADE_PROPOSE(1).withResources(0, 0, 1, -15, 0).failsWithError("ERROR")
                .TRADE_PROPOSE(1).withResources(0, 0, 1, -15, -1).failsWithError("ERROR")
                .TRADE_PROPOSE(1).withResources(0, 0, 1, -15, -15).failsWithError("ERROR");
    }

    @Test
    public void should_fail_trade_proposal_if_it_is_not_players_turn() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(1)

                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("TRADE_PROPOSE").withoutParameters().and().withoutNotification()
                .gameUser(1).doesntHaveAvailableAction("TRADE_REPLY")

                .getGameDetails(2)
                .gameUser(2).doesntHaveAvailableAction("TRADE_REPLY")
                .gameUser(2).doesntHaveAvailableAction("TRADE_PROPOSE")

                .TRADE_PROPOSE(2).withResources(0, 1, -1, 0, 0).failsWithError("ERROR");
    }

    @Test
    public void should_fail_trade_proposal_if_player_has_not_thrown_the_dice() {
        playPreparationStageAndGiveResources()

                .getGameDetails(1)
                .gameUser(1).doesntHaveAvailableAction("TRADE_PROPOSE")

                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, 0).failsWithError("ERROR");
    }

    @Test
    public void should_fail_trade_proposal_when_player_already_send_trade_proposal() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("TRADE_PROPOSE").withoutParameters().and().withoutNotification()

                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, 0).successfully()
                .TRADE_PROPOSE(1).withResources(0, 0, 1, 0, -1).failsWithError("ERROR");
    }

    @Test
    public void should_successfully_send_trade_proposal() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .startTrackResourcesQuantity()

                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("TRADE_PROPOSE").withoutParameters().and().withoutNotification()

                .TRADE_PROPOSE(1).withResources(0, 1, 0, -3, -2).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 0, 0, 0, 0);
    }

    @Test
    public void should_successfully_send_trade_decline_even_if_trade_proposal_declined_already_by_other_player(){
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .startTrackResourcesQuantity()

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, 0).successfully()

                .getGameDetails(1)
                .gameUser(1).hasLogWithCode("TRADE_PROPOSE").hasMessage("You made a new trade propose: 1 building to 1 cable").isHidden()

                .getGameDetails(2)
                .gameUser(2).hasLogWithCode("TRADE_PROPOSE").hasMessage(scenario.getUsername(1) + " made a new trade propose: 1 building to 1 cable").isHidden()

                .getGameDetails(3)
                .gameUser(3).hasLogWithCode("TRADE_PROPOSE").hasMessage(scenario.getUsername(1) + " made a new trade propose: 1 building to 1 cable").isHidden()

                .getGameDetails(2)
                .gameUser(2).hasAvailableAction("TRADE_REPLY")
                .withParameters("offerId=" + OFFER_ID)
                .and()
                .withNotification(NOTIFY_MESSAGE_TRADE_REPLY)

                .TRADE_DECLINE(2).withOfferId(OFFER_ID).successfully()
                .getGameDetails(1)
                .gameUser(1).hasLogWithCode("TRADE_DECLINE").hasMessage(scenario.getUsername(2) + " declined your trade propose").isDisplayedOnTop()

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .gameUser(2).doesntHaveAvailableAction("TRADE_REPLY")
                .gameUser(2).hasLogWithCode("TRADE_DECLINE").hasMessage("You declined " + scenario.getUsername(1) + "’s trade propose").isHidden()

                .getGameDetails(3)
                .gameUser(3).hasLogWithCode("TRADE_DECLINE").hasMessage(scenario.getUsername(2) + " declined " +
                scenario.getUsername(1) + "’s trade propose").isDisplayedOnTop()
                .gameUser(3).hasAvailableAction("TRADE_REPLY")
                .withParameters("offerId=" + OFFER_ID)
                .and()
                .withNotification(NOTIFY_MESSAGE_TRADE_REPLY)

                .TRADE_DECLINE(3).withOfferId(OFFER_ID).successfully()

                .getGameDetails(3)
                .gameUser(3).resourcesQuantityChangedBy(0, 0, 0, 0, 0)
                .gameUser(3).doesntHaveAvailableAction("TRADE_REPLY");
    }


    @Test
    public void should_successfully_send_trade_decline_even_if_trade_proposal_accepted_already_by_other_player(){
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, 0).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully()
                .TRADE_DECLINE(3).withOfferId(OFFER_ID).successfully();
    }

    @Test
    public void should_fail_trade_decline_if_there_is_no_proposal() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .TRADE_DECLINE(3).withOfferId(OFFER_ID).failsWithError("ERROR");
    }

    @Test
    public void should_fail_trade_decline_if_decline_was_already_sent_from_this_player() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, 0).successfully()
                .TRADE_DECLINE(2).withOfferId(OFFER_ID).successfully()
                .TRADE_DECLINE(2).withOfferId(OFFER_ID).failsWithError("ERROR")
                .TRADE_DECLINE(3).withOfferId(OFFER_ID).successfully()
                .TRADE_DECLINE(3).withOfferId(OFFER_ID).failsWithError("ERROR");
    }

    @Test
    public void should_fail_trade_decline_if_accept_was_already_sent_from_this_player() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, 0).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully()
                .TRADE_DECLINE(2).withOfferId(OFFER_ID).failsWithError("ERROR");
    }

    @Test
    public void should_fail_trade_decline_if_invalid_offer_id_sent() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, 0).successfully()
                .TRADE_DECLINE(2).withOfferId(INVALID_OFFER_ID).failsWithError("OFFER_IS_NOT_ACTIVE");
    }

    @Test
    public void should_fail_trade_decline_before_accept_if_same_player_sent_proposal() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, 0).successfully()
                .TRADE_DECLINE(1).withOfferId(OFFER_ID).failsWithError("ERROR")
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully();
    }

    @Test
    public void should_fail_trade_decline_after_accept_if_same_player_sent_proposal() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, 0).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully()
                .TRADE_DECLINE(1).withOfferId(OFFER_ID).failsWithError("ERROR");
    }

    @Test
    public void should_successfully_send_trade_decline_if_player_has_resources() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, -1).successfully()
                .TRADE_DECLINE(2).withOfferId(OFFER_ID).successfully();
    }

    @Test
    public void should_successfully_send_trade_decline_if_player_has_no_resources() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(5, 5, 5, -1, 5).successfully()
                .TRADE_DECLINE(2).withOfferId(OFFER_ID).successfully();
    }

    @Test
    public void should_fail_trade_accept_if_there_is_no_proposal() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .TRADE_ACCEPT(3).withOfferId(OFFER_ID).failsWithError("ERROR");
    }

    @Test
    public void should_fail_trade_accept_if_invalid_offer_id_sent() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, 0).successfully()
                .TRADE_ACCEPT(2).withOfferId(INVALID_OFFER_ID).failsWithError("OFFER_IS_NOT_ACTIVE");
    }

    @Test
    public void should_fail_trade_accept_when_player_has_not_enough_resources() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 5, 0, -1, -1).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).failsWithError("ERROR");
    }

    @Test
    public void should_fail_trade_accept_if_same_player_sent_proposal() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, -1).successfully()
                .TRADE_ACCEPT(1).withOfferId(OFFER_ID).failsWithError("ERROR");
    }

    @Test
    public void should_fail_trade_accept_with_common_error_if_proposal_was_already_accepted_by_this_player() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, -1).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).failsWithError("ERROR");
    }

    @Test
    public void should_fail_trade_accept_with_OFFER_ALREADY_ACCEPTED_error_if_proposal_was_already_accepted_by_another_player() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, -1).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully()
                .TRADE_ACCEPT(3).withOfferId(OFFER_ID).failsWithError("OFFER_ALREADY_ACCEPTED");
    }

    @Test
    public void should_successfully_send_trade_accept_with_1_source_and_1_target_resource_and_perform_resource_change() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .startTrackResourcesQuantity()

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -1, 0).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 1, 0, -1, 0)
                .gameUser(1).hasLogWithCode("TRADE_ACCEPT").hasMessage("You exchanged 1 building to 1 cable with " + scenario.getUsername(2)).isDisplayedOnTop()

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, -1, 0, 1, 0)
                .gameUser(2).hasLogWithCode("TRADE_ACCEPT").hasMessage("You exchanged 1 cable to 1 building with " + scenario.getUsername(1)).isDisplayedOnTop()

                .getGameDetails(3)
                .gameUser(3).hasLogWithCode("TRADE_ACCEPT").hasMessage(scenario.getUsername(1) + "’s trade propose was accepted by " + scenario.getUsername(2)).isDisplayedOnTop()
        ;
    }

    @Test
    public void should_successfully_send_trade_accept_with_2_same_source_and_1_target_resources_and_perform_resource_change() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .startTrackResourcesQuantity()

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -2, 0).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 1, 0, -2, 0)

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, -1, 0, 2, 0);
    }

    @Test
    public void should_successfully_send_trade_accept_with_2_different_source_and_1_target_resources_and_perform_resource_change() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .startTrackResourcesQuantity()

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 0, -2, -1).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 1, 0, -2, -1)

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, -1, 0, 2, 1);
    }

    @Test
    public void should_successfully_send_trade_accept_with_2_different_source_and_2_same_target_resources_and_perform_resource_change() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .startTrackResourcesQuantity()

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 2, 0, -3, -1).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 2, 0, -3, -1)

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, -2, 0, 3, 1);
    }

    @Test
    public void should_successfully_send_trade_accept_with_2_different_source_and_2_different_target_resources_and_perform_resource_change() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .startTrackResourcesQuantity()

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 2, 1, -3, -1).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 2, 1, -3, -1)

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, -2, -1, 3, 1);
    }

    @Test
    public void should_successfully_send_trade_accept_with_1_source_and_2_same_target_resource_and_perform_resource_change() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .startTrackResourcesQuantity()

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 2, 0, 0, -1).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 2, 0, 0, -1)

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, -2, 0, 0, 1);
    }

    @Test
    public void should_successfully_send_trade_accept_with_1_source_and_2_different_target_resource_and_perform_resource_change() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)
                .startTrackResourcesQuantity()

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 1, 1, 0, -1).successfully()
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID).successfully()

                .getGameDetails(1)
                .gameUser(1).resourcesQuantityChangedBy(0, 1, 1, 0, -1)

                .getGameDetails(2)
                .gameUser(2).resourcesQuantityChangedBy(0, -1, -1, 0, 1);
    }

    @Test
    public void should_not_allow_other_actions_until_trade_proposal_not_closed() {
        playPreparationStageAndGiveResources().nextRandomDiceValues(asList(1, 1))
                .THROW_DICE(1)

                .nextOfferIds(singletonList(OFFER_ID))
                .TRADE_PROPOSE(1).withResources(0, 0, 1, -1, 0)
                .TRADE_ACCEPT(2).withOfferId(OFFER_ID)
                .TRADE_DECLINE(3).withOfferId(OFFER_ID)

                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("TRADE_PROPOSE")
                .gameUser(1).hasAvailableAction("BUY_CARD")
                .gameUser(1).hasAvailableAction("END_TURN")

                .nextOfferIds(singletonList(OFFER_ID + 1))
                .TRADE_PROPOSE(1).withResources(0, 0, 1, -1, 0)

                .getGameDetails(1)
                .gameUser(1).doesntHaveAvailableAction("TRADE_PROPOSE")
                .gameUser(1).doesntHaveAvailableAction("BUY_CARD")
                .gameUser(1).doesntHaveAvailableAction("END_TURN")

                .TRADE_ACCEPT(2).withOfferId(OFFER_ID + 1)
                .TRADE_DECLINE(3).withOfferId(OFFER_ID + 1)

                .getGameDetails(1)
                .gameUser(1).hasAvailableAction("TRADE_PROPOSE")
                .gameUser(1).hasAvailableAction("BUY_CARD")
                .gameUser(1).hasAvailableAction("END_TURN");

    }

    private Scenario playPreparationStageAndGiveResources() {
        return playPreparationStage()
                .nextRandomDiceValues(asList(3, 3)) // P1: +1 stone, P2: +1 wood
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(3, 3)) // P1: +1 stone, P2: +1 wood
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(3, 2)) // P1: +1 wheat, P2: +1 sheep
                .THROW_DICE(3)
                .END_TURN(3)

                .nextRandomDiceValues(asList(3, 2)) // P1: +1 wheat, P2: +1 sheep
                .THROW_DICE(1)
                .END_TURN(1)

                .nextRandomDiceValues(asList(3, 2)) // P1: +1 wheat, P2: +1 sheep
                .THROW_DICE(2)
                .END_TURN(2)

                .nextRandomDiceValues(asList(1, 1)) // P1, P2, P3: --
                .THROW_DICE(3)
                .END_TURN(3);
    }

    private Scenario playPreparationStage() {
        return scenario
                //possible dice values: 2, 3, 3, 4, 4, 5, 5, 6, 6, 7 8, 8, 9, 9, 10, 10, 11, 11, 12
                //possible hex type values: WOOD, WOOD, WOOD, WOOD, SHEEP, SHEEP, SHEEP, SHEEP,
                //    WHEAT, WHEAT, WHEAT, WHEAT, BRICK, BRICK, BRICK, STONE, STONE, STONE, EMPTY
                .setHex(WHEAT, 2).atCoordinates(-1, 2)
                .setHex(SHEEP, 3).atCoordinates(1, 0)
                .setHex(BRICK, 3).atCoordinates(2, -1)
                .setHex(WOOD, 4).atCoordinates(1, 1)
                .setHex(STONE, 4).atCoordinates(-1, 0)
                .setHex(WHEAT, 5).atCoordinates(-1, -1)
                .setHex(WHEAT, 8).atCoordinates(0, 2)
                .setHex(STONE, 6).atCoordinates(0, -2)
                .setHex(WOOD, 6).atCoordinates(0, -1)
                .setHex(EMPTY, null).atCoordinates(0, 0)
                .setHex(SHEEP, 5).atCoordinates(1, -2)
                .setHex(WOOD, 8).atCoordinates(-2, 0)
                .setHex(SHEEP, 9).atCoordinates(-2, 1)
                .setHex(BRICK, 9).atCoordinates(-1, 1)
                .setHex(STONE, 10).atCoordinates(1, -1)
                .setHex(WOOD, 10).atCoordinates(-2, 2)
                .setHex(BRICK, 11).atCoordinates(2, 0)
                .setHex(SHEEP, 11).atCoordinates(0, 1)
                .setHex(WHEAT, 12).atCoordinates(2, -2).
                        loginUser(USER_NAME_1, USER_PASSWORD_1).
                        loginUser(USER_NAME_2, USER_PASSWORD_2).
                        loginUser(USER_NAME_3, USER_PASSWORD_3).
                        createNewPublicGameByUser(USER_NAME_1).
                        joinPublicGame(USER_NAME_2).
                        joinPublicGame(USER_NAME_3).
                        setUserReady(USER_NAME_1).
                        setUserReady(USER_NAME_2).
                        setUserReady(USER_NAME_3)

                .BUILD_SETTLEMENT(1).atNode(0, -2, "topLeft")
                .BUILD_ROAD(1).atEdge(0, -2, "topLeft")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(0, 0, "topRight")
                .BUILD_ROAD(2).atEdge(0, 0, "right")
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(0, 0, "bottom")
                .BUILD_ROAD(3).atEdge(0, 0, "bottomLeft")
                .END_TURN(3)

                .BUILD_SETTLEMENT(3).atNode(2, -1, "topRight")
                .BUILD_ROAD(3).atEdge(2, -1, "right")
                .END_TURN(3)

                .BUILD_SETTLEMENT(2).atNode(0, -1, "topRight")
                .BUILD_ROAD(2).atEdge(0, -1, "right")
                .END_TURN(2)

                .BUILD_SETTLEMENT(1).atNode(-1, -1, "topLeft")
                .BUILD_ROAD(1).atEdge(-1, -1, "topLeft")
                .END_TURN(1);
    }

//                     Coordinates of generated map:
//
//                    (ANY)          (SHEEP)                                       [Node position at hex]:
//                    /   \           /  \
//                   (1)xxx*----*----*----*----*----*                                      top
//                    |    6    X    5    |    12   |                          topLeft *----*----* topRight
//                    |  STONE  X  SHEEP  |  WHEAT  | (ANY)                            |         |
//                    | ( 0,-2) X ( 1,-2) | ( 2,-2) |/   |                  bottomLeft *----*----* bottomRight
//              (1)xxx*----*----*---(2)---*----*----*---(3)                               bottom
//            /  |    5    |    6    |    10   |    3    X
//      (STONE)  |  WHEAT  |   WOOD  |  STONE  |  BRICK  X
//            \  | (-1,-1) | ( 0,-1) | ( 1,-1) | ( 2,-1) X                        [Edge position at hex]:
//          *----*----*----*----*----*---(2)---*----*----*----*
//          |    8    |    4    |         X    3    |    11   | \                     topLeft topRight
//          |   WOOD  |  STONE  |  EMPTY  X  SHEEP  |  BRICK  |  (ANY)                  .====.====.
//          | (-2, 0) | (-1, 0) | ( 0, 0) X ( 1, 0) | ( 2, 0) | /                 left ||         || right
//          *----*----*----*----*xxx(3)---*----*----*----*----*                         .====.====.
//             / |    9    |    9    |    11   |    4    |                        bottomLeft bottomRight
//      (WHEAT)  |  SHEEP  |  BRICK  |  SHEEP  |   WOOD  |
//             \ | (-2, 1) | (-1, 1) | ( 0, 1) | ( 1, 1) |
//               *----*----*----*----*----*----*----*----*
//                    |    10   |    2    |    8    |\   |
//                    |   WOOD  |  WHEAT  |  WHEAT  | (BRICK)
//                    | (-2, 2) | (-1, 2) | ( 0, 2) |
//                    *----*----*----*----*----*----*
//                     \  /           \  /
//                    (ANY)          (WOOD)
//
//
}