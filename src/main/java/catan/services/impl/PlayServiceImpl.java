package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.MapElement;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.DevelopmentCards;
import catan.domain.model.game.Resources;
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
import catan.services.util.play.CardUtil;
import catan.services.util.play.MainStageUtil;
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
    public static final String CARD_ALREADY_USED_IN_CURRENT_TURN_ERROR = "CARD_ALREADY_USED_IN_CURRENT_TURN";
    public static final String CARD_BOUGHT_IN_CURRENT_TURN_ERROR = "CARD_BOUGHT_IN_CURRENT_TURN";

    //TODO: since we inject preparationStageUtil, mainStageUtil and playUtil to playService and also preparationStageUtil and mainStageUtil to playUtil , I think we have wrong architecture, we should think how to refactor it
    private GameDao gameDao;
    private RandomUtil randomUtil;
    private GameUtil gameUtil;
    private PlayUtil playUtil;
    private BuildUtil buildUtil;
    private CardUtil cardUtil;
    private PreparationStageUtil preparationStageUtil;
    private MainStageUtil mainStageUtil;

    @Override
    public Map<String, String> processAction(GameUserActionCode action, UserBean user, String gameId) throws PlayException, GameException {
        return processAction(action, user, gameId, new HashMap<String, String>());
    }

    @Override
    public Map<String, String> processAction(GameUserActionCode action, UserBean user, String gameId, Map<String, String> params) throws PlayException, GameException {
        log.debug("{} tries to perform action {} at game with id {} and additional params {}", user, action, gameId, params);
        Map<String, String> returnedParams = new HashMap<String, String>();

        validateUserNotEmpty(user);

        GameBean game = gameUtil.getGameById(gameId, ERROR_CODE_ERROR);
        GameUserBean gameUser = gameUtil.getGameUserJoinedToGame(user, game);

        validateGameStatusIsPlaying(game);
        validateActionIsAllowedForUser(gameUser, action);

        doAction(action, user, gameUser, game, params, returnedParams);

        playUtil.updateVictoryPoints(gameUser);
        playUtil.finishGameIfTargetVictoryPointsReached(gameUser, game);
        playUtil.updateAvailableActionsForAllUsers(game);

        gameDao.updateGame(game);

        log.debug("User {} successfully performed action {}", user.getUsername(), action);
        return returnedParams;
    }

    private void doAction(GameUserActionCode action, UserBean user, GameUserBean gameUser, GameBean game, Map<String, String> params, Map<String, String> returnedParams) throws PlayException, GameException {
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
                endTurn(gameUser, game);
                break;
            case THROW_DICE:
                throwDice(game);
                break;
            case BUY_CARD:
                buyCard(gameUser, game, returnedParams);
                break;
            case USE_CARD_YEAR_OF_PLENTY:
                useCardYearOfPlenty(gameUser, game, params.get("firstResource"), params.get("secondResource"));
                break;
        }
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

    private void endTurn(GameUserBean gameUser, GameBean game) throws GameException {
        switch (game.getStage()) {

            case PREPARATION:
                Integer previousPreparationCycle = game.getPreparationCycle();
                preparationStageUtil.updateGameStageToMain(game); //TODO: move it to the end of method calls
                preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);
                preparationStageUtil.updatePreparationCycle(game);
                if (game.getPreparationCycle() == null || previousPreparationCycle.equals(game.getPreparationCycle())) {
                    preparationStageUtil.updateNextMove(game);
                }
                break;

            case MAIN:
                mainStageUtil.resetDices(game);
                gameUser.setDevelopmentCardsReadyForUsing(gameUser.getDevelopmentCards());
                game.setDevelopmentCardUsed(false);
                mainStageUtil.updateNextMove(game);
                break;
        }
    }

    private void throwDice(GameBean game) {
        Integer diceFirstValue = randomUtil.getRandomDiceNumber();
        Integer diceSecondValue = randomUtil.getRandomDiceNumber();
        log.info("First dice: " + diceFirstValue);
        log.info("Second dice: " + diceSecondValue);

        game.setDiceFirstValue(diceFirstValue);
        game.setDiceSecondValue(diceSecondValue);
        game.setDiceThrown(true);
        if (!isRobbersActivity(diceFirstValue, diceSecondValue)) {
            List<HexBean> hexesWithCurrentDiceValue = game.fetchHexesWithCurrentDiceValue();
            log.debug("Hexes with current dice value:" + hexesWithCurrentDiceValue);
            mainStageUtil.produceResourcesFromActiveDiceHexes(hexesWithCurrentDiceValue);
        } else{
            log.debug("Robbers activity due to dice value 7");
        }
    }

    private void buyCard(GameUserBean gameUser, GameBean game, Map<String, String> returnedParams) throws PlayException, GameException {
        DevelopmentCards availableDevelopmentCards = game.getAvailableDevelopmentCards();
        DevelopmentCard chosenDevelopmentCard = cardUtil.chooseDevelopmentCard(availableDevelopmentCards);
        log.debug("Card " + chosenDevelopmentCard + " was chosen from the list: " + availableDevelopmentCards);

        DevelopmentCards usersDevelopmentCards = gameUser.getDevelopmentCards();
        usersDevelopmentCards.increaseQuantityByOne(chosenDevelopmentCard);
        availableDevelopmentCards.decreaseQuantityByOne(chosenDevelopmentCard);

        returnedParams.put("card", chosenDevelopmentCard.name());
    }

    private void useCardYearOfPlenty(GameUserBean gameUser, GameBean game, String firstResourceString, String secondResourceString) throws PlayException, GameException {
        validateUserDidNotUsedCardsInCurrentTurn(game);
        validateUserDidNotBoughtCardInCurrentTurn(gameUser, DevelopmentCard.YEAR_OF_PLENTY);

        HexType firstResource = validateResourceType(firstResourceString);
        HexType secondResource = validateResourceType(secondResourceString);

        Resources userResources = gameUser.getResources();
        int currentFirstResourceQuantity = userResources.quantityOf(firstResource);
        userResources.updateResourceQuantity(firstResource, currentFirstResourceQuantity + 1);
        int currentSecondResourceQuantity = userResources.quantityOf(secondResource);
        userResources.updateResourceQuantity(secondResource, currentSecondResourceQuantity + 1);

        log.debug("Player got resources: {}, {}", firstResource, secondResource);

        gameUser.getDevelopmentCards().decreaseQuantityByOne(DevelopmentCard.YEAR_OF_PLENTY);
        gameUser.getDevelopmentCardsReadyForUsing().decreaseQuantityByOne(DevelopmentCard.YEAR_OF_PLENTY);
        game.setDevelopmentCardUsed(true);
    }

    private void validateUserDidNotBoughtCardInCurrentTurn(GameUserBean gameUser, DevelopmentCard developmentCard) throws PlayException {
        if (gameUser.getDevelopmentCardsReadyForUsing().quantityOf(developmentCard) == 0) {
            log.debug("Cannot use card. It was bought in current turn: {}", developmentCard);
            throw new PlayException(CARD_BOUGHT_IN_CURRENT_TURN_ERROR);
        }
    }

    private void validateUserDidNotUsedCardsInCurrentTurn(GameBean game) throws PlayException {
        if (game.isDevelopmentCardUsed()) {
            log.debug("Cannot use card. It was already used in current turn");
            throw new PlayException(CARD_ALREADY_USED_IN_CURRENT_TURN_ERROR);
        }
    }

    private HexType validateResourceType(String resourceString) throws PlayException {
        try {
            return HexType.valueOf(resourceString);
        } catch (Exception e) {
            log.debug("Illegal resource type: {}", resourceString);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private boolean isRobbersActivity(Integer diceFirstValue, Integer diceSecondValue) {
        return diceFirstValue + diceSecondValue == 7;
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
    public void setCardUtil(CardUtil cardUtil) {
        this.cardUtil = cardUtil;
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