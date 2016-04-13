package catan.services.impl;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.game.actions.ActionDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

@Service("smartBot")
public class SmartBot extends AbstractBot {
    static Map<Integer, Double> hexProbabilities = new HashMap<Integer, Double>();

    static {
        hexProbabilities.put(2, (1d / 36));
        hexProbabilities.put(3, (2d / 36));
        hexProbabilities.put(4, (3d / 36));
        hexProbabilities.put(5, (4d / 36));
        hexProbabilities.put(6, (5d / 36));
        hexProbabilities.put(7, (0d));
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
                               ActionDetails kickOffResourcesAction, ActionDetails useCardKnightAction,
                               ActionDetails useCardRoadBuildingAction, ActionDetails useCardYearOfPlentyAction,
                               ActionDetails useCardMonopolyAction, ActionDetails throwDiceAction,
                               ActionDetails buildCityAction, ActionDetails buildSettlementAction,
                               ActionDetails buildRoadAction, ActionDetails buyCardAction,
                               ActionDetails tradePortAction, ActionDetails tradeReplyAction,
                               ActionDetails endTurnAction, boolean cardsAreOver) throws PlayException, GameException {

        if (moveRobberAction != null) {
            moveRobber(player, user, gameId, moveRobberAction);
            return;
        }

        if (choosePlayerToRobAction != null) {
            choosePlayerToRob(player, user, gameId, choosePlayerToRobAction);
            return;
        }

        if (kickOffResourcesAction != null) {
            //TODO: kickoff not needed resources
            kickOffResources(player, user, gameId);
            return;
        }

        if (useCardKnightAction != null) {
            boolean cardUsed = useCardKnight(player, user, gameId);
            if (cardUsed) {
                return;
            }
        }

        if (useCardRoadBuildingAction != null) {
            //TODO: use useCardRoadBuilding if possible
            boolean cardUsed = useCardRoadBuilding(user, gameId);
            if (cardUsed) {
                return;
            }
        }

        if (useCardYearOfPlentyAction != null) {
            //TODO: use useCardYearOfPlenty if possible
            boolean cardUsed = useCardYearOfPlenty(user, gameId);
            if (cardUsed) {
                return;
            }
        }

        if (useCardMonopolyAction != null) {
            //TODO: use useCardMonopoly if possible
            boolean cardUsed = useCardMonopoly(user, gameId);
            if (cardUsed) {
                return;
            }
        }

        if (throwDiceAction != null) {
            //TODO: use knight if possible
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
            //TODO: calculate best road
            buildRoad(player, user, gameId, buildRoadAction);
            return;
        }

        if (buyCardAction != null && !cardsAreOver) {
            //TODO: Buy card if needed
            buyCard(user, gameId);
            return;
        }

        if (tradePortAction != null) {
            //TODO: trade required resources
            boolean tradeSuccessful = tradePort(player, gameId, user, tradePortAction);
            if (tradeSuccessful) {
                return;
            }
        }

        if (tradeReplyAction != null) {
            //TODO: Accept trade if it is useful for player
            tradeReply(user, gameId, tradeReplyAction);
            return;
        }


        if (endTurnAction != null) {
            endTurn(user, gameId);
            return;
        }

    }

