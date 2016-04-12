package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.game.actions.ActionDetails;
import catan.domain.transfer.output.game.actions.AvailableActionsDetails;
import catan.services.PlayService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;

import static catan.domain.model.game.types.GameUserActionCode.BUILD_CITY;
import static catan.domain.model.game.types.GameUserActionCode.BUILD_ROAD;
import static catan.domain.model.game.types.GameUserActionCode.BUILD_SETTLEMENT;
import static catan.domain.model.game.types.GameUserActionCode.BUY_CARD;
import static catan.domain.model.game.types.GameUserActionCode.CHOOSE_PLAYER_TO_ROB;
import static catan.domain.model.game.types.GameUserActionCode.END_TURN;
import static catan.domain.model.game.types.GameUserActionCode.KICK_OFF_RESOURCES;
import static catan.domain.model.game.types.GameUserActionCode.MOVE_ROBBER;
import static catan.domain.model.game.types.GameUserActionCode.THROW_DICE;
import static catan.domain.model.game.types.GameUserActionCode.TRADE_PORT;
import static catan.domain.model.game.types.GameUserActionCode.TRADE_REPLY;
import static catan.domain.model.game.types.GameUserActionCode.USE_CARD_KNIGHT;
import static catan.domain.model.game.types.GameUserActionCode.USE_CARD_MONOPOLY;
import static catan.domain.model.game.types.GameUserActionCode.USE_CARD_ROAD_BUILDING;
import static catan.domain.model.game.types.GameUserActionCode.USE_CARD_YEAR_OF_PLENTY;

abstract class AbstractBot {

    @Autowired
    private GameDao gameDao;

    @Autowired
    PlayService playService;

    abstract String getBotName();

    abstract void processActionsInOrder(GameUserBean player, UserBean user, String gameId,
                                        ActionDetails moveRobberAction, ActionDetails choosePlayerToRobAction,
                                        ActionDetails kickOffResourcesAction, ActionDetails useCardKnightAction,
                                        ActionDetails useCardRoadBuildingAction, ActionDetails useCardYearOfPlentyAction,
                                        ActionDetails useCardMonopolyAction, ActionDetails throwDiceAction,
                                        ActionDetails buildCityAction, ActionDetails buildSettlementAction,
                                        ActionDetails buildRoadAction, ActionDetails buyCardAction,
                                        ActionDetails tradePortAction, ActionDetails tradeReplyAction,
                                        ActionDetails endTurnAction, boolean cardsAreOver) throws PlayException, GameException;



    private GameUserBean refreshPlayerFields(GameUserBean oldStatePlayer) {
        GameBean game = gameDao.getGameByGameId(oldStatePlayer.getGame().getGameId());
        GameUserBean refreshedPlayer = null;
        for (GameUserBean gameUserBean : game.getGameUsers()) {
            if (gameUserBean.getGameUserId().equals(oldStatePlayer.getGameUserId())) {
                refreshedPlayer = gameUserBean;
            }
        }
        assert refreshedPlayer != null;
        return refreshedPlayer;
    }

    void automatePlayersActions(GameUserBean oldStatePlayer, boolean cardsAreOver) throws PlayException, GameException {
        GameUserBean player = refreshPlayerFields(oldStatePlayer);

        AvailableActionsDetails availableActions = getAvailableActions(player);

        ActionDetails moveRobberAction = null;
        ActionDetails choosePlayerToRobAction = null;
        ActionDetails kickOffResourcesAction = null;
        ActionDetails useCardKnightAction = null;
        ActionDetails useCardRoadBuildingAction = null;
        ActionDetails useCardYearOfPlentyAction = null;
        ActionDetails useCardMonopolyAction = null;
        ActionDetails throwDiceAction = null;
        ActionDetails buildCityAction = null;
        ActionDetails buildSettlementAction = null;
        ActionDetails buildRoadAction = null;
        ActionDetails buyCardAction = null;
        ActionDetails tradePortAction = null;
        ActionDetails tradeReplyAction = null;
        ActionDetails endTurnAction = null;

        for (ActionDetails action : availableActions.getList()) {
            if (action.getCode().equals(MOVE_ROBBER.name())) {
                moveRobberAction = action;
            }
            if (action.getCode().equals(CHOOSE_PLAYER_TO_ROB.name())) {
                choosePlayerToRobAction = action;
            }
            if (action.getCode().equals(KICK_OFF_RESOURCES.name())) {
                kickOffResourcesAction = action;
            }
            if (action.getCode().equals(USE_CARD_KNIGHT.name())) {
                useCardKnightAction = action;
            }
            if (action.getCode().equals(USE_CARD_ROAD_BUILDING.name())) {
                useCardRoadBuildingAction = action;
            }
            if (action.getCode().equals(USE_CARD_YEAR_OF_PLENTY.name())) {
                useCardYearOfPlentyAction = action;
            }
            if (action.getCode().equals(USE_CARD_MONOPOLY.name())) {
                useCardMonopolyAction = action;
            }
            if (action.getCode().equals(THROW_DICE.name())) {
                throwDiceAction = action;
            }
            if (action.getCode().equals(TRADE_PORT.name())) {
                tradePortAction = action;
            }
            if (action.getCode().equals(BUILD_CITY.name()) && action.getParams().getNodeIds().size() > 0) {
                buildCityAction = action;
            }
            if (action.getCode().equals(BUILD_SETTLEMENT.name()) && action.getParams().getNodeIds().size() > 0) {
                buildSettlementAction = action;
            }
            if (action.getCode().equals(BUILD_ROAD.name()) && action.getParams().getEdgeIds().size() > 0) {
                buildRoadAction = action;
            }
            if (action.getCode().equals(BUY_CARD.name())) {
                buyCardAction = action;
            }
            if (action.getCode().equals(TRADE_REPLY.name())) {
                tradeReplyAction = action;
            }
            if (action.getCode().equals(END_TURN.name())) {
                endTurnAction = action;
            }
        }

        String gameId = String.valueOf(player.getGame().getGameId());
        UserBean user = player.getUser();

        processActionsInOrder(player, user, gameId,
                moveRobberAction,
                choosePlayerToRobAction,
                kickOffResourcesAction,
                useCardKnightAction,
                useCardRoadBuildingAction,
                useCardYearOfPlentyAction,
                useCardMonopolyAction,
                throwDiceAction,
                buildCityAction,
                buildSettlementAction,
                buildRoadAction,
                buyCardAction,
                tradePortAction,
                tradeReplyAction,
                endTurnAction,
                cardsAreOver);
    }

    private AvailableActionsDetails getAvailableActions(GameUserBean player) {
        String availableActionsJson = player.getAvailableActions();
        return new Gson().fromJson(availableActionsJson, AvailableActionsDetails.class);
    }
}
