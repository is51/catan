package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.MapElement;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.services.PlayService;
import catan.services.util.game.GameUtil;
import catan.services.util.play.BuildUtil;
import catan.services.util.play.PlayUtil;
import catan.services.util.play.PreparationStageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service("playService")
@Transactional
public class PlayServiceImpl implements PlayService {
    private Logger log = LoggerFactory.getLogger(PlayService.class);

    public static final String ERROR_CODE_ERROR = "ERROR";

    private GameDao gameDao;
    private GameUtil gameUtil;
    private PlayUtil playUtil;
    private BuildUtil buildUtil;
    private PreparationStageUtil preparationStageUtil;

    @Override
    public void buildRoad(UserBean user, String gameIdString, String edgeIdString) throws PlayException, GameException {
        log.debug("{} tries to build road at edge {} of game id {}", user, edgeIdString, gameIdString);

        GameBean game = gameUtil.getGameById(gameIdString, ERROR_CODE_ERROR);
        validateUserNotEmpty(user);
        validateGameStatusIsPlaying(game);
        validateActionIsAllowed(user, game, GameUserActionCode.BUILD_ROAD);

        EdgeBean edgeToBuildOn = (EdgeBean) buildUtil.getValidMapElementByIdToBuildOn(edgeIdString, new ArrayList<MapElement>(game.getEdges()));
        buildUtil.validateUserCanBuildRoanOnEdge(user, edgeToBuildOn);
        buildUtil.buildRoadOnEdge(user, edgeToBuildOn);

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

        NodeBean nodeToBuildOn = (NodeBean) buildUtil.getValidMapElementByIdToBuildOn(nodeIdString, new ArrayList<MapElement>(game.getNodes()));
        buildUtil.validateUserCanBuildSettlementOnNode(user, game.getStage(), nodeToBuildOn);
        buildUtil.buildOnNode(user, nodeToBuildOn, NodeBuiltType.SETTLEMENT);

        preparationStageUtil.updateCurrentCycleBuildingNumber(game);
        playUtil.updateAvailableUserActions(game);

        gameDao.updateGame(game);

        log.debug("User {} successfully built settlement at node {} of game id {}", user.getUsername(), nodeIdString, gameIdString);
    }

    @Override
    public void buildCity(UserBean user, String gameIdString, String nodeIdString) throws PlayException, GameException {
        log.debug("{} tries to build city at node {} of game id {}", user, nodeIdString, gameIdString);

        GameBean game = gameUtil.getGameById(gameIdString, ERROR_CODE_ERROR);
        validateUserNotEmpty(user);
        validateGameStatusIsPlaying(game);
        validateActionIsAllowed(user, game, GameUserActionCode.BUILD_CITY);

        NodeBean nodeToBuildOn = (NodeBean) buildUtil.getValidMapElementByIdToBuildOn(nodeIdString, new ArrayList<MapElement>(game.getNodes()));
        buildUtil.validateUserCanBuildCityOnNode(user, game.getStage(), nodeToBuildOn);
        buildUtil.buildOnNode(user, nodeToBuildOn, NodeBuiltType.CITY);

        preparationStageUtil.updateCurrentCycleBuildingNumber(game);
        playUtil.updateAvailableUserActions(game);

        gameDao.updateGame(game);

        log.debug("User {} successfully built city at node {} of game id {}", user.getUsername(), nodeIdString, gameIdString);
    }

    @Override
    public void endTurn(UserBean user, String gameIdString) throws PlayException, GameException {
        log.debug("{} tries to end his turn of game id {}", user, gameIdString);

        GameBean game = gameUtil.getGameById(gameIdString, ERROR_CODE_ERROR);
        validateUserNotEmpty(user);
        validateGameStatusIsPlaying(game);
        validateActionIsAllowed(user, game, GameUserActionCode.END_TURN);

        //TODO: think about refactoring this part
        boolean shouldUpdateNextMove = true;
        if (game.getStage().equals(GameStage.PREPARATION)) {
            Integer currentPreparationCycle = game.getPreparationCycle();
            preparationStageUtil.updateGameStageToMain(game); //TODO: move it to the end of method calls
            preparationStageUtil.updateCurrentCycleBuildingNumber(game);
            preparationStageUtil.updatePreparationCycle(game);
            shouldUpdateNextMove = currentPreparationCycle.equals(game.getPreparationCycle());
        }

        if (shouldUpdateNextMove) {
            playUtil.updateNextMove(game);
        }

        playUtil.updateAvailableUserActions(game);

        gameDao.updateGame(game);


        log.debug("User {} successfully ended his turn of game id {}", user.getUsername(), gameIdString);
    }

    private void validateUserNotEmpty(UserBean user) throws PlayException {
        if (user == null) {
            log.debug("User should not be empty");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private void validateGameStatusIsPlaying(GameBean game) throws GameException {
        if (game.getStatus() != GameStatus.PLAYING) {
            log.debug("User cannot do this action in current game status: {} instead of {}", game.getStatus(), GameStatus.PLAYING);
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    private void validateActionIsAllowed(UserBean user, GameBean game, GameUserActionCode requiredAction) throws PlayException, GameException {
        String availableActionsJson = gameUtil.getGameUserJoinedToGame(user, game).getAvailableActions();
        AvailableActions availableActions = playUtil.toAvailableActionsFromJson(availableActionsJson);

        boolean actionAllowed = false;
        for (Action allowedActions : availableActions.getList()) {
            if (allowedActions.getCode().equals(requiredAction.name())) {
                actionAllowed = true;
            }
        }

        if (!actionAllowed) {
            log.debug("Required action {} is not allowed for user", requiredAction.name());
            throw new PlayException(ERROR_CODE_ERROR);
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
    public void setBuildUtil(BuildUtil buildUtil) {
        this.buildUtil = buildUtil;
    }

    @Autowired
    public void setPreparationStageUtil(PreparationStageUtil preparationStageUtil) {
        this.preparationStageUtil = preparationStageUtil;
    }
}