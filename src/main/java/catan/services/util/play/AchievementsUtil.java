package catan.services.util.play;

import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.Achievements;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AchievementsUtil {
    private Logger log = LoggerFactory.getLogger(AchievementsUtil.class);

    public void updateLongestWayLength(GameBean game, GameUserBean gameUser) {
        int maxWayLength = 0;
        for (EdgeBean edge : game.fetchEdgesWithBuildingsBelongsToGameUser(gameUser)) {
            maxWayLength = calculateMaxWayLength(gameUser, maxWayLength, new ArrayList<Integer>(), new ArrayList<Integer>(), edge, 0);
        }
        gameUser.getAchievements().setLongestWayLength(maxWayLength);
        updateLongestWayOwner(game);
    }

    public void updateLongestWayLengthInCaseWayWasInterrupted(GameBean game, GameUserBean gameUser, NodeBean nodeToBuildOn) {
        GameUserBean gameUserToUpdateLongestWayLength = fetchGameUserWhoseWayWasInterrupted(nodeToBuildOn, gameUser);
        if (gameUserToUpdateLongestWayLength != null) {
            updateLongestWayLength(game, gameUserToUpdateLongestWayLength);
        }
    }

    private GameUserBean fetchGameUserWhoseWayWasInterrupted(NodeBean node, GameUserBean gameUserWhoBuiltOnNode) {
        GameUserBean gameUserWhoseWayCouldBeInterrupted = null;
        for (EdgeBean edge : node.getEdges().listAllNotNullItems()) {
            if (edge.getBuilding() == null || edge.getBuilding().getBuildingOwner().equals(gameUserWhoBuiltOnNode)) {
                continue;
            }
            GameUserBean buildingOwner = edge.getBuilding().getBuildingOwner();
            if (buildingOwner == gameUserWhoseWayCouldBeInterrupted) {
                return gameUserWhoseWayCouldBeInterrupted;
            }
            gameUserWhoseWayCouldBeInterrupted = buildingOwner;
        }
        return null;
    }

    private int calculateMaxWayLength(GameUserBean gameUser, int lastMaxWayLength, List<Integer> checkedEdgeIds, List<Integer> checkedNodeIds, EdgeBean edge, int currMaxWayLength) {
        int edgeId = edge.getId();
        if (checkedEdgeIds.contains(edgeId) || edgeDoesNotContainGameUsersRoad(gameUser, edge)) {
            return lastMaxWayLength;
        }

        checkedEdgeIds.add(edgeId);
        currMaxWayLength++;
        for (NodeBean node : edge.getNodes().listAllNotNullItems()) {
            int nodeId = node.getId();
            if (checkedNodeIds.contains(nodeId) || nodeContainsOpponentsBuilding(gameUser, node)) {
                continue;
            }

            checkedNodeIds.add(nodeId);
            for (EdgeBean nextEdge : node.getEdges().listAllNotNullItems()) {
                lastMaxWayLength = calculateMaxWayLength(gameUser, lastMaxWayLength, checkedEdgeIds, checkedNodeIds, nextEdge, currMaxWayLength);
            }
            checkedNodeIds.remove(new Integer(nodeId));
        }
        checkedEdgeIds.remove(new Integer(edgeId));

        return currMaxWayLength > lastMaxWayLength
                ? currMaxWayLength
                : lastMaxWayLength;
    }

    private boolean nodeContainsOpponentsBuilding(GameUserBean gameUser, NodeBean node) {
        return node.getBuilding() != null && !node.getBuilding().getBuildingOwner().equals(gameUser);
    }

    private boolean edgeDoesNotContainGameUsersRoad(GameUserBean gameUser, EdgeBean edge) {
        return edge.getBuilding() == null || !edge.getBuilding().getBuildingOwner().equals(gameUser);
    }

    private void updateLongestWayOwner(GameBean game) {
        int maxLongestWayLength = 0;
        GameUserBean newLongestWayOwner = null;
        GameUserBean currentLongestWayOwner = game.getLongestWayOwner();
        for (GameUserBean gameUser : game.getGameUsers()) {
            int longestWayLength = gameUser.getAchievements().getLongestWayLength();
            if (longestWayLength < 5) {
                continue;
            }

            if (longestWayLength > maxLongestWayLength) {
                maxLongestWayLength = longestWayLength;
                newLongestWayOwner = gameUser;
                continue;
            }

            if (longestWayLength == maxLongestWayLength && (newLongestWayOwner == null || !newLongestWayOwner.equals(currentLongestWayOwner))) {
                newLongestWayOwner = !gameUser.equals(currentLongestWayOwner)
                        ? null
                        : gameUser;
            }
        }

        game.setLongestWayOwner(newLongestWayOwner);
    }

    public void updateBiggestArmyOwner(GameUserBean gameUser, GameBean game) {
        int totalUsedKnights = gameUser.getAchievements().getTotalUsedKnights();
        if (totalUsedKnights >= 3 && gameUsersUsedKnightsIsTheBiggestArmy(totalUsedKnights, game)) {
            game.setBiggestArmyOwner(gameUser);
        }
    }

    public void increaseTotalUsedKnightsByOne(GameUserBean gameUser) {
        Achievements achievements = gameUser.getAchievements();
        int totalUsedKnightsNew = achievements.getTotalUsedKnights() + 1;
        achievements.setTotalUsedKnights(totalUsedKnightsNew);
    }

    private boolean gameUsersUsedKnightsIsTheBiggestArmy(int totalUsedKnights, GameBean game) {
        GameUserBean currentBiggestArmyOwner = game.getBiggestArmyOwner();
        return currentBiggestArmyOwner == null || currentBiggestArmyOwner.getAchievements().getTotalUsedKnights() < totalUsedKnights;
    }
}
