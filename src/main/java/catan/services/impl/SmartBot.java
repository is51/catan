package catan.services.impl;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.game.actions.ActionDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@Service("smartBot")
public class SmartBot extends AbstractBot {
    static Map<Integer, Double> hexProbabilities = new HashMap<Integer, Double>();

    static {
        hexProbabilities.put(2, (1d / 36));
        hexProbabilities.put(3, (2d / 36));
        hexProbabilities.put(4, (3d / 36));
        hexProbabilities.put(5, (4d / 36));
        hexProbabilities.put(6, (5d / 36));
        hexProbabilities.put(7, (6d / 36));
        hexProbabilities.put(8, (5d / 36));
        hexProbabilities.put(9, (4d / 36));
        hexProbabilities.put(10, (3d / 36));
        hexProbabilities.put(11, (2d / 36));
        hexProbabilities.put(12, (1d / 36));
    }

    @Override
    String getBotName() {
        return "SMART_BOT";
    }

    @Override
    void processActionsInOrder(GameUserBean player, UserBean user, String gameId,
                               ActionDetails moveRobberAction, ActionDetails choosePlayerToRobAction,
                               ActionDetails kickOffResourcesAction, ActionDetails throwDiceAction,
                               ActionDetails buildCityAction, ActionDetails buildSettlementAction,
                               ActionDetails buildRoadAction, ActionDetails buyCardAction,
                               ActionDetails tradePortAction, ActionDetails tradeReplyAction,
                               ActionDetails endTurnAction) throws PlayException, GameException {

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

        if (throwDiceAction != null) {
            throwDice(user, gameId);
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


        if (endTurnAction != null) {
            endTurn(user, gameId);
            return;
        }

    }

    private void buyCard(UserBean user, String gameId) throws PlayException, GameException {
        playService.processAction(BUY_CARD, user, gameId);
    }

    private void buildRoad(UserBean user, String gameId, ActionDetails buildRoadAction) throws PlayException, GameException {
        Map<String, String> params = new HashMap<String, String>();
        List<Integer> edgeIds = buildRoadAction.getParams().getEdgeIds();
        params.put("edgeId", edgeIds.get((int) (Math.random() * edgeIds.size())).toString());

        playService.processAction(BUILD_ROAD, user, gameId, params);
    }

    private void buildSettlement(GameUserBean player, UserBean user, String gameId, ActionDetails buildSettlementAction) throws PlayException, GameException {
        List<Integer> nodeIdsToBuildSettlement = buildSettlementAction.getParams().getNodeIds();
        int nodeIdWithNeighbourHexMaxProbability = getNodeIdWithNeighbourHexMaxProbability(player, nodeIdsToBuildSettlement);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", String.valueOf(nodeIdWithNeighbourHexMaxProbability));

        GameUserActionCode buildSettlementActionCode = BUILD_SETTLEMENT;
        playService.processAction(buildSettlementActionCode, user, gameId, params);
    }

    private void buildCity(GameUserBean player, UserBean user, String gameId, ActionDetails buildCityAction) throws PlayException, GameException {
        List<Integer> nodeIdsToBuildCity = buildCityAction.getParams().getNodeIds();
        int nodeIdWithNeighbourHexMaxProbability = getNodeIdWithNeighbourHexMaxProbability(player, nodeIdsToBuildCity);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", String.valueOf(nodeIdWithNeighbourHexMaxProbability));

        playService.processAction(BUILD_CITY, user, gameId, params);
    }

    private void moveRobber(GameUserBean player, UserBean user, String gameId, ActionDetails moveRobberAction) throws PlayException, GameException {
        List<Integer> hexIdsToMoveRob = moveRobberAction.getParams().getHexIds();
        int hexIdWithMaxProbability = getHexIdWithMaxProbability(player, hexIdsToMoveRob);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", String.valueOf(hexIdWithMaxProbability));

        playService.processAction(MOVE_ROBBER, user, gameId, params);
    }

    private void kickOffResources(GameUserBean player, UserBean user, String gameId) throws PlayException, GameException {
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
    }

    private void choosePlayerToRob(GameUserBean player, UserBean user, String gameId, ActionDetails choosePlayerToRobAction) throws PlayException, GameException {
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
    }

    private void throwDice(UserBean user, String gameId) throws PlayException, GameException {
        playService.processAction(THROW_DICE, user, gameId);
    }

    private void endTurn(UserBean user, String gameId) throws PlayException, GameException {
        playService.processAction(END_TURN, user, gameId);
    }

    private void tradeReply(UserBean user, String gameId, ActionDetails tradeReplyAction) throws PlayException, GameException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("tradeReply", "decline");
        params.put("offerId", tradeReplyAction.getParams().getOfferId().toString());

        playService.processAction(TRADE_REPLY, user, gameId, params);
    }

