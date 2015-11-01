package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameUserActionCode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PreparationStageUtil {
    private Logger log = LoggerFactory.getLogger(PreparationStageUtil.class);

    private static final Gson GSON = new Gson();

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

    public void updateCurrentCycleBuildingNumber(GameBean game) {
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
        boolean lastCycle = game.getPreparationCycle().equals(initialBuildingsSet.size());

        if (lastCycle && isEndOfCycle(game)) {
            game.setStage(GameStage.MAIN);
            log.debug("Game Stage was changed from PREPARATION to {}", game.getStage());
        }
    }

    public void updatePreparationCycle(GameBean game) {
        if (isEndOfCycle(game)) {
            List<List<GameUserActionCode>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
            boolean lastCycle = game.getPreparationCycle().equals(initialBuildingsSet.size());
            if (lastCycle) {
                game.setPreparationCycle(null);
                log.debug("Preparation Cycle changed to {}", game.getPreparationCycle());
            } else {
                game.setPreparationCycle(game.getPreparationCycle() + 1);
                log.debug("Preparation cycle increased by 1. Current preparation cycle is {} of {}", game.getPreparationCycle(), initialBuildingsSet.size());
            }
        }
    }

    public void updateAvailableUserActions(GameBean game) throws GameException {

        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getMoveOrder() == game.getCurrentMove()) {

                List<GameUserActionCode> actionCodesList = new ArrayList<GameUserActionCode>();
                actionCodesList.add(getCurrentInitialAction(game));

                List<Action> actionsList = new ArrayList<Action>();
                for (GameUserActionCode actionCode : actionCodesList) {
                    actionsList.add(new Action(actionCode));
                }

                AvailableActions availableActions = new AvailableActions();
                availableActions.setList(actionsList);
                availableActions.setIsMandatory(true);

                String availableActionsString = GSON.toJson(availableActions, AvailableActions.class);
                gameUser.setAvailableActions(availableActionsString);
            } else {
                gameUser.setAvailableActions("{\"list\": [], \"isMandatory\": false}");
            }
        }
    }

    private boolean isEndOfCycle(GameBean game) {
        boolean firstPlayer = game.getCurrentMove() == 1;
        boolean lastPlayer = game.getCurrentMove() == game.getGameUsers().size();
        boolean oddCycle = game.getPreparationCycle() % 2 > 0;

        return firstPlayer && !oddCycle || lastPlayer && oddCycle;
    }

    private GameUserActionCode getCurrentInitialAction(GameBean game) {

        if (game.getCurrentCycleBuildingNumber() == null) {
            return GameUserActionCode.END_TURN;
        }

        List<List<GameUserActionCode>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
        int indexOfCycle = game.getPreparationCycle() - 1;
        int indexOfBuildingNumberInCycle = game.getCurrentCycleBuildingNumber() - 1;

        return initialBuildingsSet.get(indexOfCycle).get(indexOfBuildingNumberInCycle);
    }
}