    private boolean useCardMonopoly(UserBean user, String gameId) throws PlayException, GameException {
        try {
            playService.processAction(USE_CARD_MONOPOLY, user, gameId);
            return true;
        } catch (PlayException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean useCardYearOfPlenty(UserBean user, String gameId) throws GameException {
        try {
            playService.processAction(USE_CARD_YEAR_OF_PLENTY, user, gameId);
            return true;
        } catch (PlayException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean useCardRoadBuilding(UserBean user, String gameId) throws GameException {
        try {
            playService.processAction(USE_CARD_ROAD_BUILDING, user, gameId);
            return true;
        } catch (PlayException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean useCardKnight(GameUserBean player, UserBean user, String gameId) throws GameException {
        GameBean game = player.getGame();
        boolean shouldUseKnightCard = false;
        for (HexBean hex : game.getHexes()) {
            if (!hex.isRobbed()) {
                continue;
            }

            for (NodeBean node : hex.getNodes().listAllNotNullItems()) {
                if (node.hasBuildingBelongsToUser(player)) {
                    shouldUseKnightCard = true;
                }
            }

        }

        if (shouldUseKnightCard || player.getDevelopmentCards().getKnight() > 1) {
            try {
                playService.processAction(USE_CARD_KNIGHT, user, gameId);
                return true;
            } catch (PlayException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void buyCard(UserBean user, String gameId) throws GameException, PlayException {
        playService.processAction(BUY_CARD, user, gameId);
    }

    private void buildRoad(GameUserBean player, UserBean user, String gameId, ActionDetails buildRoadAction) throws PlayException, GameException {
        //TODO: don't build road if have place for settlement
        List<Integer> edgeIds = buildRoadAction.getParams().getEdgeIds();
        EdgeBean edgeToBuildRoad = calculateNextNeccesaryRoad(player, 3, edgeIds);

        String edgeId = edgeToBuildRoad != null
                ? edgeToBuildRoad.getAbsoluteId().toString()
                : edgeIds.get((int) (Math.random() * edgeIds.size())).toString();

        Map<String, String> params = new HashMap<String, String>();
        params.put("edgeId", edgeId);

        playService.processAction(BUILD_ROAD, user, gameId, params);
    }

    private static EdgeBean calculateNextNeccesaryRoad(GameUserBean player, int limitRoadLengthToNextBuilding, List<Integer> possibleEdgeIds) {
        Map<Double, LinkedList<EdgeBean>> nodeProbabilitiesOfRoadDestinations = new HashMap<Double, LinkedList<EdgeBean>>();

        List<NodeBean> possibleBeginRoadNodes = findNodesAvailableToStartBuildRoad(player, possibleEdgeIds);
        for (NodeBean possibleBeginRoadNode : possibleBeginRoadNodes) {
            for (NodeBean possiblePlaceForBuilding : player.getGame().getNodes()) {
                if (possiblePlaceForBuilding.getBuilding() != null || isNodeHasNeighbourBuilding(possiblePlaceForBuilding)) {
                    //current possiblePlaceForBuilding is not valid for building
                    continue;
                }

                if(possiblePlaceForBuilding.equals(possibleBeginRoadNode)){
                   continue;
                }

                List<LinkedList<EdgeBean>> ways = new ArrayList<LinkedList<EdgeBean>>();
                calculateRoadsFromNodeToNode(player, possibleBeginRoadNode, possiblePlaceForBuilding, ways, null);
                if(ways.size() == 0){
                   //not possible to build road of minimum length (10 hardcoded)
                   continue;
                }

                LinkedList<EdgeBean> minLengthWay = ways.get(0);//Default before cycle
                for (LinkedList<EdgeBean> currentLength : ways) {
                    if (minLengthWay.size() > currentLength.size()) {
                        minLengthWay = currentLength;
                    }
                }

                if(minLengthWay.size() <= limitRoadLengthToNextBuilding){
                    double sumProbabilityForPossiblePlaceForBuilding = calculateSumProbabilityForNode(possiblePlaceForBuilding);
                    //TODO: nodeProbabilitiesOfRoadDestinations.get(sumProbabilityForPossiblePlaceForBuilding) put only if minLengthWay less than existing value in map - need to make 'double' key have the same value
                    nodeProbabilitiesOfRoadDestinations.put(sumProbabilityForPossiblePlaceForBuilding, minLengthWay);
                }
            }
        }


        int minLength = 1000;
        Map<Double, LinkedList<EdgeBean>> nodeProbabilitiesOfMinLengthRoadDestinations = new HashMap<Double, LinkedList<EdgeBean>>();
        for (Map.Entry<Double, LinkedList<EdgeBean>> nodeProbabilityOfRoadDestination : nodeProbabilitiesOfRoadDestinations.entrySet()) {
            Double probability = nodeProbabilityOfRoadDestination.getKey();
            LinkedList<EdgeBean> pathToDestination = nodeProbabilityOfRoadDestination.getValue();

            if(pathToDestination.size() < minLength){
                minLength = pathToDestination.size();
                nodeProbabilitiesOfMinLengthRoadDestinations.clear();
                nodeProbabilitiesOfMinLengthRoadDestinations.put(probability, pathToDestination);
            } else if(pathToDestination.size() == minLength){
                nodeProbabilitiesOfMinLengthRoadDestinations.put(probability, pathToDestination);
            }
        }

        double maxProbability = 0;
        LinkedList<EdgeBean> bestWay = null;
        for (Double probability : nodeProbabilitiesOfMinLengthRoadDestinations.keySet()) {
            if(probability > maxProbability){
                maxProbability = probability;
                bestWay = nodeProbabilitiesOfMinLengthRoadDestinations.get(probability);
            }
        }


        //TODO: check best way calculation
        return bestWay == null || bestWay.size() == 0
                ? null
                : bestWay.get(0);
    }

    private static boolean isNodeHasNeighbourBuilding(NodeBean possiblePlaceForBuilding) {
        boolean nodeHasNeighbourBuilding = false;
        for (EdgeBean edgeOfPossiblePlaceForBuilding : possiblePlaceForBuilding.getEdges().listAllNotNullItems()) {
            for (NodeBean oppositeNode : edgeOfPossiblePlaceForBuilding.getNodes().listAllNotNullItems()) {
                if (!oppositeNode.equals(possiblePlaceForBuilding) && oppositeNode.getBuilding() != null) {
                    nodeHasNeighbourBuilding = true;
                    break;
                }
            }

            if (nodeHasNeighbourBuilding) {
                break;
            }
        }
        return nodeHasNeighbourBuilding;
    }

    private static List<NodeBean> findNodesAvailableToStartBuildRoad(GameUserBean player, List<Integer> possibleEdgeIds) {
        List<NodeBean> nodesAvailableToStartBuildRoad = new ArrayList<NodeBean>();
        for (NodeBean node : player.getGame().getNodes()) {
            if (!node.hasBuildingBelongsToUser(player) && !node.hasNeighbourRoadBelongsToGameUser(player)) {
                continue;
            }

            boolean hasEmptyEdgeToBuildRoad = false;
            for (EdgeBean edge : node.getEdges().listAllNotNullItems()) {
                if (edge.getBuilding() != null || !possibleEdgeIds.contains(edge.getAbsoluteId())) {
                    continue;
                }

                hasEmptyEdgeToBuildRoad = true;
            }

            if (hasEmptyEdgeToBuildRoad) {
                nodesAvailableToStartBuildRoad.add(node);
            }
        }

        return nodesAvailableToStartBuildRoad;
    }

    private static void calculateRoadsFromNodeToNode(GameUserBean player,
                                                     NodeBean sourceNode,
                                                     NodeBean destinationNode,
                                                     List<LinkedList<EdgeBean>> ways,
                                                     List<EdgeBean> path) {
        if (path == null) {
            path = new LinkedList<EdgeBean>();
        }

        if (sourceNode.equals(destinationNode)) {
            ways.add(new LinkedList<EdgeBean>(path));
            return;
        } else if (path.size() > 10) {
            return;
        }

        for (EdgeBean possibleRoadPlace : sourceNode.getEdges().listAllNotNullItems()) {
            if (path.contains(possibleRoadPlace) || possibleRoadPlace.getBuilding() != null) {
                continue;
            }

            NodeBean oppositeToSourceNode = getOppositeNode(possibleRoadPlace, sourceNode);

            path.add(possibleRoadPlace);
            calculateRoadsFromNodeToNode(player, oppositeToSourceNode, destinationNode, ways, path);
            path.remove(possibleRoadPlace);
        }
    }


    private static NodeBean getOppositeNode(EdgeBean edge, NodeBean currentNode) {
        for (NodeBean nodeBean : edge.getNodes().listAllNotNullItems()) {
            if (nodeBean.equals(currentNode)) {
                continue;
            }

            return nodeBean;
        }

        return null;
    }

    // add method canBuild that return how many roads required to build settlement
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

        //TODO: take resources in correct order, not just starting from brick
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
        List<Integer> availableNodeIds = choosePlayerToRobAction.getParams().getNodeIds();
        Set<GameUserBean> playersToRob = new HashSet<GameUserBean>();
        for (NodeBean node : player.getGame().getNodes()) {
            for (Integer availableNodeId : availableNodeIds) {
                if (availableNodeId.equals(node.getAbsoluteId())) {
                    playersToRob.add(node.getBuilding().getBuildingOwner());
                }
            }
        }

        GameUserBean playerToRob = choosePlayerToRob(player, playersToRob);
        String gameUserId = playerToRob.getGameUserId().toString();

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

        if (player.getResources().getStone() == 0) {
            buy.put("stone", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getWheat() == 0) {
            buy.put("wheat", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getSheep() == 0) {
            buy.put("sheep", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getWood() == 0) {
            buy.put("wood", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getBrick() == 0) {
            buy.put("brick", sumSell);
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


    private static GameUserBean choosePlayerToRob(GameUserBean player, Set<GameUserBean> gameUsers) {
        GameUserBean playerToRob = null;
        for (GameUserBean opponent : gameUsers) {
            if (opponent.getGameUserId().equals(player.getGameUserId())) {
                continue;
            }

            if (playerToRob == null
                    || playerToRob.getAchievements().getDisplayVictoryPoints() < opponent.getAchievements().getDisplayVictoryPoints()
                    || (playerToRob.getAchievements().getDisplayVictoryPoints() == opponent.getAchievements().getDisplayVictoryPoints()
                    && playerToRob.getAchievements().getTotalResources() * 3
                    + playerToRob.getAchievements().getLongestWayLength() * 2
                    + playerToRob.getAchievements().getTotalUsedKnights() * 2
                    + playerToRob.getAchievements().getTotalCards() < opponent.getAchievements().getTotalResources() * 3
                    + opponent.getAchievements().getLongestWayLength() * 2
                    + opponent.getAchievements().getTotalUsedKnights() * 2
                    + opponent.getAchievements().getTotalCards())) {
                playerToRob = opponent;
            }
        }
        return playerToRob;
    }

    private static int getHexIdWithMaxProbability(GameUserBean player, List<Integer> hexIdsToMoveRob) {
        int hexIdWithMaxProbability = 0;
        double maxProbability = 0;

        GameUserBean playerToRob = choosePlayerToRob(player, player.getGame().getGameUsers());

        for (HexBean hex : player.getGame().getHexes()) {
            double hexDiceResourceSumProbability = 0;

            if (hex.getDice() == null || hex.getDice() == 7) {
                continue;
            }

            boolean ignoreCurrentHex = true;
            for (NodeBean nodeAtHex : hex.getNodes().listAllNotNullItems()) {
                if (nodeAtHex.hasBuildingBelongsToUser(playerToRob)) {
                    ignoreCurrentHex = false;
                    int resourceQuantityMultiplier = nodeAtHex.getBuilding().getBuilt().equals(NodeBuiltType.SETTLEMENT) ? 1 : 2;
                    hexDiceResourceSumProbability += hexProbabilities.get(hex.getDice()) * resourceQuantityMultiplier;
                }
            }
            //TODO: ignore hex, if player has building close to this hex
            if (ignoreCurrentHex) {
                continue;
            }

            for (Integer hexId : hexIdsToMoveRob) {
                if (hexId.equals(hex.getAbsoluteId()) && maxProbability <= hexDiceResourceSumProbability) {
                    hexIdWithMaxProbability = hexId;
                    maxProbability = hexDiceResourceSumProbability;
                }
            }
        }

        return hexIdWithMaxProbability;
    }

    private static int getNodeIdWithNeighbourHexMaxProbability(GameUserBean player, List<Integer> nodeIdsToBuild) {
        Map<Integer, Double> sumNodeProbabilities = new HashMap<Integer, Double>();

        for (Integer nodeId : nodeIdsToBuild) {
            NodeBean nodeToCalculate = getNodeById(nodeId, player.getGame().getNodes());
            assert nodeToCalculate != null;

            double sumProbability = calculateSumProbabilityForNode(nodeToCalculate);
            sumNodeProbabilities.put(nodeId, sumProbability);
        }

        int nodeIdWithMaxProbability = -1;
        for (Integer currentNodeId : sumNodeProbabilities.keySet()) {
            if (nodeIdWithMaxProbability == -1 ||
                    sumNodeProbabilities.get(nodeIdWithMaxProbability) <= sumNodeProbabilities.get(currentNodeId)) {
                nodeIdWithMaxProbability = currentNodeId;
            }
        }

        return nodeIdWithMaxProbability;
    }

    private static double calculateSumProbabilityForNode(NodeBean nodeToCalculate) {
        double sumProbability = 0d;
        for (HexBean hexAtNode : nodeToCalculate.getHexes().listAllNotNullItems()) {
            if (hexAtNode.getDice() == null || hexAtNode.getDice() == 7) {
                continue;
            }

            sumProbability += hexProbabilities.get(hexAtNode.getDice());
        }

        return sumProbability;
    }

    private static NodeBean getNodeById(Integer nodeId, Set<NodeBean> nodes) {
        for (NodeBean node : nodes) {
            if (nodeId.equals(node.getAbsoluteId())) {
                return node;
            }
        }

        return null;
    }

}
