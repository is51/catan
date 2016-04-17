package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static catan.services.impl.GameServiceImpl.ERROR_CODE_ERROR;

@Component
public class PlayUtil {
    private Logger log = LoggerFactory.getLogger(PlayUtil.class);

    private PreparationStageUtil preparationStageUtil;
    private MainStageUtil mainStageUtil;

    private static final Gson GSON = new Gson();

    public AvailableActions toAvailableActionsFromJson(String availableActionsJson) {
        return GSON.fromJson(availableActionsJson, AvailableActions.class);
    }

    public void updateAvailableActionsForAllUsers(GameBean game) throws GameException {
        switch (game.getStage()) {
            case PREPARATION:
                preparationStageUtil.updateAvailableActionsForAllUsers(game);
                break;
            case MAIN:
                mainStageUtil.updateAvailableActionsForAllUsers(game);
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", game.getStage());
                throw new GameException(ERROR_CODE_ERROR);
        }
    }

    public void updateNextMove(GameBean game) throws GameException {
        switch (game.getStage()) {
            case PREPARATION:
                preparationStageUtil.updateNextMove(game);
                break;
            case MAIN:
                mainStageUtil.updateNextMove(game);
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", game.getStage());
                throw new GameException(ERROR_CODE_ERROR);
        }
    }

    public void updateUsersResources(NodeBean node) throws GameException {
        GameBean game = node.getGame();
        switch (game.getStage()) {
            case PREPARATION:
                preparationStageUtil.distributeResourcesForLastBuilding(node);
                break;
            case MAIN:
                if (NodeBuiltType.SETTLEMENT.equals(node.getBuilding().getBuilt())) {
                    game.fetchActiveGameUser().getResources().addResources(-1, -1, -1, -1, 0);
                }
                if (NodeBuiltType.CITY.equals(node.getBuilding().getBuilt())) {
                    game.fetchActiveGameUser().getResources().addResources(0, 0, 0, -2, -3);
                }
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", game.getStage());
                throw new GameException(ERROR_CODE_ERROR);
        }
    }

    public void produceResourcesFromActiveDiceHexes(GameBean game) {
        List<HexBean> hexesWithCurrentDiceValue = game.fetchHexesWithCurrentDiceValue();
        if (hexesWithCurrentDiceValue.size() == 0) {
            return;
        }
        log.debug("Producing resources form hexes with current dice value:" + (hexesWithCurrentDiceValue.isEmpty() ? "" : "\n\t\t") + hexesWithCurrentDiceValue.toString());
        for (HexBean hex : hexesWithCurrentDiceValue) {
            if (hex.isRobbed()) {
                log.debug("Ignoring for produce resources from robbed " + hex);
                continue;
            }
            for (NodeBean node : hex.fetchNodesWithBuildings()) {
                ResourceUtil.produceResources(hex, node.getBuilding(), log);
            }
        }
    }

    public void updateUsersResources(EdgeBean edge) {
        GameBean game = edge.getGame();
        if (GameStage.MAIN.equals(game.getStage()) && game.getRoadsToBuildMandatory() == 0) {
            game.fetchActiveGameUser().getResources().addResources(-1, -1, 0, 0, 0);
        }
    }

    public void updateRoadsToBuildMandatory(GameBean game) {
        Integer mandatoryRoads = game.getRoadsToBuildMandatory();
        if (mandatoryRoads > 0) {
            game.setRoadsToBuildMandatory(mandatoryRoads - 1);
        }
    }

    public void resetDevCardUsage(GameUserBean gameUser) {
        GameBean game = gameUser.getGame();
        if (GameStage.PREPARATION.equals(game.getStage())) {
            return;
        }
        gameUser.setDevelopmentCardsReadyForUsing(gameUser.getDevelopmentCards());
        game.setDevelopmentCardUsed(false);
    }

    public void resetTradeReplies(GameBean game) {
        if (GameStage.PREPARATION.equals(game.getStage())) {
            return;
        }
        for (GameUserBean currentGameUser : game.getGameUsers()) {
            currentGameUser.setAvailableTradeReply(false);
        }
    }

    public void resetDices(GameBean game) {
        if (GameStage.PREPARATION.equals(game.getStage())) {
            return;
        }
        game.setDiceThrown(false);
        game.setDiceFirstValue(null);
        game.setDiceSecondValue(null);
    }

    public void setDiceValues(Integer diceFirstValue, Integer diceSecondValue, GameBean game) {
        game.setDiceFirstValue(diceFirstValue);
        game.setDiceSecondValue(diceSecondValue);
        game.setDiceThrown(true);
        log.info("Current dice value is " + (diceFirstValue + diceSecondValue) + " (First dice: " + diceFirstValue + ", Second dice: " + diceSecondValue + ")");
    }

    public void activateRobberIfNeeded(GameBean game) {
        if (isRobbersActivity(game)) {
            log.debug("Robber activity is started, checking if players should kick-off resources");
            checkIfPlayersShouldKickOffResources(game);
        }
    }

