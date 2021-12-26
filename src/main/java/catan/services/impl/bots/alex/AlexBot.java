package catan.services.impl.bots.alex;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.game.actions.ActionDetails;
import catan.services.impl.bots.AbstractBot;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static catan.domain.model.game.types.GameUserActionCode.*;

@Service("alexBot")
public class AlexBot extends AbstractBot {

    @Override
    public String getBotName() {
        return "ALEX_BOT";
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
                                      ActionDetails endTurnAction, boolean isMandatory, boolean cardsAreOver) throws PlayException, GameException {

        GameBean game = player.getGame();

        // PREPARATION

        if (game.getStage() == GameStage.PREPARATION) {

            if (buildSettlementAction != null) {
                buildSettlementInPlaceWithMaxProbability(player, user, gameId, buildSettlementAction);
                processActionEndTurn(user, gameId);
                return;
            }

            /*if (buildCityAction != null) {
                buildCity(player, user, gameId, buildCityAction);
                return;
            }

            if (buildRoadAction != null) {
                buildRoad(user, gameId, buildRoadAction);
                return;
            }

            if (endTurnAction != null) {
                endTurn(user, gameId);
                return;
            }*/

            return;
        }


        // MAIN

        /*
        if (moveRobberAction != null) {
            moveRobber(player, user, gameId, moveRobberAction);
            return;
        }

        if (choosePlayerToRobAction != null) {
            choosePlayerToRob(player, user, gameId, choosePlayerToRobAction);
            return;
        }

        if (kickOffResourcesAction != null) {
            kickOffResources(player, user, gameId);
            return;
        }











        if (buildCityAction != null) {
            buildCity(player, user, gameId, buildCityAction);
            return;
        }

        if (buildSettlementAction != null) {
            buildSettlement(player, user, gameId, buildSettlementAction);
            return;
        }

        if (buildRoadAction != null) {
            buildRoad(user, gameId, buildRoadAction);
            return;
        }

        if (buyCardAction != null) {
            buyCard(user, gameId);
            return;
        }

        if (tradePortAction != null) {
            boolean tradeSuccessful = tradePort(player, gameId, user, tradePortAction);
            if (tradeSuccessful) {
                return;
            }
        }

        if (tradeReplyAction != null) {
            tradeReply(user, gameId, tradeReplyAction);
            return;
        }






        if (throwDiceAction != null) {
            throwDice(user, gameId);
            return;
        }

        if (endTurnAction != null) {
            endTurn(user, gameId);
            return;
        }*/

    }





    private void buildSettlementInPlaceWithMaxProbability(GameUserBean player, UserBean user, String gameId, ActionDetails buildSettlementAction) throws PlayException, GameException {
        List<Integer> nodeIdsToBuildSettlement = buildSettlementAction.getParams().getNodeIds();
        int nodeIdWithNeighbourHexMaxProbability = getNodeIdWithNeighbourHexMaxProbability(player, nodeIdsToBuildSettlement);
        processActionBuildSettlement(nodeIdWithNeighbourHexMaxProbability, user, gameId);
    }



    private void processActionBuildSettlement(int nodeId, UserBean user, String gameId) throws PlayException, GameException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", String.valueOf(nodeId));

        GameUserActionCode buildSettlementActionCode = BUILD_SETTLEMENT;
        playService.processAction(buildSettlementActionCode, user, gameId, params);
    }

    private void processActionEndTurn(UserBean user, String gameId) throws PlayException, GameException {
        playService.processAction(END_TURN, user, gameId);
    }










    private static int getNodeIdWithNeighbourHexMaxProbability(GameUserBean player, List<Integer> nodeIdsToBuild) {
        Map<Integer, Double> sumNodeProbabilities = new HashMap<Integer, Double>();
        for (Integer nodeId : nodeIdsToBuild) {
            double sumProbability = 0d;
            for (NodeBean node : player.getGame().getNodes()) {
                if (nodeId.equals(node.getId())) {
                    for (HexBean hexAtNode : node.getHexes().listAllNotNullItems()) {
                        sumProbability += hexProbabilities.get(hexAtNode.getDice());
                    }

                }
            }

            sumNodeProbabilities.put(nodeId, sumProbability);
        }

        int nodeIdWithMaxProbability = 0;
        for (Integer currentNodeId : sumNodeProbabilities.keySet()) {
            if (sumNodeProbabilities.get(nodeIdWithMaxProbability) <= sumNodeProbabilities.get(currentNodeId)) {
                nodeIdWithMaxProbability = currentNodeId;
            }
        }
        return nodeIdWithMaxProbability;
    }

}