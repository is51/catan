package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.Building;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.EdgeBuiltType;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.services.PlayService;
import catan.services.util.game.GameUtil;
import catan.services.util.play.PlayUtil;
import catan.services.util.play.PreparationStageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("playService")
@Transactional
public class PlayServiceImpl implements PlayService {
    private Logger log = LoggerFactory.getLogger(PlayService.class);

    public static final String ERROR_CODE_ERROR = "ERROR";

    private GameDao gameDao;
    private GameUtil gameUtil;
    private PlayUtil playUtil;
    private PreparationStageUtil preparationStageUtil;

    @Override
    public void buildRoad(UserBean user, String gameIdString, String edgeIdString) throws PlayException, GameException {
        log.debug("{} tries to build road at edge {} of game id {}", user, edgeIdString, gameIdString);

        GameBean game = gameUtil.getGameById(gameIdString, ERROR_CODE_ERROR);
        validateUserNotEmpty(user);
        validateGameStatusIsPlaying(game);
        validateActionIsAllowed(user, game, GameUserActionCode.BUILD_ROAD);

        EdgeBean edgeToBuildOn = getValidEdgeToBuildOn(edgeIdString, game);
        validateCanBuildRoanOnEdge(user, edgeToBuildOn);
        buildRoadOnEdge(user, edgeToBuildOn);

        preparationStageUtil.updateCurrentCycleBuildingNumber(game);
        playUtil.updateAvailableUserActions(game);

        gameDao.updateGame(game);

        log.debug("User {} successfully built road at edge {} of game id {}", user.getUsername(), edgeIdString, gameIdString);
    }

    @Override
    public void buildSettlement(UserBean user, String gameIdString, String nodeIdString) throws PlayException, GameException {
        log.debug("{} tries to build settlement at node {} of game id {}", user, nodeIdString, gameIdString);

        GameBean game = gameUtil.getGameById(gameIdString, ERROR_CODE_ERROR);
        validateUserNotEmpty(user);
        validateGameStatusIsPlaying(game);
        validateActionIsAllowed(user, game, GameUserActionCode.BUILD_SETTLEMENT);

        NodeBean nodeToBuildOn = getValidNodeToBuildOn(gameIdString, nodeIdString, game);
        validateCanBuildSettlementOnEdge(user, nodeToBuildOn);
        buildSettlementOnNode(user, nodeToBuildOn);

        preparationStageUtil.updateCurrentCycleBuildingNumber(game);
        playUtil.updateAvailableUserActions(game);

        gameDao.updateGame(game);

        log.debug("User {} successfully built settlement at node {} of game id {}", user.getUsername(), nodeIdString, gameIdString);
    }

    @Override
    public void endTurn(UserBean user, String gameIdString) throws PlayException, GameException {
        log.debug("{} tries to end his turn of game id {}", user, gameIdString);

        GameBean game = gameUtil.getGameById(gameIdString, ERROR_CODE_ERROR);
        validateUserNotEmpty(user);
        validateGameStatusIsPlaying(game);
        validateActionIsAllowed(user, game, GameUserActionCode.END_TURN);

        //TODO: think about refactoring this part
        if (game.getStage().equals(GameStage.PREPARATION)) {
            Integer currentPreparationCycle = game.getPreparationCycle();
            preparationStageUtil.updateGameStageToMain(game);
            preparationStageUtil.updateCurrentCycleBuildingNumber(game);
            preparationStageUtil.updatePreparationCycle(game);
            if (!currentPreparationCycle.equals(game.getPreparationCycle())) {
                preparationStageUtil.updateNextMoveInPreparationStage(game);
            }
        } else {
            playUtil.updateNextMoveInMainStage(game);
        }
        playUtil.updateAvailableUserActions(game);

        gameDao.updateGame(game);

        log.debug("User {} successfully ended his turn", user.getUsername(), gameIdString);
    }

