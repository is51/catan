package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.user.UserBean;
import catan.services.util.game.GameUtil;
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
    private GameUtil gameUtil;

    private static final Gson GSON = new Gson();

    public AvailableActions toAvailableActionsFromJson(String availableActionsJson) {
        return GSON.fromJson(availableActionsJson, AvailableActions.class);
    }

    public void updateNextMove(GameBean game) throws GameException {
        switch (game.getStage()) {
            case PREPARATION:
                preparationStageUtil.updateNextMove(game);
                break;
            case MAIN:
                mainStageUtil.updateNextMove(game);
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", game.getStage());
                throw new GameException(ERROR_CODE_ERROR);
        }
    }

    public void updateAvailableUserActions(GameBean game) throws GameException {
        switch (game.getStage()) {
            case PREPARATION:
                preparationStageUtil.updateAvailableUserActions(game);
                break;
            case MAIN:
                mainStageUtil.updateAvailableUserActions(game);
                break;
            default:
                log.debug("Cannot recognize current game stage: {}", game.getStage());
                throw new GameException(ERROR_CODE_ERROR);
        }
    }

    public void updateVictoryPoints(UserBean user, GameBean game) throws GameException {
        GameUserBean gameUserBean = gameUtil.getGameUserJoinedToGame(user, game);

        int settlementsCount = gameUserBean.getBuildingsCount().getSettlements();
        int citiesCount = gameUserBean.getBuildingsCount().getCities();

        gameUserBean.getAchievements().setDisplayVictoryPoints(settlementsCount + citiesCount * 2);
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

    @Autowired
    public void setGameUtil(GameUtil gameUtil) {
        this.gameUtil = gameUtil;
    }
}