    public void updateGameStage(GameBean game) {
        if (GameStage.PREPARATION.equals(game.getStage()) && shouldChangeGameStageToMain(game)) {
            game.setStage(GameStage.MAIN);
            game.setDiceThrown(false);
            game.setDevelopmentCardUsed(false);
            game.setChoosePlayerToRobMandatory(false);
            game.setRoadsToBuildMandatory(0);
            for (GameUserBean gameUser : game.getGameUsers()) {
                gameUser.setKickingOffResourcesMandatory(false);
                gameUser.setAvailableTradeReply(false);
            }
            preparationStageUtil.updatePreparationCycle(game);
            log.debug("Game Stage was changed from PREPARATION to {}", game.getStage());
        }
    }

    private boolean shouldChangeGameStageToMain(GameBean game) {
        return preparationStageUtil.isLastCycle(game) && preparationStageUtil.isEndOfCycle(game) && game.isUpdatePreparationCycle() != null && game.isUpdatePreparationCycle();
    }

    public void finishGameIfTargetVictoryPointsReached(GameUserBean gameUser) {
        GameBean game = gameUser.getGame();
        if (gameUser.getMoveOrder() == game.getCurrentMove()) {
            int realVictoryPoints = gameUser.getDevelopmentCards().getVictoryPoint() + gameUser.getAchievements().getDisplayVictoryPoints();
            if (realVictoryPoints >= game.getTargetVictoryPoints()) {
                game.setStatus(GameStatus.FINISHED);
            }
        }
    }

    public void updateAchievements(GameBean game) throws GameException {
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (game.getCurrentMove().equals(gameUser.getMoveOrder())) {
                updateTotalDevCards(gameUser);
            }
            updateVictoryPoints(gameUser, game);
            updateTotalResources(gameUser);
        }
    }

    private boolean isRobbersActivity(GameBean game) {
        return game.getDiceFirstValue() + game.getDiceSecondValue() == 7;
    }

    private void checkIfPlayersShouldKickOffResources(GameBean game) {
        GameUserBean gameUser = game.fetchActiveGameUser();
        boolean shouldResourcesBeKickedOff = false;
        for (GameUserBean gameUserIterated : game.getGameUsers()) {
            if (gameUserIterated.getAchievements().getTotalResources() > 7) {
                gameUserIterated.setKickingOffResourcesMandatory(true);
                shouldResourcesBeKickedOff = true;
            }
        }

        if (shouldResourcesBeKickedOff) {
            MessagesUtil.updateDisplayedMsg(gameUser, "help_msg_wait_for_kicking_off_res");
            return;
        }
        game.setRobberShouldBeMovedMandatory(true);
        MessagesUtil.updateDisplayedMsg(gameUser, "help_msg_move_robber");
    }

    private void updateTotalDevCards(GameUserBean gameUser) {
        int totalCards = gameUser.getDevelopmentCards().calculateSum();
        gameUser.getAchievements().setTotalCards(totalCards);
    }

    private void updateTotalResources(GameUserBean gameUser) {
        int totalResources = gameUser.getResources().calculateSum();
        gameUser.getAchievements().setTotalResources(totalResources);
    }

    private void updateVictoryPoints(GameUserBean gameUser, GameBean game) throws GameException {

        int settlementsCount = gameUser.getBuildingsCount().getSettlements();
        int citiesCount = gameUser.getBuildingsCount().getCities();
        boolean isBiggestArmyOwner = gameUser.equals(game.getBiggestArmyOwner());
        boolean isLongestWayOwner = gameUser.equals(game.getLongestWayOwner());

        int displayVictoryPoints = settlementsCount + citiesCount * 2 + (isBiggestArmyOwner ? 2 : 0) + (isLongestWayOwner ? 2 : 0);
        gameUser.getAchievements().setDisplayVictoryPoints(displayVictoryPoints);
    }

    public void changeRobbedHex(HexBean hexToRob) {
        for (HexBean hex : hexToRob.getGame().getHexes()) {
            if (hex.isRobbed()) {
                hex.setRobbed(false);
                break;
            }
        }
        hexToRob.setRobbed(true);
        log.info("Hex {} successfully robbed", hexToRob.getAbsoluteId());
    }

    public void validateGameIdNotEmpty(String gameId) throws GameException {
        if (gameId == null || gameId.trim().length() == 0) {
            log.error("Cannot get game with empty gameId");
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    public void validateUserNotEmpty(UserBean user) throws PlayException {
        if (user == null) {
            log.debug("User should not be empty");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public void validateGameStatusIsPlaying(GameBean game) throws GameException {
        if (game.getStatus() != GameStatus.PLAYING) {
            log.debug("User cannot do this action in current game status: {} instead of {}", game.getStatus(), GameStatus.PLAYING);
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    public void validateActionIsAllowedForUser(GameUserBean gameUser, GameUserActionCode requiredAction) throws PlayException, GameException {
        if (requiredAction.equals(GameUserActionCode.TRADE_REPLY) && gameUser.isAvailableTradeReply()) {
            return;
        }

        String availableActionsJson = gameUser.getAvailableActions();
        AvailableActions availableActions = toAvailableActionsFromJson(availableActionsJson);
        for (Action allowedActions : availableActions.getList()) {
            if (allowedActions.getCode().equals(requiredAction.name())) {
                return;
            }
        }

        log.debug("Required action {} is not allowed for {}", requiredAction.name(), gameUser);
        throw new PlayException(ERROR_CODE_ERROR);
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
