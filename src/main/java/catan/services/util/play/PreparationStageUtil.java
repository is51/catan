package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
        if (!game.getStage().equals(GameStage.PREPARATION)) {
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
                String msgToShow;
                String username = gameUser.getUser().getUsername();
                switch (actionCode) {
                    case BUILD_SETTLEMENT:
                        ActionOnNodeParams buildSettlementParams = new ActionOnNodeParams(actionParamsUtil.calculateBuildSettlementParams(gameUser));
                        actionToAdd = new Action(actionCode, buildSettlementParams);
                        Object[] argsForBuildSettlementPattern = {gameUser.getBuildingsCount().getSettlements(), username};
                        msgToShow = getMessagePattern(gameUser, "help_msg_build_settlement").format(argsForBuildSettlementPattern);
                        break;
                    case BUILD_CITY:
                        ActionOnNodeParams buildCityParams = new ActionOnNodeParams(actionParamsUtil.calculateBuildCityParams(gameUser));
                        actionToAdd = new Action(actionCode, buildCityParams);
                        Object[] argsForBuildCityPattern = {gameUser.getBuildingsCount().getCities(), username};
                        msgToShow = getMessagePattern(gameUser, "help_msg_build_city").format(argsForBuildCityPattern);
                        break;
                    case BUILD_ROAD:
                        ActionOnEdgeParams buildRoadParams = new ActionOnEdgeParams(actionParamsUtil.calculateBuildRoadParams(gameUser));
                        actionToAdd = new Action(actionCode, buildRoadParams);
                        Object[] argsForBuildRoadPattern = {(getFirstActionCodeInCurrentCycle(game).equals("BUILD_CITY") ? 2 : 1), username};
                        msgToShow = getMessagePattern(gameUser, "help_msg_build_road").format(argsForBuildRoadPattern);
                        break;
                    default:
                        actionToAdd = new Action(actionCode);
                        msgToShow = null;
                        break;
                }
                actionsList.add(actionToAdd);
                isMandatory = true;
                gameUser.setDisplayedMessage(msgToShow);
            }

            AvailableActions availableActions = new AvailableActions();
            availableActions.setList(actionsList);
            availableActions.setIsMandatory(isMandatory);

            String availableActionsString = GSON.toJson(availableActions, AvailableActions.class);
            gameUser.setAvailableActions(availableActionsString);
        }
    }

    private MessageFormat getMessagePattern(GameUserBean gameUser, String key) {
        ResourceBundle messagesBundle = gameUser.getUser().getMsgs();
        return new MessageFormat(messagesBundle.getString(key));
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

    private String getFirstActionCodeInCurrentCycle(GameBean game) {
        List<List<GameUserActionCode>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
        int indexOfCurrentCycle = game.getPreparationCycle() - 1;

        return initialBuildingsSet.get(indexOfCurrentCycle).get(0).toString();
    }

    public void distributeResourcesForLastBuilding(NodeBean nodeToBuildOn) {
        GameBean game = nodeToBuildOn.getGame();
        List<List<GameUserActionCode>> initialBuildingsSet = toInitialBuildingsSetFromJson(game.getInitialBuildingsSet());

        if (game.getPreparationCycle() < initialBuildingsSet.size()) {
            // Currently it is not last preparation cycle
            return;
        }

        List<GameUserActionCode> buildingsInLastPreparationCycle = initialBuildingsSet.get(game.getPreparationCycle() - 1);

        //find last distributable building in last cycle
        Integer numberOfLastDistributableBuildingInLastCycle = buildingsInLastPreparationCycle.size();
        while(buildingsInLastPreparationCycle.get(numberOfLastDistributableBuildingInLastCycle - 1).equals(GameUserActionCode.BUILD_ROAD)){
            numberOfLastDistributableBuildingInLastCycle--;
        }

        if(!game.getCurrentCycleBuildingNumber().equals(numberOfLastDistributableBuildingInLastCycle)){
           // User builds not last Settlement or City in last cycle, should not distribute resources from hexes
           return;
        }

        log.debug("User builds last distributable building {} and will get resources from neighbour hexes", nodeToBuildOn.getBuilding().getBuilt());

        for(HexBean sourceHex : nodeToBuildOn.getHexes().listAllNotNullItems()){
            if(sourceHex.getResourceType().equals(HexType.EMPTY)){
                continue;
            }

            ResourceUtil.produceResources(sourceHex, nodeToBuildOn.getBuilding(), log);
        }
    }

    @Autowired
    public void setActionParamsUtil(ActionParamsUtil actionParamsUtil) {
        this.actionParamsUtil = actionParamsUtil;
    }
}