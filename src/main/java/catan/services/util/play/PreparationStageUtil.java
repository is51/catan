package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.transfer.output.game.actions.Action;
import catan.domain.transfer.output.game.actions.AvailableActions;
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

    public List<List<String>> toInitialBuildingsSetFromJson(String initialBuildingsSetJson) {
        return GSON.fromJson(initialBuildingsSetJson, new TypeToken<List<List<String>>>() {
        }.getType());
    }

    public void updateNextMoveInPreparationStage(GameBean game) {
        Integer nextMoveNumber;

        if (game.getPreparationCycle() == null) {
            nextMoveNumber = 1;
        } else {
            Integer currentMove = game.getCurrentMove();
            if(game.getPreparationCycle() % 2 > 0){
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
        List<List<String>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
        Integer numberOfBuildingsInCycle = initialBuildingsSet.get(game.getPreparationCycle() - 1).size();

        if (game.getCurrentCycleBuildingNumber().equals(numberOfBuildingsInCycle)) {
            game.setCurrentCycleBuildingNumber(null);
        } else if (game.getCurrentCycleBuildingNumber() == null) {
                game.setCurrentCycleBuildingNumber(1);
            } else {
                game.setCurrentCycleBuildingNumber(game.getCurrentCycleBuildingNumber() + 1);
            }
        log.debug("Current Cycle Building Number changed to {}", game.getCurrentCycleBuildingNumber());
    }

    public void updateGameStageToMain(GameBean game) {
        List<List<String>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
        boolean lastCycle = game.getPreparationCycle().equals(initialBuildingsSet.size());

        if (lastCycle) {
            game.setStage(GameStage.MAIN);
            log.debug("Game Stage was changed from PREPARATION to {}", game.getStage());
        }
    }

    public void updatePreparationCycle(GameBean game) {
        if (isEndOfCycle(game)) {
            List<List<String>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
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

    public void updateAvailableUserActionsInPreparationStage(GameBean game) throws GameException {

        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getMoveOrder() == game.getCurrentMove()) {

                List<GameUserActionCode> actionCodesList = getListOfActionCodes(game);
                AvailableActions availableActions = new AvailableActions();
                List<Action> actionsList = new ArrayList<Action>();

                for (GameUserActionCode actionCode : actionCodesList) {
                    actionsList.add(new Action(actionCode));
                }
                availableActions.setList(actionsList);
                availableActions.setIsMandatory(true);
                String availableActionsString = GSON.toJson(availableActions, AvailableActions.class);
                gameUser.setAvailableActions(availableActionsString);
            } else {
                gameUser.setAvailableActions("{\"list\": [], \"isMandatory\": false}");
            }
        }
    }

    private List<GameUserActionCode> getListOfActionCodes (GameBean game) {

        List<GameUserActionCode> actionCodesList = new ArrayList<GameUserActionCode>();
        if (userBuiltAllBuildingsInCurrentCycle(game)) {
            actionCodesList.add(GameUserActionCode.END_TURN);
            //set end turn mandatory
        } else if (getCurrentInitialBuilding(game).equals("ROAD")) {
            actionCodesList.add(GameUserActionCode.BUILD_ROAD);
            //set build road mandatory
        } else if (getCurrentInitialBuilding(game).equals("SETTLEMENT")) {
            actionCodesList.add(GameUserActionCode.BUILD_SETTLEMENT);
            //set build settlement mandatory
        } else if (getCurrentInitialBuilding(game).equals("CITY")) {
            actionCodesList.add(GameUserActionCode.BUILD_CITY);
            //set build city mandatory
        }
        return actionCodesList;
    }

    private boolean isEndOfCycle(GameBean game) {
        boolean firstPlayer = game.getCurrentMove() == 1;
        boolean lastPlayer = game.getCurrentMove() == game.getGameUsers().size();
        boolean oddCycle = game.getPreparationCycle() % 2 > 0;

        return firstPlayer && !oddCycle || lastPlayer && oddCycle;
    }

    private boolean userBuiltAllBuildingsInCurrentCycle(GameBean game) {
        return game.getCurrentCycleBuildingNumber() == null;
    }

    private String getCurrentInitialBuilding(GameBean game) {
        List<List<String>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
        int indexOfCycle = game.getPreparationCycle() - 1;
        int indexOfBuildingNumberInCycle = game.getCurrentCycleBuildingNumber() - 1;
        return initialBuildingsSet.get(indexOfCycle).get(indexOfBuildingNumberInCycle);
    }
}
