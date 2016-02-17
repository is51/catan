package catan.services.util.play;

import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.actions.ResourcesParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static catan.domain.model.game.types.GameStage.PREPARATION;
import static catan.domain.model.dashboard.types.NodeBuiltType.SETTLEMENT;

@Component
public class ActionParamsUtil {
    private Logger log = LoggerFactory.getLogger(ActionParamsUtil.class);

    public List<Integer> calculateBuildSettlementParams(GameBean game, GameUserBean gameUser) {

        if (game.getStage().equals(PREPARATION)) {
            return fetchNodeIdsToBuildOnInPreparationStage(game);
        }

        List<Integer> nodeIdsToBuildOn = new ArrayList<Integer>();
        for (NodeBean node : game.getNodes()) {
            if (node.getBuilding() == null
                    && node.noBuildingsOnNeighbourNodes()
                    && node.nearGameUsersNeighbourRoad(gameUser)) {
                nodeIdsToBuildOn.add(node.getId());
            }
        }
        return nodeIdsToBuildOn;
    }

    public List<Integer> calculateBuildCityParams(GameBean game, GameUserBean gameUser) {

        if (game.getStage().equals(PREPARATION)) {
            return fetchNodeIdsToBuildOnInPreparationStage(game);
        }

        List<Integer> nodeIdsToBuildOn = new ArrayList<Integer>();
        for (NodeBean node : game.getNodes()) {
            if (node.getBuilding() != null
                    && node.getBuilding().getBuildingOwner().equals(gameUser)
                    && node.getBuilding().getBuilt().equals(SETTLEMENT)) {
                nodeIdsToBuildOn.add(node.getId());
            }
        }
        return nodeIdsToBuildOn;
    }

    public List<Integer> calculateBuildRoadParams(GameBean game, GameUserBean gameUser) {
        List<Integer> edgeIdsToBuildOn = new ArrayList<Integer>();
        if (game.getStage().equals(PREPARATION)) {
            for (NodeBean node : game.getNodes()) {
                if (node.getBuilding() != null
                        && node.getBuilding().getBuildingOwner().equals(gameUser)
                        && !node.nearGameUsersNeighbourRoad(gameUser)) {
                    for (EdgeBean edge : node.getEdges().listAllNotNullItems()) {
                        edgeIdsToBuildOn.add(edge.getId());
                    }
                    break;
                }
            }
            return edgeIdsToBuildOn;
        }

        for (EdgeBean edge : game.fetchEdgesAccessibleForBuildingRoadInMainStage(gameUser)) {
            edgeIdsToBuildOn.add(edge.getId());
        }
        return edgeIdsToBuildOn;
    }

    private List<Integer> fetchNodeIdsToBuildOnInPreparationStage(GameBean game) {
        List<Integer> nodeIdsToBuildOn = new ArrayList<Integer>();
        for (NodeBean node : game.getNodes()) {
            if (node.getBuilding() == null && node.noBuildingsOnNeighbourNodes()) {
                nodeIdsToBuildOn.add(node.getId());
            }
        }
        return nodeIdsToBuildOn;
    }

    public ResourcesParams calculateResourcesParams(GameUserBean gameUser, GameBean game) {
        int brick = 4;
        int wood = 4;
        int sheep = 4;
        int wheat = 4;
        int stone = 4;

        for (NodePortType port : game.fetchPortsAvailableForGameUser(gameUser)) {
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
}
