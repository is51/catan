package catan.services.util.play;

import catan.domain.model.game.GameBean;
import catan.domain.model.game.types.GameStage;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class EndTurnUtil {
    private Logger log = LoggerFactory.getLogger(EndTurnUtil.class);

    public Integer endTurnImplInPreparationStage(GameBean game, List<List<String>> initialBuildingsSet) {
        Integer nextMoveNumber;
        Integer preparationCycle = game.getPreparationCycle();
        game.setCurrentCycleBuildingNumber(1);
        if (isFirstMove(game) && !isOddCycle(preparationCycle) || isLastMove(game) && isOddCycle(preparationCycle)) {
            if (preparationCycle == initialBuildingsSet.size()) {
                game.setStage(GameStage.MAIN);
                game.setPreparationCycle(null);
                log.debug("Game Stage was changed from PREPARATION to {}", game.getStage());
                nextMoveNumber = 1;
            } else {
                nextMoveNumber = getNextMoveInPreparationStage(game);
                game.setPreparationCycle(preparationCycle + 1);
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

    public Integer getNextMoveInPreparationStage(GameBean game) {
        Integer currentMove = game.getCurrentMove();

        if(game.getPreparationCycle() % 2 > 0){
            return currentMove.equals(game.getGameUsers().size()) ? currentMove : currentMove + 1;
        } else {
            return currentMove.equals(1) ? 1 : currentMove - 1;
        }
    }

    private boolean isOddCycle(Integer preparationCycle) {
        return preparationCycle % 2 > 0;
    }

    private boolean isFirstMove(GameBean game) {
        return game.getCurrentMove() == 1;
    }

    private boolean isLastMove(GameBean game) {
        return game.getCurrentMove() == game.getGameUsers().size();
    }
}
