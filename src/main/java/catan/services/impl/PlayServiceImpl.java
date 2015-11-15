package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.MapElement;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.services.PlayService;
import catan.services.util.game.GameUtil;
import catan.services.util.play.BuildUtil;
import catan.services.util.play.MainStageUtil;
import catan.services.util.play.PlayUtil;
import catan.services.util.play.PreparationStageUtil;
import catan.services.util.random.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service("playService")
@Transactional
public class PlayServiceImpl implements PlayService {
    private Logger log = LoggerFactory.getLogger(PlayService.class);

    public static final String ERROR_CODE_ERROR = "ERROR";

    //TODO: since we inject preparationStageUtil, mainStageUtil and playUtil to playService and also preparationStageUtil and mainStageUtil to playUtil , I think we have wrong architecture, we should think how to refactor it
    private GameDao gameDao;
    private RandomUtil randomUtil;
    private GameUtil gameUtil;
    private PlayUtil playUtil;
    private BuildUtil buildUtil;
    private PreparationStageUtil preparationStageUtil;
    private MainStageUtil mainStageUtil;

    @Override
    public void processAction(GameUserActionCode action, UserBean user, String gameId) throws PlayException, GameException {
        processAction(action, user, gameId, new HashMap<String, String>());
    }

    @Override
    public void processAction(GameUserActionCode action, UserBean user, String gameId, Map<String, String> params) throws PlayException, GameException {
        log.debug("{} tries to perform action {} at game with id {} and additional params {}", user, action, gameId, params);

        validateUserNotEmpty(user);

        GameBean game = gameUtil.getGameById(gameId, ERROR_CODE_ERROR);
        GameUserBean gameUser = gameUtil.getGameUserJoinedToGame(user, game);

        validateGameStatusIsPlaying(game);
        validateActionIsAllowedForUser(gameUser, action);
        doAction(action, user, game, params);

        playUtil.updateVictoryPoints(gameUser);
        playUtil.finishGameIfTargetVictoryPointsReached(gameUser, game);
        playUtil.updateAvailableActionsForAllUsers(game);

        gameDao.updateGame(game);

        log.debug("User {} successfully performed action {}", user.getUsername(), action);
    }

    private void doAction(GameUserActionCode action, UserBean user, GameBean game, Map<String, String> params) throws PlayException, GameException {
        switch(action){
            case BUILD_ROAD:
                buildRoad(user, game, params.get("edgeId"));
                break;
            case BUILD_SETTLEMENT:
                buildSettlement(user, game, params.get("nodeId"));
                break;
            case BUILD_CITY:
                buildCity(user, game, params.get("nodeId"));
                break;
            case END_TURN:
                endTurn(game);
                break;
            case THROW_DICE:
                throwDice(game);
                break;
        }
    }

    private void buildRoad(UserBean user, GameBean game, String edgeId) throws PlayException, GameException {
        EdgeBean edgeToBuildOn = (EdgeBean) buildUtil.getValidMapElementByIdToBuildOn(edgeId, new ArrayList<MapElement>(game.getEdges()));
        buildUtil.validateUserCanBuildRoadOnEdge(user, edgeToBuildOn);
        buildUtil.buildRoadOnEdge(user, edgeToBuildOn);

        preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);
    }

    private void buildSettlement(UserBean user, GameBean game, String nodeId) throws PlayException, GameException {
        NodeBean nodeToBuildOn = (NodeBean) buildUtil.getValidMapElementByIdToBuildOn(nodeId, new ArrayList<MapElement>(game.getNodes()));
        buildUtil.validateUserCanBuildSettlementOnNode(user, game.getStage(), nodeToBuildOn);
        buildUtil.buildOnNode(user, nodeToBuildOn, NodeBuiltType.SETTLEMENT);

        preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);
    }

    private void buildCity(UserBean user, GameBean game, String nodeId) throws PlayException, GameException {
        NodeBean nodeToBuildOn = (NodeBean) buildUtil.getValidMapElementByIdToBuildOn(nodeId, new ArrayList<MapElement>(game.getNodes()));
        buildUtil.validateUserCanBuildCityOnNode(user, game.getStage(), nodeToBuildOn);
        buildUtil.buildOnNode(user, nodeToBuildOn, NodeBuiltType.CITY);

        preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);
    }

    private void endTurn(GameBean game) throws GameException {
        switch (game.getStage()) {

            case PREPARATION:
                Integer previousPreparationCycle = game.getPreparationCycle();
                preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);
                preparationStageUtil.updatePreparationCycle(game);
                if (game.getPreparationCycle() == null || previousPreparationCycle.equals(game.getPreparationCycle())) {
                    preparationStageUtil.updateNextMove(game);
                }
                preparationStageUtil.updateGameStageToMain(game);
                break;

            case MAIN:
                mainStageUtil.resetDices(game);
                mainStageUtil.updateNextMove(game);
                break;
        }
    }

    private void throwDice(GameBean game) {
        Integer diceFirstValue = randomUtil.getRandomDiceNumber();
        Integer diceSecondValue = randomUtil.getRandomDiceNumber();
        game.setDiceFirstValue(diceFirstValue);
        game.setDiceSecondValue(diceSecondValue);
        game.setDiceThrown(true);
        if (!isRobbersActivity(diceFirstValue, diceSecondValue)) {
            mainStageUtil.produceResourcesForUsersThatHaveBuildingsCloseToActiveHexes(game.fetchHexesWithCurrentDiceValue());
        }
    }

    private boolean isRobbersActivity(Integer diceFirstValue, Integer diceSecondValue) {
        return diceFirstValue + diceSecondValue == 7;
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

    private void validateActionIsAllowedForUser(GameUserBean gameUser, GameUserActionCode requiredAction) throws PlayException, GameException {
        String availableActionsJson = gameUser.getAvailableActions();
        AvailableActions availableActions = playUtil.toAvailableActionsFromJson(availableActionsJson);

        boolean actionAllowed = false;
        for (Action allowedActions : availableActions.getList()) {
            if (allowedActions.getCode().equals(requiredAction.name())) {
                actionAllowed = true;
            }
        }

        if (!actionAllowed) {
            log.debug("Required action {} is not allowed for {}, current move in game is {}",
                    requiredAction.name(), gameUser, gameUser.getGame().getCurrentMove());
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    @Autowired
    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @Autowired
    public void setRandomUtil(RandomUtil randomUtil) {
        this.randomUtil = randomUtil;
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

    @Autowired
    public void setMainStageUtil(MainStageUtil mainStageUtil) {
        this.mainStageUtil = mainStageUtil;
    }
}