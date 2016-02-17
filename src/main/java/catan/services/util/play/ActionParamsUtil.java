package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
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

    public List<Integer> calculateBuildSettlementParams(GameBean game, GameUserBean gameUser) throws GameException {

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

    public List<Integer> calculateBuildCityParams(GameBean game, GameUserBean gameUser) throws GameException {

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

    public List<Integer> calculateBuildRoadParams(GameBean game, GameUserBean gameUser) throws GameException {
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
}
