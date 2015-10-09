package catan.services.util.play;

import catan.domain.model.game.GameBean;
import catan.domain.model.game.types.GameStage;
import catan.services.util.game.GameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class EndTurnUtil {
    private Logger log = LoggerFactory.getLogger(EndTurnUtil.class);

    private GameUtil gameUtil;

    public Integer endTurnImplInPreparationStage(GameBean game) {
        Integer nextMoveNumber;
        List<List<String>> initialBuildingsSet = gameUtil.getInitialBuildingsSetFromJson(game.getInitialBuildingsSet());
        if (isCycleFinished(game)) {
            if (isLastCycle(game, initialBuildingsSet.size())) {
                game.setStage(GameStage.MAIN);
                game.setPreparationCycle(null);
                game.setCurrentCycleBuildingNumber(0);
                log.debug("Game Stage was changed from PREPARATION to {}", game.getStage());
                nextMoveNumber = 1;
            } else {
                nextMoveNumber = getNextMoveInPreparationStage(game);
                game.setPreparationCycle(game.getPreparationCycle() + 1);
                log.debug("Preparation cycle increased by 1. Current preparation cycle is {} of {}", game.getPreparationCycle(), initialBuildingsSet.size());
            }
        } else {
            nextMoveNumber = getNextMoveInPreparationStage(game);
        }
        return nextMoveNumber;
    }

    public int endTurnImplInMainStage(GameBean game) {
        return game.getCurrentMove().equals(game.getGameUsers().size())
                ? 1
                : game.getCurrentMove() + 1;
    }

    private Integer getNextMoveInPreparationStage(GameBean game) {
        Integer currentMove = game.getCurrentMove();

        if(game.getPreparationCycle() % 2 > 0){
            return currentMove.equals(game.getGameUsers().size()) ? currentMove : currentMove + 1;
        } else {
            return currentMove.equals(1) ? 1 : currentMove - 1;
        }
    }

    private boolean isCycleFinished(GameBean game) {
        Integer preparationCycle = game.getPreparationCycle();
        return isFirstPlayer(game) && !isOddCycle(preparationCycle) || isLastPlayer(game) && isOddCycle(preparationCycle);
    }

    private boolean isLastCycle(GameBean game, Integer initialBuildingsSet) {
        return game.getPreparationCycle().equals(initialBuildingsSet);
    }

    private boolean isOddCycle(Integer preparationCycle) {
        return preparationCycle % 2 > 0;
    }

    private boolean isFirstPlayer(GameBean game) {
        return game.getCurrentMove() == 1;
    }

    private boolean isLastPlayer(GameBean game) {
        return game.getCurrentMove() == game.getGameUsers().size();
    }

    @Autowired
    public void setGameUtil(GameUtil gameUtil) {
        this.gameUtil = gameUtil;
    }
}