package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.actions.AvailableActions;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static catan.services.impl.GameServiceImpl.ERROR_CODE_ERROR;

@Component
public class PlayUtil {
    private Logger log = LoggerFactory.getLogger(PlayUtil.class);

    private PreparationStageUtil preparationStageUtil;

    private static final Gson GSON = new Gson();

    public AvailableActions toAvailableActionsFromJson(String availableActionsJson) {
        return GSON.fromJson(availableActionsJson, AvailableActions.class);
    }

    public void updateNextMove(GameBean game) throws GameException {
        switch (game.getStage()) {
            case PREPARATION:
                preparationStageUtil.updateNextMoveInPreparationStage(game);
                break;
            case MAIN:
                updateNextMoveInMainStage(game);
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", game.getStage());
                throw new GameException(ERROR_CODE_ERROR);
        }
    }

    public void updateNextMoveInMainStage(GameBean game) {
        Integer nextMoveNumber = game.getCurrentMove().equals(game.getGameUsers().size())
                ? 1
                : game.getCurrentMove() + 1;

        log.debug("Next move order in {} stage is changing from {} to {}", game.getStage(), game.getCurrentMove(), nextMoveNumber);
        game.setCurrentMove(nextMoveNumber);
    }

    public void updateAvailableUserActions(GameBean game) throws GameException {
        switch (game.getStage()) {
            case PREPARATION:
                preparationStageUtil.updateAvailableUserActions(game);
                break;
            case MAIN:
                //TODO: complete this part when developing main stage part
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", game.getStage());
                throw new GameException(ERROR_CODE_ERROR);
        }
    }

    @Autowired
    public void setPreparationStageUtil(PreparationStageUtil preparationStageUtil) {
        this.preparationStageUtil = preparationStageUtil;
    }
}
