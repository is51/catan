package catan.services.util.play;

import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.actions.ResourcesParams;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static catan.domain.model.game.types.GameStage.PREPARATION;
import static catan.domain.model.dashboard.types.NodeBuiltType.SETTLEMENT;

@Component
public class ActionParamsUtil {

    public List<Integer> calculateBuildSettlementParams(GameUserBean gameUser) {
        GameBean game = gameUser.getGame();
        if (game.getStage().equals(PREPARATION)) {
            return fetchNodeIdsToBuildOnInPreparationStage(game);
        }

        List<Integer> nodeIdsToBuildOn = new ArrayList<Integer>();
        for (NodeBean node : game.getNodes()) {
            if (node.couldBeUsedForBuildingSettlementByGameUserInMainStage(gameUser)) {
                nodeIdsToBuildOn.add(node.getId());
            }
        }

        return nodeIdsToBuildOn;
    }

    public List<Integer> calculateBuildCityParams(GameUserBean gameUser) {
        GameBean game = gameUser.getGame();
        if (game.getStage().equals(PREPARATION)) {
            return fetchNodeIdsToBuildOnInPreparationStage(game);
        }

        List<Integer> nodeIdsToBuildOn = new ArrayList<Integer>();
        for (NodeBean node : game.getNodes()) {
            if (node.hasBuildingBelongsToUser(gameUser)
                    && node.getBuilding().getBuilt().equals(SETTLEMENT)) {
                nodeIdsToBuildOn.add(node.getId());
            }
        }

        return nodeIdsToBuildOn;
    }

    public List<Integer> calculateBuildRoadParams(GameUserBean gameUser) {
        GameBean game = gameUser.getGame();
        if (game.getStage().equals(PREPARATION)) {
            return fetchEdgeIdsToBuildOnInPreparationStage(gameUser);
        }

        List<Integer> edgeIdsToBuildOn = new ArrayList<Integer>();
        for (EdgeBean edge : game.fetchEdgesAccessibleForBuildingRoadInMainStage(gameUser)) {
            edgeIdsToBuildOn.add(edge.getId());
        }

        return edgeIdsToBuildOn;
    }

    public List<Integer> calculateChoosePlayerToRobParams(GameUserBean gameUser) {
        List<Integer> nodeIdsToBuildOn = new ArrayList<Integer>();
        for (HexBean hex : gameUser.getGame().getHexes()) {
            if (!hex.isRobbed()) {
                continue;
            }
            for (NodeBean node : hex.getNodes().listAllNotNullItems()) {
                if (node.getBuilding() != null && !node.getBuilding().getBuildingOwner().equals(gameUser)) {
                    nodeIdsToBuildOn.add(node.getId());
                }
            }
            break;
        }

        return nodeIdsToBuildOn;
    }

    public List<Integer> calculateMoveRobberParams(GameUserBean gameUser) {
        List<Integer> hexIdsToMoveRobberTo = new ArrayList<Integer>();
        for (HexBean hex : gameUser.getGame().getHexes()) {
            if (hex.isRobbed() || hex.getResourceType() == HexType.EMPTY) {
                continue;
            }

            hexIdsToMoveRobberTo.add(hex.getId());
        }

        return hexIdsToMoveRobberTo;
    }

    public ResourcesParams calculateTradePortParams(GameUserBean gameUser) {
        int brick = 4;
        int wood = 4;
        int sheep = 4;
        int wheat = 4;
        int stone = 4;

        for (NodePortType port : gameUser.fetchAvailablePorts()) {
            switch (port) {
                case BRICK:
                    brick = 2;
                    break;
                case WOOD:
                    wood = 2;
                    break;
                case SHEEP:
                    sheep = 2;
                    break;
                case WHEAT:
                    wheat = 2;
                    break;
                case STONE:
                    stone = 2;
                    break;
                case ANY:
                    brick = brick == 4 ? 3 : brick;
                    wood = wood == 4 ? 3 : wood;
                    sheep = sheep == 4 ? 3 : sheep;
                    wheat = wheat == 4 ? 3 : wheat;
                    stone = stone == 4 ? 3 : stone;
                    break;
            }
        }

        return new ResourcesParams(brick, wood, sheep, wheat, stone);
    }

    private List<Integer> fetchNodeIdsToBuildOnInPreparationStage(GameBean game) {
        List<Integer> nodeIdsToBuildOn = new ArrayList<Integer>();
        for (NodeBean node : game.getNodes()) {
            if (node.getBuilding() == null && node.hasAllNeighbourNodesEmpty()) {
                nodeIdsToBuildOn.add(node.getId());
            }
        }

        return nodeIdsToBuildOn;
    }

    private List<Integer> fetchEdgeIdsToBuildOnInPreparationStage(GameUserBean gameUser) {
        List<Integer> edgeIdsToBuildOn = new ArrayList<Integer>();
        for (NodeBean node : gameUser.getGame().getNodes()) {
            if (node.hasBuildingBelongsToUser(gameUser)
                    && !node.hasNeighbourRoadBelongsToGameUser(gameUser)) {
                for (EdgeBean edge : node.getEdges().listAllNotNullItems()) {
                    edgeIdsToBuildOn.add(edge.getId());
                }
                break;
            }
        }

        return edgeIdsToBuildOn;
    }
}