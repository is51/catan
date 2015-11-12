package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.MapElement;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.types.DevelopmentCard;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.services.PlayService;
import catan.services.util.game.GameUtil;
import catan.services.util.play.BuildUtil;
import catan.services.util.play.PlayUtil;
import catan.services.util.play.PreparationStageUtil;
import catan.services.util.random.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("playService")
@Transactional
public class PlayServiceImpl implements PlayService {
    private Logger log = LoggerFactory.getLogger(PlayService.class);

    public static final String ERROR_CODE_ERROR = "ERROR";
    public static final String CARDS_ARE_OVER_ERROR = "CARDS_ARE_OVER";

    private GameDao gameDao;
    private RandomUtil randomUtil;
    private GameUtil gameUtil;
    private PlayUtil playUtil;
    private BuildUtil buildUtil;
    private PreparationStageUtil preparationStageUtil;

    @Override
    public Map<String, String> processAction(GameUserActionCode action, UserBean user, String gameId) throws PlayException, GameException {
        return processAction(action, user, gameId, new HashMap<String, String>());
    }

    @Override
    public Map<String, String> processAction(GameUserActionCode action, UserBean user, String gameId, Map<String, String> params) throws PlayException, GameException {
        log.debug("{} tries to perform action {} at game with id {} and additional params {}", user, action, gameId, params);

        validateUserNotEmpty(user);

        GameBean game = gameUtil.getGameById(gameId, ERROR_CODE_ERROR);
        GameUserBean gameUser = gameUtil.getGameUserJoinedToGame(user, game);

        validateGameStatusIsPlaying(game);
        validateActionIsAllowedForUser(gameUser, action);

        Map<String, String> obtainedParams = doAction(action, user, game, params);

        playUtil.updateVictoryPoints(gameUser);
        playUtil.updateAvailableActionsForAllUsers(game);

        gameDao.updateGame(game);

        log.debug("User {} successfully performed action {}", user.getUsername(), action);
        return obtainedParams;
    }

    private Map<String, String> doAction(GameUserActionCode action, UserBean user, GameBean game, Map<String, String> params) throws PlayException, GameException {
        Map<String, String> obtainedParams = new HashMap<String, String>();
        switch(action){
            case BUILD_ROAD:
                buildRoad(user, game, params.get("edgeId"));
                break;
            case BUILD_SETTLEMENT:
                buildSettlement(user, game, params.get("nodeId"));
                break;
            case BUILD_CITY:
                buildCity(user, game, params.get("nodeId"));
                break;
            case END_TURN:
                endTurn(game);
                break;
            case THROW_DICE:
                throwDice(game);
                break;
            case BUY_CARD:
                obtainedParams = buyCard(user, game);
                break;
        }
        return obtainedParams;
    }

