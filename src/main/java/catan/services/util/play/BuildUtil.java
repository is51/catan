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
import catan.services.impl.GameServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static catan.services.impl.PlayServiceImpl.ERROR_CODE_ERROR;

@Component
public class BuildUtil {
    private Logger log = LoggerFactory.getLogger(BuildUtil.class);

    public void buildRoadOnEdge(GameUserBean gameUserBean, EdgeBean edgeToBuildOn) throws GameException {
        Building<EdgeBuiltType> building = new Building<EdgeBuiltType>();
        building.setBuilt(EdgeBuiltType.ROAD);
        building.setBuildingOwner(gameUserBean);

        edgeToBuildOn.setBuilding(building);
    }

    public void buildOnNode(GameUserBean gameUserBean, NodeBean nodeToBuildOn, NodeBuiltType nodeBuiltType) throws GameException {
        Building<NodeBuiltType> building = new Building<NodeBuiltType>();
        building.setBuilt(nodeBuiltType);
        building.setBuildingOwner(gameUserBean);

        nodeToBuildOn.setBuilding(building);

        updateBuildingsCount(nodeBuiltType, gameUserBean);
    }

    private void updateBuildingsCount(NodeBuiltType nodeBuiltType, GameUserBean gameUserBean) {
        switch (nodeBuiltType) {
            case SETTLEMENT:
                gameUserBean.getBuildingsCount().setSettlements(gameUserBean.getBuildingsCount().getSettlements() + 1);
                break;
            case CITY:
                if (gameUserBean.getGame().getStage().equals(GameStage.MAIN)) {
                    gameUserBean.getBuildingsCount().setSettlements(gameUserBean.getBuildingsCount().getSettlements() - 1);
                }

                gameUserBean.getBuildingsCount().setCities(gameUserBean.getBuildingsCount().getCities() + 1);
                break;
        }
    }

    public MapElement getValidMapElementByIdToBuildOn(String mapElementIdString, List<MapElement> mapElements) throws PlayException {
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

    public void validateUserCanBuildRoadOnEdge(GameUserBean gameUser, EdgeBean edgeToBuildOn, GameStage gameStage) throws PlayException {
        if (edgeToBuildOn.getBuilding() != null) {
            log.debug("Cannot build road on this edge as it already has building on it");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        for (NodeBean node : edgeToBuildOn.getNodes().listAllNotNullItems()) {
            if (GameStage.PREPARATION.equals(gameStage)) {
                if (node.hasBuildingBelongsToUser(gameUser) && !node.hasNeighbourRoadBelongsToGameUser(gameUser)) {
                    return;
                }
                continue;
            }

            if (node.getBuilding() == null) {
                if (node.hasNeighbourRoadBelongsToGameUser(gameUser)) {
                    return;
                }
            } else {
                if (node.getBuilding().getBuildingOwner().equals(gameUser)) {
                    return;
                }
            }
        }

        log.debug("Cannot build road that doesn't have neighbour road or settlement that belongs to this player ");
        throw new PlayException(ERROR_CODE_ERROR);
    }

    public void validateUserCanBuildSettlementOnNode(GameUserBean gameUser, GameStage gameStage, NodeBean nodeToBuildOn) throws PlayException {
        if (gameStage.equals(GameStage.PREPARATION)) {
            validateNodeIsEmpty(nodeToBuildOn);
            validateNoBuildingsCloseToNode(nodeToBuildOn);
            return;
        }
        validateNodeCouldBeUsedForBuildingSettlementByGameUserInMainStage(gameUser, nodeToBuildOn);
    }

    public void validateUserCanBuildCityOnNode(GameUserBean gameUser, GameStage gameStage, NodeBean nodeToBuildOn) throws GameException, PlayException {
        switch (gameStage) {
            case PREPARATION:
                validateNodeIsEmpty(nodeToBuildOn);
                validateNoBuildingsCloseToNode(nodeToBuildOn);
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

                GameUserBean buildingOwner = nodeToBuildOn.getBuilding().getBuildingOwner();
                if (!buildingOwner.equals(gameUser)) {
                    log.debug("Cannot build city on this node as building on it doesn't belong to user");
                    throw new PlayException(ERROR_CODE_ERROR);
                }
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", gameStage);
                throw new GameException(GameServiceImpl.ERROR_CODE_ERROR);
        }
    }

    private void validateNodeIsEmpty(NodeBean nodeToBuildOn) throws PlayException {
        if (nodeToBuildOn.getBuilding() != null) {
            log.debug("Cannot build building on this node as it already has building on it");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private void validateNodeCouldBeUsedForBuildingSettlementByGameUserInMainStage(GameUserBean gameUser, NodeBean nodeToBuildOn) throws PlayException {
        if (!nodeToBuildOn.couldBeUsedForBuildingSettlementByGameUserInMainStage(gameUser)) {
            log.debug("Cannot build settlement on node");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private void validateNoBuildingsCloseToNode(NodeBean nodeToBuildOn) throws PlayException {
        if (!nodeToBuildOn.hasAllNeighbourNodesEmpty()) {
            log.debug("Cannot build building close to other settlements");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }
}
