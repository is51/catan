package catan.services.impl.bots.stupid;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.game.actions.ActionDetails;
import catan.services.impl.bots.AbstractBot;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static catan.domain.model.game.types.GameUserActionCode.CHOOSE_PLAYER_TO_ROB;
import static catan.domain.model.game.types.GameUserActionCode.END_TURN;
import static catan.domain.model.game.types.GameUserActionCode.KICK_OFF_RESOURCES;
import static catan.domain.model.game.types.GameUserActionCode.MOVE_ROBBER;
import static catan.domain.model.game.types.GameUserActionCode.THROW_DICE;
import static catan.domain.model.game.types.GameUserActionCode.TRADE_REPLY;

@Service("stupidBot")
public class StupidBot extends AbstractBot {


    @Override
    public String getBotName() {
        return "STUPID_BOT";
    }

    @Override
    public void processActionsInOrder(GameUserBean player, UserBean user, String gameId,
                                      ActionDetails moveRobberAction, ActionDetails choosePlayerToRobAction,
                                      ActionDetails kickOffResourcesAction, ActionDetails useCardKnightAction,
                                      ActionDetails useCardRoadBuildingAction, ActionDetails useCardYearOfPlentyAction,
                                      ActionDetails useCardMonopolyAction, ActionDetails throwDiceAction,
                                      ActionDetails buildCityAction, ActionDetails buildSettlementAction,
                                      ActionDetails buildRoadAction, ActionDetails buyCardAction,
                                      ActionDetails tradePortAction, ActionDetails tradeReplyAction,
                                      ActionDetails endTurnAction, boolean cardsAreOver) throws PlayException, GameException {
        if (endTurnAction != null) {
            playService.processAction(END_TURN, user, gameId);
            return;
        }

        if (throwDiceAction != null) {
            playService.processAction(THROW_DICE, user, gameId);
            return;
        }

        if (tradeReplyAction != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("tradeReply", "decline");
            params.put("offerId", tradeReplyAction.getParams().getOfferId().toString());

            playService.processAction(TRADE_REPLY, user, gameId, params);
            return;
        }

        if (moveRobberAction != null) {
            Map<String, String> params = new HashMap<String, String>();
            List<Integer> hexIds = moveRobberAction.getParams().getHexIds();
            params.put("hexId", hexIds.get((int) (Math.random() * hexIds.size())).toString());

            playService.processAction(MOVE_ROBBER, user, gameId, params);
            return;
        }

        if (choosePlayerToRobAction != null) {
            String gameUserId = "";
            List<Integer> nodeIds = choosePlayerToRobAction.getParams().getNodeIds();
            Integer nodeAbsoluteIdToRobPlayer = nodeIds.get((int) (Math.random() * nodeIds.size()));
            for (NodeBean nodeBean : player.getGame().getNodes()) {
                if (nodeAbsoluteIdToRobPlayer.equals(nodeBean.getAbsoluteId())) {
                    gameUserId = nodeBean.getBuilding().getBuildingOwner().getGameUserId().toString();
                    break;
                }
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("gameUserId", gameUserId);

            playService.processAction(CHOOSE_PLAYER_TO_ROB, user, gameId, params);
            return;
        }

        if (kickOffResourcesAction != null) {
            Integer sum = player.getResources().getBrick() +
                    player.getResources().getWood() +
                    player.getResources().getSheep() +
                    player.getResources().getWheat() +
                    player.getResources().getStone();
            Integer halfSum = sum / 2;
            //brick=0, wood=2, sheep=1, wheat=4, stone=1}
            Integer brick = player.getResources().getBrick() >= halfSum
                    ? halfSum
                    : player.getResources().getBrick();
            Integer wood = player.getResources().getWood() + brick >= halfSum
                    ? halfSum - brick
                    : player.getResources().getWood();
            Integer sheep = player.getResources().getSheep() + brick + wood >= halfSum
                    ? halfSum - brick - wood
                    : player.getResources().getSheep();
            Integer wheat = player.getResources().getWheat() + brick + wood + sheep >= halfSum
                    ? halfSum - brick - wood - sheep
                    : player.getResources().getWheat();
            Integer stone = player.getResources().getStone() + brick + wood + sheep + wheat >= halfSum
                    ? halfSum - brick - wood - sheep - wheat
                    : player.getResources().getStone();

            Map<String, String> params = new HashMap<String, String>();
            params.put("brick", brick.toString());
            params.put("wood", wood.toString());
            params.put("sheep", sheep.toString());
            params.put("wheat", wheat.toString());
            params.put("stone", stone.toString());

            playService.processAction(KICK_OFF_RESOURCES, user, gameId, params);
            return;
        }

    }
}
