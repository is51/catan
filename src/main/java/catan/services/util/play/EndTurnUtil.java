package catan.services.util.play;

import catan.domain.model.game.GameBean;
import org.springframework.stereotype.Component;

@Component
public class EndTurnUtil {

    public Integer getNextMoveInPreparationStage(GameBean game) {
        Integer currentMove = game.getCurrentMove();

        if(game.getPreparationCycle() % 2 > 0){
            return currentMove.equals(game.getGameUsers().size()) ? currentMove : currentMove + 1;
        } else {
            return currentMove.equals(1) ? 1 : currentMove - 1;
        }
    }

    public Integer getNextMoveInMainStage(GameBean game) {
        return game.getCurrentMove().equals(game.getGameUsers().size())
                ? 1
                : game.getCurrentMove() + 1;
    }
}
