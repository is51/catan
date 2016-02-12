package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.types.GameStatus;
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

    public void finishGameIfTargetVictoryPointsReached(GameUserBean gameUser, GameBean game) {
        if (gameUser.getMoveOrder() == game.getCurrentMove()) {
            int realVictoryPoints = gameUser.getDevelopmentCards().getVictoryPoint() + gameUser.getAchievements().getDisplayVictoryPoints();
            if (realVictoryPoints >= game.getTargetVictoryPoints()) {
                game.setStatus(GameStatus.FINISHED);
            }
        }
    }

    public void updateAchievements(GameBean game) throws GameException {
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (game.getCurrentMove().equals(gameUser.getMoveOrder())) {
                updateTotalDevCards(gameUser);
            }
            updateVictoryPoints(gameUser, game);
            updateTotalResources(gameUser);
        }
    }

    private void updateTotalDevCards(GameUserBean gameUser) {
        int totalCards = gameUser.getDevelopmentCards().calculateSum();
        gameUser.getAchievements().setTotalCards(totalCards);
    }

    private void updateTotalResources(GameUserBean gameUser) {
        int totalResources = gameUser.getResources().calculateSum();
        gameUser.getAchievements().setTotalResources(totalResources);
    }

    private void updateVictoryPoints(GameUserBean gameUser, GameBean game) throws GameException {

        int settlementsCount = gameUser.getBuildingsCount().getSettlements();
        int citiesCount = gameUser.getBuildingsCount().getCities();
        int biggestArmyOwnerBonus = gameUser.getGameUserId().equals(game.getBiggestArmyOwner()) ? 2 : 0;

        gameUser.getAchievements().setDisplayVictoryPoints(settlementsCount + citiesCount * 2 + biggestArmyOwnerBonus);
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
