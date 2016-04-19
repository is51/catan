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
        Integer nextMoveNumber = game.getCurrentMove();

        if (isEndOfCycle(game)) {
            game.setNeedToUpdatePreparationCycle(true);
            nextMoveNumber = isLastCycle(game)
                    ? 1
                    : nextMoveNumber;
        } else {
            nextMoveNumber = game.getPreparationCycle() % 2 > 0
                    ? ++nextMoveNumber
                    : --nextMoveNumber;
        }

        if (!nextMoveNumber.equals(game.getCurrentMove())) {
            log.debug("Next move order in {} stage is changing from {} to {}", game.getStage(), game.getCurrentMove(), nextMoveNumber);
            game.setCurrentMove(nextMoveNumber);
        }
    }

    public void updateCurrentCycleInitialBuildingNumber(GameBean game) {
        if (game.getPreparationCycle() == null) {
            game.setCurrentCycleBuildingNumber(1);
            return;
        }
        List<List<GameUserActionCode>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
        Integer quantityOfBuildingsInCycle = initialBuildingsSet.get(game.getPreparationCycle() - 1).size();
        Integer currentCycleBuildingNumber = game.getCurrentCycleBuildingNumber();

        if (quantityOfBuildingsInCycle.equals(currentCycleBuildingNumber)) {
            game.setCurrentCycleBuildingNumber(null);
        } else if (currentCycleBuildingNumber == null) {
            game.setCurrentCycleBuildingNumber(1);
        } else {
            game.setCurrentCycleBuildingNumber(currentCycleBuildingNumber + 1);
        }

        log.debug("Current Cycle Building Number changed from {} to {}", currentCycleBuildingNumber, game.getCurrentCycleBuildingNumber());
    }

    public void updatePreparationCycle(GameBean game) {
        Integer preparationCycle = game.getPreparationCycle();
        if (preparationCycle == null) {
            game.setPreparationCycle(1);
            return;
        }

        if (!game.isNeedToUpdatePreparationCycle()) {
            return;
        }

        if (GameStage.MAIN.equals(game.getStage())) {
            game.setPreparationCycle(null);
            log.debug("Preparation Cycle changed to {}", preparationCycle);
        } else {
            game.setPreparationCycle(preparationCycle + 1);
            log.debug("Preparation cycle increased by 1. Current preparation cycle is {}", game.getPreparationCycle());
        }
    }

    public void updateAvailableActionsForAllUsers(GameBean game) throws GameException {
        updateCurrentCycleInitialBuildingNumber(game);
        updatePreparationCycle(game);

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
                        MessagesUtil.updateDisplayedMsg(gameUser, "help_msg_build_settlement");
                        break;
                    case BUILD_CITY:
                        ActionOnNodeParams buildCityParams = new ActionOnNodeParams(actionParamsUtil.calculateBuildCityParams(gameUser));
                        actionToAdd = new Action(actionCode, buildCityParams);
                        MessagesUtil.updateDisplayedMsg(gameUser, "help_msg_build_city");
                        break;
                    case BUILD_ROAD:
                        ActionOnEdgeParams buildRoadParams = new ActionOnEdgeParams(actionParamsUtil.calculateBuildRoadParams(gameUser));
                        actionToAdd = new Action(actionCode, buildRoadParams);
                        MessagesUtil.updateDisplayedMsg(gameUser, "help_msg_build_road");
                        break;
                    default:
                        actionToAdd = new Action(actionCode);
                        gameUser.setDisplayedMessage(null);
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

    public boolean isLastCycle(GameBean game) {
        List<List<GameUserActionCode>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
        int preparationCyclesQuantity = initialBuildingsSet.size();
        return game.getPreparationCycle().equals(preparationCyclesQuantity);
    }

    public boolean isEndOfCycle(GameBean game) {
        boolean firstPlayer = game.getCurrentMove() == 1;
        boolean lastPlayer = game.getCurrentMove() == game.getGameUsers().size();
        boolean oddCycle = game.getPreparationCycle() % 2 > 0;

        return firstPlayer && !oddCycle || lastPlayer && oddCycle;
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