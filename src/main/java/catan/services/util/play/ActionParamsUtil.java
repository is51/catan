package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
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
        List<Integer> nodeIdsToBuildOn = new ArrayList<Integer>();
        switch (game.getStage()) {
            case PREPARATION:
                for (NodeBean node : game.getNodes()) {
                    if (node.getBuilding() == null && node.noBuildingsOnNeighbourNodes()) {
                        nodeIdsToBuildOn.add(node.getId());
                    }
                }
                break;
            case MAIN:
                for (NodeBean node : game.getNodes()) {
                    if (node.getBuilding() == null
                            && node.noBuildingsOnNeighbourNodes()
                            && node.nearGameUsersNeighbourRoad(gameUser)) {
                        nodeIdsToBuildOn.add(node.getId());
                    }
                }
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", game.getStage());
                throw new GameException(ERROR_CODE_ERROR);
        }
        return nodeIdsToBuildOn;
    }
}
