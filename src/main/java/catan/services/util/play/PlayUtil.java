package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameUserActions;
import catan.domain.transfer.output.game.AllAvailableActionsDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static catan.services.impl.GameServiceImpl.ERROR_CODE_ERROR;

@Component
public class PlayUtil {
    private Logger log = LoggerFactory.getLogger(PlayUtil.class);

    private EndTurnUtil endTurnUtil;

    private static final Gson GSON = new Gson();

    public List<List<String>> getInitialBuildingsSetFromJson(String json) {
        return GSON.fromJson(json, new TypeToken<List<List<String>>>() {
        }.getType());
    }

    public AllAvailableActionsDetails getAllAvailableActions(String json) {
        return GSON.fromJson(json, AllAvailableActionsDetails.class);
    }

    public void updateNextMoveOrder(GameBean game) throws GameException {
        Integer nextMoveNumber;
        switch (game.getStage()) {
            case PREPARATION:
                List<List<String>> initialBuildingsSet = getInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
                nextMoveNumber = endTurnUtil.endTurnImplInPreparationStage(game, initialBuildingsSet);
                break;
            case MAIN:
                nextMoveNumber = endTurnUtil.endTurnImplInMainStage(game);
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", game.getStage());
                throw new GameException(ERROR_CODE_ERROR);
        }

        log.debug("Next move order in {} stage is changing from {} to {}", game.getStage(), game.getCurrentMove(), nextMoveNumber);
        game.setCurrentMove(nextMoveNumber);
    }

    public void updateCurrentCycleBuildingNumber(GameBean game) {
        if (game.getStage().equals(GameStage.PREPARATION)) {
            List<List<String>> initialBuildingsSet = getInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
            if (game.getCurrentCycleBuildingNumber() > initialBuildingsSet.get(game.getPreparationCycle() - 1).size()) {
                game.setCurrentCycleBuildingNumber(1);
            } else {
                game.setCurrentCycleBuildingNumber(game.getCurrentCycleBuildingNumber() + 1);
            }
        }
    }
    public void updateAvailableUserActions(GameBean game) throws GameException {
        boolean isMandatory = false;
        List<String> actionsList = new ArrayList<String>();
        List<GameUserActions> actionCode = new ArrayList<GameUserActions>();
        switch (game.getStage()) {
            case PREPARATION:
                List<List<String>> initialBuildingsSet = getInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
                for (GameUserBean gameUser : game.getGameUsers()) {
                    if (gameUser.getMoveOrder() == game.getCurrentMove()) {
                        actionCode = getActionCodeInPreparationStage(game, initialBuildingsSet);
                    }
                    if (!actionCode.isEmpty()) {
                        actionsList.add("{\"code\": \"" + actionCode.get(0).toString() + "\", \"params\": {}}");
                        gameUser.setAvailableActions("{\"list\": [" + actionsList.get(0) + "], \"isMandatory\": true}");
                    } else {
                        gameUser.setAvailableActions("{\"list\": [], \"isMandatory\": false}");
                    }
                    actionCode.clear();
                    actionsList.clear();
                }
                break;
            case MAIN:
                //TODO: complete this part when developing main stage part
                for (GameUserBean gameUser : game.getGameUsers()) {
                    if (gameUser.getMoveOrder() == game.getCurrentMove()) {
                        gameUser.setAvailableActions("{\"list\": [], \"isMandatory\": false}");
                    } else {
                        gameUser.setAvailableActions("{\"list\": [], \"isMandatory\": false}");
                        //non-active users have not any available actions (trade in future)
                    }
                }
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", game.getStage());
                throw new GameException(ERROR_CODE_ERROR);
        }
    }

    private List<GameUserActions> getActionCodeInPreparationStage (GameBean game, List<List<String>> initialBuildingsSet) {
        List<GameUserActions> actionCode = new ArrayList<GameUserActions>();
        if (isUserBuiltAllBuildingsInCurrentCycle(game, initialBuildingsSet)) {
            actionCode.add(GameUserActions.END_TURN);
            //set end turn mandatory
        } else if (getCurrentInitialBuilding(game, initialBuildingsSet).equals("ROAD")) {
            actionCode.add(GameUserActions.BUILD_ROAD);
            //set build road mandatory
        } else if (getCurrentInitialBuilding(game, initialBuildingsSet).equals("SETTLEMENT")) {
            actionCode.add(GameUserActions.BUILD_SETTLEMENT);
            //set build settlement mandatory
        } else if (getCurrentInitialBuilding(game, initialBuildingsSet).equals("CITY")) {
            actionCode.add(GameUserActions.BUILD_CITY);
            //set build city mandatory
        }
        return actionCode;
    }

    private boolean isUserBuiltAllBuildingsInCurrentCycle(GameBean game, List<List<String>> initialBuildingsSet) {
        return game.getCurrentCycleBuildingNumber() > initialBuildingsSet.get(game.getPreparationCycle() - 1).size();
    }

    private String getCurrentInitialBuilding(GameBean game, List<List<String>> initialBuildingsSet) {
        return initialBuildingsSet.get(game.getPreparationCycle() - 1).get(game.getCurrentCycleBuildingNumber() - 1);
    }

    @Autowired
    public void setEndTurnUtil(EndTurnUtil endTurnUtil) {
        this.endTurnUtil = endTurnUtil;
    }
}
