package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static catan.services.impl.GameServiceImpl.ERROR_CODE_ERROR;

@Component
public class ActionParamsUtil {
    private Logger log = LoggerFactory.getLogger(ActionParamsUtil.class);

    public List<Integer> calculateBuildSettlementParams(GameBean game, GameUserBean gameUser) throws GameException {

        if (game.getStage().equals(GameStage.PREPARATION)) {
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

        if (game.getStage().equals(GameStage.PREPARATION)) {
            return fetchNodeIdsToBuildOnInPreparationStage(game);
        }

        List<Integer> nodeIdsToBuildOn = new ArrayList<Integer>();
        for (NodeBean node : game.getNodes()) {
            if (node.getBuilding() != null
                    && node.getBuilding().getBuildingOwner().equals(gameUser)
                    && node.getBuilding().getBuilt().equals(NodeBuiltType.SETTLEMENT)) {
                nodeIdsToBuildOn.add(node.getId());
            }
        }
        return nodeIdsToBuildOn;
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
