package catan.services.impl.bots.smart.utils;

import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameUserBean;
import catan.services.impl.bots.smart.SmartBot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SmartBotUtil {
    public static GameUserBean chooseOpponentPlayerWithBestCurrentResult(GameUserBean player, Set<GameUserBean> gameUsers) {
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

    public static NodeBean getNodeWithNeighbourHexMaxProbability(GameUserBean player, List<Integer> nodeIdsToBuild) {
        Map<NodeBean, Double> sumNodeProbabilities = new HashMap<NodeBean, Double>();

        for (Integer nodeId : nodeIdsToBuild) {
            NodeBean nodeToCalculate = getNodeById(nodeId, player.getGame().getNodes());
            assert nodeToCalculate != null;

            double sumProbability = calculateSumProbabilityForNode(nodeToCalculate);
            sumNodeProbabilities.put(nodeToCalculate, sumProbability);
        }

        NodeBean nodeWithMaxProbability = null;
        for (NodeBean currentNode : sumNodeProbabilities.keySet()) {
            if (nodeWithMaxProbability == null ||
                sumNodeProbabilities.get(nodeWithMaxProbability) <= sumNodeProbabilities.get(currentNode)) {
                nodeWithMaxProbability = currentNode;
            }
        }

        return nodeWithMaxProbability;
    }

    static double calculateSumProbabilityForNode(NodeBean nodeToCalculate) {
        double sumProbability = 0d;
        for (HexBean hexAtNode : nodeToCalculate.getHexes().listAllNotNullItems()) {
            if (hexAtNode.getDice() == null || hexAtNode.getDice() == 7) {
                continue;
            }
            sumProbability += SmartBot.hexProbabilities.get(hexAtNode.getDice());

            if(nodeToCalculate.getPort() == null || nodeToCalculate.getPort().equals(NodePortType.NONE)){
                sumProbability += 0.001;
            } else if(nodeToCalculate.getPort().equals(NodePortType.ANY)){
                sumProbability += 0.009;
            } else {
                sumProbability += 0.005;
            }
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
