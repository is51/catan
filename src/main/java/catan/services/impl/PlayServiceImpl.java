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
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.user.UserBean;
import catan.services.PlayService;
import catan.services.util.game.GameUtil;
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

    @Override
    public void buildRoad(UserBean user, String gameIdString, String edgeIdString) throws PlayException, GameException {
        log.debug("User {} tries to build road at edge {} of game id {}",
                user == null ? "<EMPTY>" : user.getUsername(), edgeIdString, gameIdString);
        //TODO: move to common validation method
        if (user == null) {
            log.debug("User should not be empty");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        int edgeId;
        try {
            if (edgeIdString == null || edgeIdString.trim().length() == 0) {
                log.debug("Cannot build road on empty edgeId");
                throw new PlayException(ERROR_CODE_ERROR);
            }

            edgeId = Integer.parseInt(edgeIdString);
        } catch (Exception e) {
            log.debug("Cannot convert edgeId to integer value");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        GameBean game = gameUtil.getGameById(gameIdString, ERROR_CODE_ERROR);
        if (game.getStatus() != GameStatus.PLAYING) {
            log.debug("Cannot build road in not playing game");
            throw new GameException(ERROR_CODE_ERROR);
        }

        //TODO: move to util method and refactor all other places
        GameUserBean gameUserBean = null;
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getUser().equals(user)) {
                gameUserBean = gameUser;
                break;
            }
        }

        if (gameUserBean == null) {
            log.debug("User is not joined to game id specified %s", gameIdString);
            throw new PlayException(ERROR_CODE_ERROR);
        }

        EdgeBean edgeToBuildOn = null;
        for (EdgeBean edge : game.getEdges()) {
            if (edge.getId() == edgeId) {
                edgeToBuildOn = edge;
            }
        }

        if (edgeToBuildOn == null) {
            log.debug("Cannot build road on edgeId {} that does not belong to game {}", edgeId, gameIdString);
            throw new PlayException(ERROR_CODE_ERROR);
        }

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
            log.debug("Cannot build that doesn't have neighbour road or settlers that belongs to this player ");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        Building<EdgeBuiltType> building = new Building<EdgeBuiltType>();
        building.setBuilt(EdgeBuiltType.ROAD);
        building.setBuildingOwner(gameUserBean);

        edgeToBuildOn.setBuilding(building);

        gameDao.updateGame(game);
    }

    @Override
    public void buildSettlement(UserBean user, String gameIdString, String nodeIdString) throws PlayException, GameException {
        log.debug("User {} tries to build settlement at node {} of game id {}",
                user == null ? "<EMPTY>" : user.getUsername(), nodeIdString, gameIdString);
        if (user == null) {
            log.debug("User should not be empty");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        int nodeId;
        try {
            if (nodeIdString == null || nodeIdString.trim().length() == 0) {
                log.debug("Cannot build settlement on empty nodeId");
                throw new PlayException(ERROR_CODE_ERROR);
            }

            nodeId = Integer.parseInt(nodeIdString);
        } catch (Exception e) {
            log.debug("Cannot convert nodeId to integer value");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        GameBean game = gameUtil.getGameById(gameIdString, ERROR_CODE_ERROR);
        if (game.getStatus() != GameStatus.PLAYING) {
            log.debug("Cannot build settlement in not playing game");
            throw new GameException(ERROR_CODE_ERROR);
        }

        GameUserBean gameUserBean = null;
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getUser().equals(user)) {
                gameUserBean = gameUser;
                break;
            }
        }

        if (gameUserBean == null) {
            log.debug("User is not joined to game {}", gameIdString);
            throw new PlayException(ERROR_CODE_ERROR);
        }

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

        if (!nearNeighbourRoad) {
            log.debug("Cannot build settlement without any connections with player's roads");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        Building<NodeBuiltType> building = new Building<NodeBuiltType>();
        building.setBuilt(NodeBuiltType.SETTLEMENT);
        building.setBuildingOwner(gameUserBean);

        nodeToBuildOn.setBuilding(building);

        gameDao.updateGame(game);

    }

    @Override
    public void endTurn(UserBean user, String gameIdString) throws PlayException, GameException {
        log.debug("User {} tries to end his turn of game id {}",
                user == null ? "<EMPTY>" : user.getUsername(), gameIdString);
        //TODO: move to common validation method
        if (user == null) {
            log.debug("User should not be empty");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        GameBean game = gameUtil.getGameById(gameIdString, ERROR_CODE_ERROR);
        if (game.getStatus() != GameStatus.PLAYING) {
            log.debug("Cannot end turn of not playing game");
            throw new GameException(ERROR_CODE_ERROR);
        }

        //TODO: move to util method and refactor all other places
        GameUserBean gameUserBean = null;
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getUser().equals(user)) {
                gameUserBean = gameUser;
                break;
            }
        }

        if (gameUserBean == null) {
            log.debug("User is not joined to game with specified id {}", gameIdString);
            throw new PlayException(ERROR_CODE_ERROR);
        }

        if (!game.getCurrentMove().equals(gameUserBean.getMoveOrder())) {
            log.debug("It is not current turn of user {}", user.getUsername());
            throw new PlayException(ERROR_CODE_ERROR);
        }

        //TODO: think about naming of method and namings of fields related to moveOrder and turn
        giveCurrentMoveToNextPlayer(game);

        gameDao.updateGame(game);
    }

    private void giveCurrentMoveToNextPlayer(GameBean game) {
        int nextMove = 1;
        if (!game.getCurrentMove().equals(game.getGameUsers().size())) {
            nextMove = game.getCurrentMove() + 1;
        }

        game.setCurrentMove(nextMove);
    }

    @Autowired
    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @Autowired
    public void setGameUtil(GameUtil gameUtil) {
        this.gameUtil = gameUtil;
    }


}
