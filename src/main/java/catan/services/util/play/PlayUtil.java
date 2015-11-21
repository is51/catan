package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.types.GameUserActionCode;
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
    private MainStageUtil mainStageUtil;

    private static final Gson GSON = new Gson();

    public AvailableActions toAvailableActionsFromJson(String availableActionsJson) {
        return GSON.fromJson(availableActionsJson, AvailableActions.class);
    }

    public void updateAvailableActionsForAllUsers(GameBean game) throws GameException {
        switch (game.getStage()) {
            case PREPARATION:
                preparationStageUtil.updateAvailableActionsForAllUsers(game);
                break;
            case MAIN:
                mainStageUtil.updateAvailableActionsForAllUsers(game);
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", game.getStage());
                throw new GameException(ERROR_CODE_ERROR);
        }
    }

    public void takeResources(GameStage gameStage, Resources usersResources, GameUserActionCode action) {
        if (GameStage.MAIN.equals(gameStage)) {
            mainStageUtil.takeResources(usersResources, action);
        }
    }

    public void finishGameIfTargetVictoryPointsReached(GameUserBean gameUser, GameBean game) {
        if (gameUser.getMoveOrder() == game.getCurrentMove()) {
            int realVictoryPoints = gameUser.getDevelopmentCards().getVictoryPoint() + gameUser.getAchievements().getDisplayVictoryPoints();
            if (realVictoryPoints >= game.getTargetVictoryPoints()) {
                game.setStatus(GameStatus.FINISHED);
            }
        }
    }

    public void updateVictoryPoints(GameUserBean gameUser) throws GameException {

        int settlementsCount = gameUser.getBuildingsCount().getSettlements();
        int citiesCount = gameUser.getBuildingsCount().getCities();

        gameUser.getAchievements().setDisplayVictoryPoints(settlementsCount + citiesCount * 2);
        //points =  settlementsCount + cityCount * 2 + ((isOwnerWay) ? 2 : 0 ) +  ((isOwnerArmy) ? 2 : 0 );
    }

    @Autowired
    public void setPreparationStageUtil(PreparationStageUtil preparationStageUtil) {
        this.preparationStageUtil = preparationStageUtil;
    }

    @Autowired
    public void setMainStageUtil(MainStageUtil mainStageUtil) {
        this.mainStageUtil = mainStageUtil;
    }
}
