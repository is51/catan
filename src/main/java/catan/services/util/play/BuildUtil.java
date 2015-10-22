package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.Building;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.MapElement;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.EdgeBuiltType;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.user.UserBean;
import catan.services.impl.GameServiceImpl;
import catan.services.util.game.GameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static catan.services.impl.PlayServiceImpl.ERROR_CODE_ERROR;

@Component
public class BuildUtil {
    private Logger log = LoggerFactory.getLogger(BuildUtil.class);

    private GameUtil gameUtil;

    public MapElement getValidMapElementToBuildOn(String mapElementIdString, List<MapElement> mapElements) throws PlayException {
        int mapElementId;
        try {
            mapElementId = Integer.parseInt(mapElementIdString);
        } catch (Exception e) {
            log.debug("Cannot convert object Id to integer value");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        MapElement mapElementToBuildOn = null;
        for (MapElement mapElement : mapElements) {
            if (mapElement.getId() == mapElementId) {
                mapElementToBuildOn = mapElement;
            }
        }

        if (mapElementToBuildOn == null) {
            log.debug("Cannot build mapElement on map item with id {}", mapElementIdString);
            throw new PlayException(ERROR_CODE_ERROR);
        }

        return mapElementToBuildOn;
    }

    public void validateUserCanBuildRoanOnEdge(UserBean user, EdgeBean edgeToBuildOn) throws PlayException {
        if (edgeToBuildOn.getBuilding() != null) {
            log.debug("Cannot build road on this edge as it already has building on it");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        boolean nearNeighbourRoad = false;
        boolean nearNeighbourSettlement = false;

        for (NodeBean node : edgeToBuildOn.getNodes().all()) {
            if (node == null) {
                continue;
            }

            if (node.getBuilding() == null) {
                for (EdgeBean neighbourEdge : node.getEdges().all()) {
                    if (neighbourEdge == null || neighbourEdge.equals(edgeToBuildOn)) {
                        continue;
                    }

                    if (neighbourEdge.getBuilding() != null && neighbourEdge.getBuilding().getBuildingOwner().getUser().equals(user)) {
                        nearNeighbourRoad = true;
                        break;
                    }
                }
            } else {
                if (node.getBuilding().getBuildingOwner().getUser().equals(user)) {
                    nearNeighbourSettlement = true;
                    break;
                } else {
                    log.debug("Cannot build road close to settles that don't belong to user");
                    throw new PlayException(ERROR_CODE_ERROR);
                }
            }
        }

        if (!nearNeighbourRoad && !nearNeighbourSettlement) {
            log.debug("Cannot build road that doesn't have neighbour road or settlement that belongs to this player ");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public void validateUserCanBuildSettlementOnNode(UserBean user, GameStage gameStage, NodeBean nodeToBuildOn) throws PlayException {

        validateNodeToBuildOnIsEmpty(nodeToBuildOn);
        validateThereIsNoOtherBuildingsCloseToNodeToBuildOn(nodeToBuildOn);

        if (gameStage.equals(GameStage.MAIN)) {

            boolean nearOwnNeighbourRoad = false;
            for (EdgeBean edge : nodeToBuildOn.getEdges().all()) {
                if (edge != null && edge.getBuilding() != null && edge.getBuilding().getBuildingOwner().getUser().equals(user)) {
                    nearOwnNeighbourRoad = true;
                }
            }

            if (!nearOwnNeighbourRoad) {
                log.debug("Cannot build settlement without any connections with player's roads");
                throw new PlayException(ERROR_CODE_ERROR);
            }
        }
    }

    public void validateUserCanBuildCityOnNode(UserBean user, GameStage gameStage, NodeBean nodeToBuildOn) throws GameException, PlayException {
        switch (gameStage) {
            case PREPARATION:
                validateNodeToBuildOnIsEmpty(nodeToBuildOn);
                validateThereIsNoOtherBuildingsCloseToNodeToBuildOn(nodeToBuildOn);
                break;
            case MAIN:
                if (nodeToBuildOn.getBuilding() == null) {
                    log.debug("Cannot build city on empty node. User should build settlement first");
                    throw new PlayException(ERROR_CODE_ERROR);
                }
                if (nodeToBuildOn.getBuilding().getBuilt() == NodeBuiltType.CITY) {
                    log.debug("Cannot build city on this node as it is already built");
                    throw new PlayException(ERROR_CODE_ERROR);
                }
                UserBean buildingOwner = nodeToBuildOn.getBuilding().getBuildingOwner().getUser();
                if (buildingOwner != user) {
                    log.debug("Cannot build city on this node as building on it doesn't belong to user");
                    throw new PlayException(ERROR_CODE_ERROR);
                }
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", gameStage);
                throw new GameException(GameServiceImpl.ERROR_CODE_ERROR);
        }
    }

    public void buildOnNode(UserBean user, NodeBean nodeToBuildOn, NodeBuiltType nodeBuiltType) throws GameException {
        GameUserBean gameUserBean = gameUtil.getGameUserJoinedToGame(user, nodeToBuildOn.getGame());

        Building<NodeBuiltType> building = new Building<NodeBuiltType>();
        building.setBuilt(nodeBuiltType);
        building.setBuildingOwner(gameUserBean);

        nodeToBuildOn.setBuilding(building);
    }

    public void buildRoadOnEdge(UserBean user, EdgeBean edgeToBuildOn) throws GameException {
        GameUserBean gameUserBean = gameUtil.getGameUserJoinedToGame(user, edgeToBuildOn.getGame());

        Building<EdgeBuiltType> building = new Building<EdgeBuiltType>();
        building.setBuilt(EdgeBuiltType.ROAD);
        building.setBuildingOwner(gameUserBean);

        edgeToBuildOn.setBuilding(building);
    }

    private void validateNodeToBuildOnIsEmpty(NodeBean nodeToBuildOn) throws PlayException {
        if (nodeToBuildOn.getBuilding() != null) {
            log.debug("Cannot build building on this node as it already has building on it");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private void validateThereIsNoOtherBuildingsCloseToNodeToBuildOn(NodeBean nodeToBuildOn) throws PlayException {
        for (EdgeBean edge : nodeToBuildOn.getEdges().all()) {
            if (edge != null) {
                for (NodeBean node : edge.getNodes().all()) {
                    if (node != null && node != nodeToBuildOn && node.getBuilding() != null) {
                        log.debug("Cannot build building close to other settlements");
                        throw new PlayException(ERROR_CODE_ERROR);
                    }
                }
            }
        }
    }

    @Autowired
    public void setGameUtil(GameUtil gameUtil) {
        this.gameUtil = gameUtil;
    }
}