    private void validateCanBuildSettlementOnEdge(UserBean user, NodeBean nodeToBuildOn) throws PlayException {
        if (nodeToBuildOn.getBuilding() != null) {
            log.debug("Cannot build settlement on this node as it already has building on it");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        UserBean opponent = null;
        boolean nearNeighbourRoad = false;
        for (EdgeBean edge : nodeToBuildOn.getEdges().all()) {
            if (edge != null) {
                if (edge.getBuilding() != null) {
                    UserBean buildingOwner = edge.getBuilding().getBuildingOwner().getUser();
                    if (buildingOwner.equals(user)) {
                        nearNeighbourRoad = true;
                    } else {
                        if (buildingOwner.equals(opponent)) {
                            log.debug("Cannot build settlement on opponents' ways");
                            throw new PlayException(ERROR_CODE_ERROR);
                        } else opponent = buildingOwner;
                    }
                }
                for (NodeBean node : edge.getNodes().all()) {
                    if (node != null && node != nodeToBuildOn && node.getBuilding() != null) {
                        log.debug("Cannot build settlement close to other settlements");
                        throw new PlayException(ERROR_CODE_ERROR);
                    }
                }
            }
        }

        /* TODO: uncomment this part when preparation development would be done
        if (!nearNeighbourRoad && !game.getStage().equals(GameStage.PREPARATION)) {
            log.debug("Cannot build settlement without any connections with player's roads");
            throw new PlayException(ERROR_CODE_ERROR);
        }
        */
    }

    private void validateCanBuildRoanOnEdge(UserBean user, EdgeBean edgeToBuildOn) throws PlayException {
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

    private NodeBean getValidNodeToBuildOn(String gameIdString, String nodeIdString, GameBean game) throws PlayException {
        int nodeId = toValidId(nodeIdString);
        NodeBean nodeToBuildOn = null;
        for (NodeBean node : game.getNodes()) {
            if (node.getId() == nodeId) {
                nodeToBuildOn = node;
            }
        }

        if (nodeToBuildOn == null) {
            log.debug("Cannot build settlement on nodeId {} that does not belong to game {}", nodeId, gameIdString);
            throw new PlayException(ERROR_CODE_ERROR);
        }
        return nodeToBuildOn;
    }

    private EdgeBean getValidEdgeToBuildOn(String edgeIdString, GameBean game) throws PlayException {
        int edgeId = toValidId(edgeIdString);
        EdgeBean edgeToBuildOn = null;
        for (EdgeBean edge : game.getEdges()) {
            if (edge.getId() == edgeId) {
                edgeToBuildOn = edge;
            }
        }

        if (edgeToBuildOn == null) {
            log.debug("Cannot build road on edgeId {} that does not belong to game {}", edgeId, game.getGameId());
            throw new PlayException(ERROR_CODE_ERROR);
        }

        return edgeToBuildOn;
    }

    private void buildSettlementOnNode(UserBean user, NodeBean nodeToBuildOn) throws PlayException {
        GameUserBean gameUserBean = getGameUserJoinedToGame(user, nodeToBuildOn.getGame());

        Building<NodeBuiltType> building = new Building<NodeBuiltType>();
        building.setBuilt(NodeBuiltType.SETTLEMENT);
        building.setBuildingOwner(gameUserBean);

        nodeToBuildOn.setBuilding(building);
    }

    private void buildRoadOnEdge(UserBean user, EdgeBean edgeToBuildOn) throws PlayException {
        GameUserBean gameUserBean = getGameUserJoinedToGame(user, edgeToBuildOn.getGame());

        Building<EdgeBuiltType> building = new Building<EdgeBuiltType>();
        building.setBuilt(EdgeBuiltType.ROAD);
        building.setBuildingOwner(gameUserBean);

        edgeToBuildOn.setBuilding(building);
    }

    private void validateActionIsAllowed(UserBean user, GameBean game, GameUserActionCode requiredAction) throws PlayException {
        String actionsString = getGameUserJoinedToGame(user, game).getAvailableActions();
        AvailableActions actions = playUtil.toAvailableActionsFromJson(actionsString);
        boolean actionAllowed = false;
        for (Action allowedActions : actions.getList()) {
            if (allowedActions.getCode().equals(requiredAction.name())) {
                actionAllowed = true;
            }
        }

        if (!actionAllowed) {
            log.debug("Required action {} is not allowed for user", requiredAction.name());
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private void validateUserNotEmpty(UserBean user) throws PlayException {
        if (user == null) {
            log.debug("User should not be empty");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private GameUserBean getGameUserJoinedToGame(UserBean user, GameBean game) throws PlayException {
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getUser().equals(user)) {
                return gameUser;
            }
        }

        log.debug("User is not joined to game {}", game.getGameId());
        throw new PlayException(ERROR_CODE_ERROR);
    }

    private Integer toValidId(String idString) throws PlayException {
        try {
            return Integer.parseInt(idString);
        } catch (Exception e) {
            log.debug("Cannot convert object Id to integer value");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private void validateGameStatusIsPlaying(GameBean game) throws GameException {
        if (game.getStatus() != GameStatus.PLAYING) {
            log.debug("User cannot do this action in current game status: {} instead of {}", game.getStatus(), GameStatus.PLAYING);
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    @Autowired
    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @Autowired
    public void setGameUtil(GameUtil gameUtil) {
        this.gameUtil = gameUtil;
    }

    @Autowired
    public void setPlayUtil(PlayUtil playUtil) {
        this.playUtil = playUtil;
    }

    @Autowired
    public void setPreparationStageUtil(PreparationStageUtil preparationStageUtil) {
        this.preparationStageUtil = preparationStageUtil;
    }
}