    private void buildRoad(UserBean user, GameBean game, String edgeId) throws PlayException, GameException {
        EdgeBean edgeToBuildOn = (EdgeBean) buildUtil.getValidMapElementByIdToBuildOn(edgeId, new ArrayList<MapElement>(game.getEdges()));
        buildUtil.validateUserCanBuildRoadOnEdge(user, edgeToBuildOn);
        buildUtil.buildRoadOnEdge(user, edgeToBuildOn);

        preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);
    }

    private void buildSettlement(UserBean user, GameBean game, String nodeId) throws PlayException, GameException {
        NodeBean nodeToBuildOn = (NodeBean) buildUtil.getValidMapElementByIdToBuildOn(nodeId, new ArrayList<MapElement>(game.getNodes()));
        buildUtil.validateUserCanBuildSettlementOnNode(user, game.getStage(), nodeToBuildOn);
        buildUtil.buildOnNode(user, nodeToBuildOn, NodeBuiltType.SETTLEMENT);

        preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);
    }

    private void buildCity(UserBean user, GameBean game, String nodeId) throws PlayException, GameException {
        NodeBean nodeToBuildOn = (NodeBean) buildUtil.getValidMapElementByIdToBuildOn(nodeId, new ArrayList<MapElement>(game.getNodes()));
        buildUtil.validateUserCanBuildCityOnNode(user, game.getStage(), nodeToBuildOn);
        buildUtil.buildOnNode(user, nodeToBuildOn, NodeBuiltType.CITY);

        preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);
    }

    private void endTurn(GameBean game) throws GameException {
        boolean shouldUpdateNextMove = true;

        if (game.getStage().equals(GameStage.PREPARATION)) {
            Integer previousPreparationCycle = game.getPreparationCycle();
            preparationStageUtil.updateGameStageToMain(game); //TODO: move it to the end of method calls
            preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);
            preparationStageUtil.updatePreparationCycle(game);
            Integer newPreparationCycle = game.getPreparationCycle();
            boolean preparationFinished = newPreparationCycle == null && !game.getCurrentMove().equals(1);
            shouldUpdateNextMove = (preparationFinished || previousPreparationCycle.equals(newPreparationCycle));
        } else {
            game.setDiceThrown(false);
        }

        if (shouldUpdateNextMove) {
            playUtil.updateNextMove(game);
        }
    }

    private void throwDice(GameBean game) {
        game.setDiceThrown(true);
    }

    private Map<String, String> buyCard(UserBean user, GameBean game) throws PlayException, GameException {
        Map<String, String> obtainedParams = new HashMap<String, String>();

        List<DevelopmentCard> availableDevCards = new ArrayList<DevelopmentCard>();

        int knights = game.getAvailableDevelopmentCards().getKnight();
        int victoryPoints = game.getAvailableDevelopmentCards().getVictoryPoint();
        int monopolies = game.getAvailableDevelopmentCards().getMonopoly();
        int roadBuildings = game.getAvailableDevelopmentCards().getRoadBuilding();
        int yearsOfPlenty = game.getAvailableDevelopmentCards().getYearOfPlenty();

        formAvailableDevCardsList(availableDevCards, knights, victoryPoints, monopolies, roadBuildings, yearsOfPlenty);

        if (availableDevCards.size() == 0) {
            log.debug("No available cards");
            throw new PlayException(CARDS_ARE_OVER_ERROR);
        }

        DevelopmentCard obtainedDevelopmentCard = randomUtil.pullRandomDevelopmentCard(availableDevCards);
        obtainedParams.put("card", obtainedDevelopmentCard.name());

        GameUserBean gameUserBean = gameUtil.getGameUserJoinedToGame(user, game);
        updateUserDevCards(gameUserBean, obtainedDevelopmentCard);
        updateAvailableDevCards(game, obtainedDevelopmentCard, knights, victoryPoints, monopolies, roadBuildings, yearsOfPlenty);

        return obtainedParams;
    }

    private void updateUserDevCards(GameUserBean gameUserBean, DevelopmentCard obtainedDevelopmentCard) {
        switch (obtainedDevelopmentCard) {
            case VICTORY_POINT:
                int userVictoryPoints = gameUserBean.getDevelopmentCards().getVictoryPoint();
                gameUserBean.getDevelopmentCards().setVictoryPoint(userVictoryPoints + 1);
                break;
            case KNIGHT:
                int userKnights = gameUserBean.getDevelopmentCards().getKnight();
                gameUserBean.getDevelopmentCards().setKnight(userKnights + 1);
                break;
            case MONOPOLY:
                int userMonopolies = gameUserBean.getDevelopmentCards().getMonopoly();
                gameUserBean.getDevelopmentCards().setMonopoly(userMonopolies + 1);
                break;
            case ROAD_BUILDING:
                int userRoadBuildings = gameUserBean.getDevelopmentCards().getRoadBuilding();
                gameUserBean.getDevelopmentCards().setRoadBuilding(userRoadBuildings + 1);
                break;
            case YEAR_OF_PLENTY:
                int userYearsOfPlenty = gameUserBean.getDevelopmentCards().getYearOfPlenty();
                gameUserBean.getDevelopmentCards().setYearOfPlenty(userYearsOfPlenty + 1);
                break;
        }
    }

    private void updateAvailableDevCards(GameBean game, DevelopmentCard obtainedDevelopmentCard, int knights, int victoryPoints, int monopolies, int roadBuildings, int yearsOfPlenty) {
        switch (obtainedDevelopmentCard) {
            case VICTORY_POINT:
                game.getAvailableDevelopmentCards().setVictoryPoint(victoryPoints - 1);
                break;
            case KNIGHT:
                game.getAvailableDevelopmentCards().setKnight(knights - 1);
                break;
            case MONOPOLY:
                game.getAvailableDevelopmentCards().setMonopoly(monopolies - 1);
                break;
            case ROAD_BUILDING:
                game.getAvailableDevelopmentCards().setRoadBuilding(roadBuildings - 1);
                break;
            case YEAR_OF_PLENTY:
                game.getAvailableDevelopmentCards().setYearOfPlenty(yearsOfPlenty - 1);
                break;
        }
    }

    private void formAvailableDevCardsList(List<DevelopmentCard> availableDevCards, int knights, int victoryPoints, int monopolies, int roadBuildings, int yearsOfPlenty) {
        for (int i = victoryPoints; i > 0; i--) {
            availableDevCards.add(DevelopmentCard.VICTORY_POINT);
        }
        for (int i = knights; i > 0; i--) {
            availableDevCards.add(DevelopmentCard.KNIGHT);
        }
        for (int i = monopolies; i > 0; i--) {
            availableDevCards.add(DevelopmentCard.MONOPOLY);
        }
        for (int i = roadBuildings; i > 0; i--) {
            availableDevCards.add(DevelopmentCard.ROAD_BUILDING);
        }
        for (int i = yearsOfPlenty; i > 0; i--) {
            availableDevCards.add(DevelopmentCard.YEAR_OF_PLENTY);
        }
    }

    private void validateUserNotEmpty(UserBean user) throws PlayException {
        if (user == null) {
            log.debug("User should not be empty");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private void validateGameStatusIsPlaying(GameBean game) throws GameException {
        if (game.getStatus() != GameStatus.PLAYING) {
            log.debug("User cannot do this action in current game status: {} instead of {}", game.getStatus(), GameStatus.PLAYING);
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    private void validateActionIsAllowedForUser(GameUserBean gameUser, GameUserActionCode requiredAction) throws PlayException, GameException {
        String availableActionsJson = gameUser.getAvailableActions();
        AvailableActions availableActions = playUtil.toAvailableActionsFromJson(availableActionsJson);

        boolean actionAllowed = false;
        for (Action allowedActions : availableActions.getList()) {
            if (allowedActions.getCode().equals(requiredAction.name())) {
                actionAllowed = true;
            }
        }

        if (!actionAllowed) {
            log.debug("Required action {} is not allowed for {}, current move in game is {}",
                    requiredAction.name(), gameUser, gameUser.getGame().getCurrentMove());
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }


    @Autowired
    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @Autowired
    public void setRandomUtil(RandomUtil randomUtil) {
        this.randomUtil = randomUtil;
    }

    @Autowired
    public void setGameUtil(GameUtil gameUtil) {
        this.gameUtil = gameUtil;
    }

    @Autowired
    public void setPlayUtil(PlayUtil playUtil) {
        this.playUtil = playUtil;
    }

    @Autowired
    public void setBuildUtil(BuildUtil buildUtil) {
        this.buildUtil = buildUtil;
    }

    @Autowired
    public void setPreparationStageUtil(PreparationStageUtil preparationStageUtil) {
        this.preparationStageUtil = preparationStageUtil;
    }
}