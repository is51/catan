package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.LogCodeType;
import catan.domain.model.game.Achievements;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AchievementsUtil {

    public void updateAchievements(GameBean game) throws GameException {
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (game.getCurrentMove().equals(gameUser.getMoveOrder())) {
                updateTotalDevCards(gameUser);
            }
            updateVictoryPoints(gameUser, game);
            updateTotalResources(gameUser);
        }
    }

    private void updateTotalDevCards(GameUserBean gameUser) {
        int totalCards = gameUser.getDevelopmentCards().calculateSum();
        gameUser.getAchievements().setTotalCards(totalCards);
    }

    private void updateTotalResources(GameUserBean gameUser) {
        int totalResources = gameUser.getResources().calculateSum();
        gameUser.getAchievements().setTotalResources(totalResources);
    }

    private void updateVictoryPoints(GameUserBean gameUser, GameBean game) throws GameException {

        int settlementsCount = gameUser.getBuildingsCount().getSettlements();
        int citiesCount = gameUser.getBuildingsCount().getCities();
        boolean isBiggestArmyOwner = gameUser.equals(game.getBiggestArmyOwner());
        boolean isLongestWayOwner = gameUser.equals(game.getLongestWayOwner());

        int displayVictoryPoints = settlementsCount + citiesCount * 2 + (isBiggestArmyOwner ? 2 : 0) + (isLongestWayOwner ? 2 : 0);
        gameUser.getAchievements().setDisplayVictoryPoints(displayVictoryPoints);
    }

    public void updateLongestWayLengthIfInterrupted(NodeBean nodeWithBuilding) {
        if (!GameStage.MAIN.equals(nodeWithBuilding.getGame().getStage())) {
            return;
        }
        GameUserBean gameUserToUpdateLongestWayLength = fetchGameUserWhoseWayWasInterrupted(nodeWithBuilding);
        if (gameUserToUpdateLongestWayLength != null) {
            updateLongestWayLength(gameUserToUpdateLongestWayLength);
        }
    }

    public void updateLongestWayLength(GameUserBean gameUser) {
        int maxWayLength = 0;
        for (EdgeBean edge : gameUser.fetchEdgesWithBuildingsBelongsToGameUser()) {
            maxWayLength = calculateMaxWayLength(gameUser, maxWayLength, new ArrayList<Integer>(), new ArrayList<Integer>(), edge, 0);
        }
        gameUser.getAchievements().setLongestWayLength(maxWayLength);
        updateLongestWayOwner(gameUser.getGame());
    }

    private GameUserBean fetchGameUserWhoseWayWasInterrupted(NodeBean node) {
        GameUserBean gameUserWhoseWayCouldBeInterrupted = null;
        GameUserBean gameUserWhoBuiltOnNode = node.getGame().fetchActiveGameUser();
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

    private int calculateMaxWayLength(GameUserBean gameUser, int lastMaxWayLength, List<Integer> checkedEdgeAbsoluteIds, List<Integer> checkedNodeAbsoluteIds, EdgeBean edge, int currMaxWayLength) {
        int edgeAbsoluteId = edge.getAbsoluteId();
        if (checkedEdgeAbsoluteIds.contains(edgeAbsoluteId) || edgeDoesNotContainGameUsersRoad(gameUser, edge)) {
            return lastMaxWayLength;
        }

        checkedEdgeAbsoluteIds.add(edgeAbsoluteId);
        currMaxWayLength++;
        for (NodeBean node : edge.getNodes().listAllNotNullItems()) {
            int nodeAbsoluteId = node.getAbsoluteId();
            if (checkedNodeAbsoluteIds.contains(nodeAbsoluteId) || nodeContainsOpponentsBuilding(gameUser, node)) {
                continue;
            }

            checkedNodeAbsoluteIds.add(nodeAbsoluteId);
            for (EdgeBean nextEdge : node.getEdges().listAllNotNullItems()) {
                lastMaxWayLength = calculateMaxWayLength(gameUser, lastMaxWayLength, checkedEdgeAbsoluteIds, checkedNodeAbsoluteIds, nextEdge, currMaxWayLength);
            }
            checkedNodeAbsoluteIds.remove(new Integer(nodeAbsoluteId));
        }
        checkedEdgeAbsoluteIds.remove(new Integer(edgeAbsoluteId));

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

        if (newLongestWayOwner == null && currentLongestWayOwner != null) {
            game.setLongestWayOwner(null);
            MessagesUtil.addLogMsgForGameUsers(LogCodeType.INTERRUPTED_WIDEST_NETWORK, currentLongestWayOwner);
            return;
        }
        if (newLongestWayOwner != null && !newLongestWayOwner.equals(currentLongestWayOwner)) {
            newLongestWayOwner.assignLongestWayOwner();
            MessagesUtil.addLogMsgForGameUsers(LogCodeType.NEW_WIDEST_NETWORK, newLongestWayOwner);
        }
    }

    public void updateBiggestArmyOwner(GameUserBean gameUser) {
        if (gameUserGotTheBiggestArmy(gameUser)) {
            gameUser.assignBiggestArmyOwner();
            MessagesUtil.addLogMsgForGameUsers(LogCodeType.NEW_SECURITY_LEADER, gameUser);
        }
    }

    public void increaseTotalUsedKnightsByOne(GameUserBean gameUser) {
        Achievements achievements = gameUser.getAchievements();
        int totalUsedKnightsNew = achievements.getTotalUsedKnights() + 1;
        achievements.setTotalUsedKnights(totalUsedKnightsNew);
    }

    private boolean gameUserGotTheBiggestArmy(GameUserBean gameUser) {
        int totalUsedKnights = gameUser.getAchievements().getTotalUsedKnights();
        GameUserBean currentBiggestArmyOwner = gameUser.getGame().getBiggestArmyOwner();
        if (totalUsedKnights < 3 || gameUser.equals(currentBiggestArmyOwner)) {
            return false;
        }

        return currentBiggestArmyOwner == null || currentBiggestArmyOwner.getAchievements().getTotalUsedKnights() < totalUsedKnights;
    }
}
