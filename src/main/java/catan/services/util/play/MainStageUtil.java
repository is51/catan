package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
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

    public void updateAvailableUserActions(GameBean game) throws GameException {

        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getMoveOrder() == game.getCurrentMove() || !GameStatus.FINISHED.equals(game.getStatus())) {

                AvailableActions availableActions = new AvailableActions();
                List<Action> actionsList = new ArrayList<Action>();

                actionsList.add(new Action(GameUserActionCode.BUILD_SETTLEMENT));
                actionsList.add(new Action(GameUserActionCode.BUILD_ROAD));
                actionsList.add(new Action(GameUserActionCode.BUILD_CITY));
                actionsList.add(new Action(GameUserActionCode.END_TURN));

                availableActions.setList(actionsList);
                availableActions.setIsMandatory(false);
                String availableActionsString = GSON.toJson(availableActions, AvailableActions.class);
                gameUser.setAvailableActions(availableActionsString);
            } else {
                gameUser.setAvailableActions("{\"list\": [], \"isMandatory\": false}");
            }
        }
    }
}
