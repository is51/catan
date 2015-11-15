package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.types.GameUserActionCode;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainStageUtil {
    private Logger log = LoggerFactory.getLogger(MainStageUtil.class);

    private static final Gson GSON = new Gson();

    public void updateNextMove(GameBean game) {
        Integer nextMoveNumber = game.getCurrentMove().equals(game.getGameUsers().size())
                ? 1
                : game.getCurrentMove() + 1;

        log.debug("Next move order in {} stage is changing from {} to {}", game.getStage(), game.getCurrentMove(), nextMoveNumber);
        game.setCurrentMove(nextMoveNumber);
    }

    public void produceResources(GameBean game) {
        for (HexBean hex : game.getHexesWithCurrentDiceValue()) {
            for (NodeBean node : hex.getNodesWithBuildings()) {
                Resources userResources = node.getBuilding().getBuildingOwner().getResources();
                int currentResourceQuantity = userResources.getResource(hex.getResourceType());
                int resourceQuantityToAdd = node.getBuilding().getBuilt().equals(NodeBuiltType.SETTLEMENT) ? 1 : 2;
                userResources.setResource(hex.getResourceType(), currentResourceQuantity + resourceQuantityToAdd);
            }
        }
    }

    public void updateAvailableActionsForAllUsers(GameBean game) throws GameException {
        for (GameUserBean gameUser : game.getGameUsers()) {
            updateAvailableActionsForUser(gameUser, game);
        }
    }

    private void updateAvailableActionsForUser(GameUserBean gameUser, GameBean game) {
        List<Action> actionsList = new ArrayList<Action>();

        allowBuildSettlement(gameUser, game, actionsList);
        allowBuildCity(gameUser, game, actionsList);
        allowBuildRoad(gameUser, game, actionsList);
        allowEndTurn(gameUser, game, actionsList);
        allowThrowDice(gameUser, game, actionsList);

        AvailableActions availableActions = new AvailableActions();
        availableActions.setList(actionsList);
        availableActions.setIsMandatory(false);

        String availableActionsString = GSON.toJson(availableActions, AvailableActions.class);
        gameUser.setAvailableActions(availableActionsString);
    }

    private void allowThrowDice(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game) 
                && isCurrentUsersMove(gameUser, game) 
                && !game.isDiceThrown()) {
            actionsList.add(new Action(GameUserActionCode.THROW_DICE));
        }
    }

    private void allowEndTurn(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game) 
                && isCurrentUsersMove(gameUser, game) 
                && game.isDiceThrown()) {
            actionsList.add(new Action(GameUserActionCode.END_TURN));
        }
    }

    private void allowBuildCity(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game) 
                && isCurrentUsersMove(gameUser, game) 
                && game.isDiceThrown() 
                && userHasResourcesToBuildCity(gameUser)) {
            actionsList.add(new Action(GameUserActionCode.BUILD_CITY));
        }
    }

    private void allowBuildSettlement(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game) 
                && isCurrentUsersMove(gameUser, game) 
                && game.isDiceThrown() 
                && userHasResourcesForSettlement(gameUser)) {
            actionsList.add(new Action(GameUserActionCode.BUILD_SETTLEMENT));
        }
    }

    private void allowBuildRoad(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game) 
                && isCurrentUsersMove(gameUser, game) 
                && game.isDiceThrown() 
                && userHasResourcesToBuildRoad(gameUser)) {
            actionsList.add(new Action(GameUserActionCode.BUILD_ROAD));
        }
    }

    private boolean userHasResourcesToBuildCity(GameUserBean gameUser) {
        return gameUser.getResources().getStone() >= 0
                && gameUser.getResources().getWheat() >= 0;
    }

    private boolean userHasResourcesForSettlement(GameUserBean gameUser) {
        return gameUser.getResources().getWood() >= 0
                && gameUser.getResources().getBrick() >= 0
                && gameUser.getResources().getSheep() >= 0
                && gameUser.getResources().getWheat() >= 0;
    }

    private boolean userHasResourcesToBuildRoad(GameUserBean gameUser) {
        return gameUser.getResources().getWood() >= 0
                && gameUser.getResources().getBrick() >= 0;
    }

    private boolean isCurrentUsersMove(GameUserBean gameUser, GameBean game) {
        return gameUser.getMoveOrder() == game.getCurrentMove();
    }
    
    private boolean gameNotFinished(GameBean game) {
        return !GameStatus.FINISHED.equals(game.getStatus());
    }
}
