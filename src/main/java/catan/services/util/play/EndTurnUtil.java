package catan.services.util.play;

import catan.domain.model.game.GameBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EndTurnUtil {
    private Logger log = LoggerFactory.getLogger(EndTurnUtil.class);

    public void calculateNextMove(GameBean game) {
        Integer nextMoveNumber = null;
        switch (game.getStage()) {
            case PREPARATION:
                nextMoveNumber = getNextMoveInPreparationStage(game);
                break;
            case MAIN:
                nextMoveNumber = getNextMoveInMainStage(game);
                break;
        }
        log.debug("Next move order calculated in {} stage is: {}", game.getStage(), nextMoveNumber);

        game.setCurrentMove(nextMoveNumber);
    }

    private Integer getNextMoveInPreparationStage(GameBean game) {
        Integer currentMove = game.getCurrentMove();

        if(game.getPreparationCycle() % 2 > 0){
            return currentMove.equals(game.getGameUsers().size()) ? currentMove : currentMove + 1;
        } else {
            return currentMove.equals(1) ? 1 : currentMove - 1;
        }
    }

    private Integer getNextMoveInMainStage(GameBean game) {
        return game.getCurrentMove().equals(game.getGameUsers().size())
                ? 1
                : game.getCurrentMove() + 1;
    }
}
