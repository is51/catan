package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
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
        if (gameUser.getMoveOrder() == game.getCurrentMove() && !game.isDiceThrown()) {
            //actionsList.add(new Action(GameUserActionCode.THROW_DICE));
        }
    }

    private void allowEndTurn(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameUser.getMoveOrder() == game.getCurrentMove()
                //&& game.isDiceThrown()
                ) {
            actionsList.add(new Action(GameUserActionCode.END_TURN));
        }
    }

    private void allowBuildCity(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameUser.getMoveOrder() == game.getCurrentMove()
                //&& game.isDiceThrown()
                && gameUser.getResources().getStone() >= 0
                && gameUser.getResources().getWheat() >= 0
                ) {
            actionsList.add(new Action(GameUserActionCode.BUILD_CITY));
        }
    }

    private void allowBuildSettlement(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameUser.getMoveOrder() == game.getCurrentMove()
                //&& game.isDiceThrown()
                && gameUser.getResources().getWood() >= 0
                && gameUser.getResources().getBrick() >= 0
                && gameUser.getResources().getSheep() >= 0
                && gameUser.getResources().getWheat() >= 0
                ) {
            actionsList.add(new Action(GameUserActionCode.BUILD_SETTLEMENT));
        }
    }

    private void allowBuildRoad(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameUser.getMoveOrder() == game.getCurrentMove()
                //&& game.isDiceThrown()
                && gameUser.getResources().getWood() >= 0
                && gameUser.getResources().getBrick() >= 0
                ) {
            actionsList.add(new Action(GameUserActionCode.BUILD_ROAD));
        }
    }
}