    private boolean tradePort(GameUserBean player, String gameId, UserBean user, ActionDetails action) throws PlayException, GameException {
        Map<String, Integer> sell = new HashMap<String, Integer>();
        Map<String, Integer> buy = new HashMap<String, Integer>();
        Integer sumSell = 0;
        Integer sumBuy = 0;

        if (player.getResources().getBrick() >= action.getParams().getBrick()) {
            int numberOrResourcesCanBuy = player.getResources().getBrick() / action.getParams().getBrick();
            int buyBricks = action.getParams().getBrick() * numberOrResourcesCanBuy;
            sell.put("brick", buyBricks);
            sumSell += numberOrResourcesCanBuy;
        }
        if (player.getResources().getWood() >= action.getParams().getWood()) {
            int numberOrResourcesCanBuy = player.getResources().getWood() / action.getParams().getWood();
            int buyWood = action.getParams().getWood() * numberOrResourcesCanBuy;
            sell.put("wood", buyWood);
            sumSell += numberOrResourcesCanBuy;
        }
        if (player.getResources().getSheep() >= action.getParams().getSheep()) {
            int numberOrResourcesCanBuy = player.getResources().getSheep() / action.getParams().getSheep();
            int buySheep = action.getParams().getSheep() * numberOrResourcesCanBuy;
            sell.put("sheep", buySheep);
            sumSell += numberOrResourcesCanBuy;

        }
        if (player.getResources().getWheat() >= action.getParams().getWheat()) {
            int numberOrResourcesCanBuy = player.getResources().getWheat() / action.getParams().getWheat();
            int buyWheat = action.getParams().getWheat() * numberOrResourcesCanBuy;
            sell.put("wheat", buyWheat);
            sumSell += numberOrResourcesCanBuy;
        }
        if (player.getResources().getStone() >= action.getParams().getStone()) {
            int numberOrResourcesCanBuy = player.getResources().getStone() / action.getParams().getStone();
            int buyStone = action.getParams().getStone() * numberOrResourcesCanBuy;
            sell.put("stone", buyStone);
            sumSell += numberOrResourcesCanBuy;
        }

        if (sumSell == 0) {
            return false;
        }

        if (player.getResources().getBrick() == 0) {
            buy.put("brick", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getWood() == 0) {
            buy.put("wood", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getSheep() == 0) {
            buy.put("sheep", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getWheat() == 0) {
            buy.put("wheat", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getStone() == 0) {
            buy.put("stone", sumSell);
            sumBuy += sumSell;
        }

        if (!sumBuy.equals(sumSell)) {
            if (!sell.keySet().contains("brick")) {
                buy.put("brick", sumSell);
                sumBuy += sumSell;
            } else if (!sell.keySet().contains("wood")) {
                buy.put("wood", sumSell);
                sumBuy += sumSell;
            } else if (!sell.keySet().contains("sheep")) {
                buy.put("sheep", sumSell);
                sumBuy += sumSell;
            } else if (!sell.keySet().contains("wheat")) {
                buy.put("wheat", sumSell);
                sumBuy += sumSell;
            } else if (!sell.keySet().contains("stone")) {
                buy.put("stone", sumSell);
                sumBuy += sumSell;
            }
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", buy.get("brick") != null ? buy.get("brick").toString() : (sell.get("brick") != null ? "-" + sell.get("brick") : "0"));
        params.put("wood", buy.get("wood") != null ? buy.get("wood").toString() : (sell.get("wood") != null ? "-" + sell.get("wood") : "0"));
        params.put("sheep", buy.get("sheep") != null ? buy.get("sheep").toString() : (sell.get("sheep") != null ? "-" + sell.get("sheep") : "0"));
        params.put("wheat", buy.get("wheat") != null ? buy.get("wheat").toString() : (sell.get("wheat") != null ? "-" + sell.get("wheat") : "0"));
        params.put("stone", buy.get("stone") != null ? buy.get("stone").toString() : (sell.get("stone") != null ? "-" + sell.get("stone") : "0"));

        playService.processAction(TRADE_PORT, user, gameId, params);
        return true;
    }


    private static int getHexIdWithMaxProbability(GameUserBean player, List<Integer> hexIdsToMoveRob) {
        int hexIdWithMaxProbability = 0;
        double maxProbability = 0;

        for (HexBean hex : player.getGame().getHexes()) {
            double hexDiceProbability = hexProbabilities.get(hex.getDice());
            for (Integer hexId : hexIdsToMoveRob) {
                if(hexId.equals(hex.getId()) && maxProbability <= hexDiceProbability){
                    hexIdWithMaxProbability = hexId;
                    maxProbability = hexDiceProbability;
                }
            }
        }

        return hexIdWithMaxProbability;
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
