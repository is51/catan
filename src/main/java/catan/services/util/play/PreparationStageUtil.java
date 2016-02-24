package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.actions.ActionOnEdgeParams;
import catan.domain.model.game.actions.ActionOnNodeParams;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameUserActionCode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PreparationStageUtil {
    private Logger log = LoggerFactory.getLogger(PreparationStageUtil.class);

    private static final Gson GSON = new Gson();

    private ActionParamsUtil actionParamsUtil;

    public List<List<GameUserActionCode>> toInitialBuildingsSetFromJson(String initialBuildingsSetJson) {
        return GSON.fromJson(initialBuildingsSetJson, new TypeToken<List<List<GameUserActionCode>>>() {
        }.getType());
    }

    public void updateNextMove(GameBean game) {
        Integer nextMoveNumber;

        if (game.getPreparationCycle() == null) {
            nextMoveNumber = 1;
        } else {
            Integer currentMove = game.getCurrentMove();
            if (game.getPreparationCycle() % 2 > 0) {
                nextMoveNumber = currentMove.equals(game.getGameUsers().size())
                        ? currentMove
                        : currentMove + 1;
            } else {
                nextMoveNumber = currentMove.equals(1)
                        ? 1
                        : currentMove - 1;
            }
        }

        log.debug("Next move order in {} stage is changing from {} to {}", game.getStage(), game.getCurrentMove(), nextMoveNumber);
        game.setCurrentMove(nextMoveNumber);
    }

    public void updateCurrentCycleInitialBuildingNumber(GameBean game) {
        if (game.getStage().equals(GameStage.PREPARATION)) {
            List<List<GameUserActionCode>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
            Integer numberOfBuildingsInCycle = initialBuildingsSet.get(game.getPreparationCycle() - 1).size();
            Integer currentCycleBuildingNumber = game.getCurrentCycleBuildingNumber();

            if (numberOfBuildingsInCycle.equals(currentCycleBuildingNumber)) {
                game.setCurrentCycleBuildingNumber(null);
            } else if (currentCycleBuildingNumber == null) {
                game.setCurrentCycleBuildingNumber(1);
            } else {
                game.setCurrentCycleBuildingNumber(currentCycleBuildingNumber + 1);
            }

            log.debug("Current Cycle Building Number changed from {} to {}", currentCycleBuildingNumber, game.getCurrentCycleBuildingNumber());
        }
    }

    public void updateGameStageToMain(GameBean game) {
        List<List<GameUserActionCode>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());

        if (isEndOfPreparationStage(game, initialBuildingsSet.size())) {
            game.setStage(GameStage.MAIN);
            game.setDiceThrown(false);
            game.setDevelopmentCardUsed(false);
            game.setChoosePlayerToRobMandatory(false);
            game.setRoadsToBuildMandatory(0);
            for (GameUserBean gameUser : game.getGameUsers()) {
                gameUser.setKickingOffResourcesMandatory(false);
                gameUser.setAvailableTradeReply(false);
            }
            log.debug("Game Stage was changed from PREPARATION to {}", game.getStage());
        }
    }

    public void updatePreparationCycle(GameBean game) {
        if (isEndOfCycle(game)) {
            List<List<GameUserActionCode>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
            int preparationCyclesQuantity = initialBuildingsSet.size();

            if (isLastCycle(game.getPreparationCycle(), preparationCyclesQuantity)) {
                game.setPreparationCycle(null);
                log.debug("Preparation Cycle changed to {}", game.getPreparationCycle());
            } else {
                game.setPreparationCycle(game.getPreparationCycle() + 1);
                log.debug("Preparation cycle increased by 1. Current preparation cycle is {} of {}", game.getPreparationCycle(), preparationCyclesQuantity);
            }
        }
    }

    public void updateAvailableActionsForAllUsers(GameBean game) throws GameException {

        for (GameUserBean gameUser : game.getGameUsers()) {
            boolean isMandatory = false;
            List<Action> actionsList = new ArrayList<Action>();

            if (gameUser.getMoveOrder() == game.getCurrentMove()) {
                GameUserActionCode actionCode = getCurrentActionCode(game);
                Action actionToAdd;
                switch (actionCode) {
                    case BUILD_SETTLEMENT:
                        ActionOnNodeParams buildSettlementParams = new ActionOnNodeParams(actionParamsUtil.calculateBuildSettlementParams(gameUser));
                        actionToAdd = new Action(actionCode, buildSettlementParams);
                        break;
                    case BUILD_CITY:
                        ActionOnNodeParams buildCityParams = new ActionOnNodeParams(actionParamsUtil.calculateBuildCityParams(gameUser));
                        actionToAdd = new Action(actionCode, buildCityParams);
                        break;
                    case BUILD_ROAD:
                        ActionOnEdgeParams buildRoadParams = new ActionOnEdgeParams(actionParamsUtil.calculateBuildRoadParams(gameUser));
                        actionToAdd = new Action(actionCode, buildRoadParams);
                        break;
                    default:
                        actionToAdd = new Action(actionCode);
                        break;
                }
                actionsList.add(actionToAdd);
                isMandatory = true;
            }

            AvailableActions availableActions = new AvailableActions();
            availableActions.setList(actionsList);
            availableActions.setIsMandatory(isMandatory);

            String availableActionsString = GSON.toJson(availableActions, AvailableActions.class);
            gameUser.setAvailableActions(availableActionsString);
        }
    }

    private boolean isLastCycle(Integer preparationCycle, int preparationCyclesQuantity) {
        return preparationCycle.equals(preparationCyclesQuantity);
    }

    private boolean isEndOfCycle(GameBean game) {
        boolean firstPlayer = game.getCurrentMove() == 1;
        boolean lastPlayer = game.getCurrentMove() == game.getGameUsers().size();
        boolean oddCycle = game.getPreparationCycle() % 2 > 0;

        return firstPlayer && !oddCycle || lastPlayer && oddCycle;
    }

    private boolean isEndOfPreparationStage(GameBean game, int preparationCyclesQuantity) {
        return isEndOfCycle(game) && isLastCycle(game.getPreparationCycle(), preparationCyclesQuantity);
    }

    private GameUserActionCode getCurrentActionCode(GameBean game) {

        if (game.getCurrentCycleBuildingNumber() == null) {
            return GameUserActionCode.END_TURN;
        }

        List<List<GameUserActionCode>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
        int indexOfCurrentCycle = game.getPreparationCycle() - 1;
        int indexOfCurrentBuildingNumberInCycle = game.getCurrentCycleBuildingNumber() - 1;

        return initialBuildingsSet.get(indexOfCurrentCycle).get(indexOfCurrentBuildingNumberInCycle);
    }

    @Autowired
    public void setActionParamsUtil(ActionParamsUtil actionParamsUtil) {
        this.actionParamsUtil = actionParamsUtil;
    }
}
