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

        playUtil.updateAchievements(game);
        playUtil.finishGameIfTargetVictoryPointsReached(gameUser, game);
        playUtil.updateAvailableActionsForAllUsers(game);

        gameDao.updateGame(game);

        log.debug("Action {} was successfully performed by user {}", action, gameUser);
        return returnedParams;
    }
    
    private void doAction(GameUserActionCode action, UserBean user, GameUserBean gameUser, GameBean game, Map<String, String> params, Map<String, String> returnedParams) throws PlayException, GameException {
        Resources usersResources = gameUser.getResources();
        switch(action){
            case BUILD_ROAD:
                buildRoad(user, game, usersResources, params.get("edgeId"));
                break;
            case BUILD_SETTLEMENT:
                buildSettlement(user, game, usersResources, params.get("nodeId"));
                break;
            case BUILD_CITY:
                buildCity(user, game, usersResources, params.get("nodeId"));
                break;
            case END_TURN:
                endTurn(gameUser, game);
                break;
            case THROW_DICE:
                throwDice(game);
                break;
            case BUY_CARD:
                buyCard(gameUser, game, usersResources, returnedParams);
                break;
            case USE_CARD_YEAR_OF_PLENTY:
                useCardYearOfPlenty(gameUser, game, usersResources, params.get("firstResource"), params.get("secondResource"));
                break;
            case USE_CARD_MONOPOLY:
                useCardMonopoly(gameUser, game, params.get("resource"), returnedParams);
                break;
            case USE_CARD_ROAD_BUILDING:
                useCardRoadBuilding(gameUser, game, returnedParams);
                break;
            case MOVE_ROBBER:
                moveRobber(gameUser, game, usersResources, params.get("hexId"));
                break;
            case CHOOSE_PLAYER_TO_ROB:
                choosePlayerToRob(gameUser, game, usersResources, params.get("gameUserId"));
                break;
        }
    }

    private void buildRoad(UserBean user, GameBean game, Resources usersResources, String edgeId) throws PlayException, GameException {
        EdgeBean edgeToBuildOn = (EdgeBean) buildUtil.getValidMapElementByIdToBuildOn(edgeId, new ArrayList<MapElement>(game.getEdges()));
        buildUtil.validateUserCanBuildRoadOnEdge(user, edgeToBuildOn);
        buildUtil.buildRoadOnEdge(user, edgeToBuildOn);

        preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);

        Integer mandatoryRoads = game.getRoadsToBuildMandatory();
        if (mandatoryRoads > 0) {
            game.setRoadsToBuildMandatory(mandatoryRoads - 1);
        }

        if (GameStage.MAIN.equals(game.getStage()) && mandatoryRoads == 0) {
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.BRICK, 1);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.WOOD, 1);
        }
    }

    private void buildSettlement(UserBean user, GameBean game, Resources usersResources, String nodeId) throws PlayException, GameException {
        NodeBean nodeToBuildOn = (NodeBean) buildUtil.getValidMapElementByIdToBuildOn(nodeId, new ArrayList<MapElement>(game.getNodes()));
        buildUtil.validateUserCanBuildSettlementOnNode(user, game.getStage(), nodeToBuildOn);
        buildUtil.buildOnNode(user, nodeToBuildOn, NodeBuiltType.SETTLEMENT);

        preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);

        if (GameStage.MAIN.equals(game.getStage())) {
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.BRICK, 1);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.WOOD, 1);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.WHEAT, 1);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.SHEEP, 1);
        }
    }

    private void buildCity(UserBean user, GameBean game, Resources usersResources, String nodeId) throws PlayException, GameException {
        NodeBean nodeToBuildOn = (NodeBean) buildUtil.getValidMapElementByIdToBuildOn(nodeId, new ArrayList<MapElement>(game.getNodes()));
        buildUtil.validateUserCanBuildCityOnNode(user, game.getStage(), nodeToBuildOn);
        buildUtil.buildOnNode(user, nodeToBuildOn, NodeBuiltType.CITY);

        preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);

        if (GameStage.MAIN.equals(game.getStage())) {
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.WHEAT, 2);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.STONE, 3);
        }
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
        if (isRobbersActivity(diceFirstValue, diceSecondValue)) {
            game.setRobberShouldBeMovedMandatory(true);
            log.debug("Robbers activity due to dice value 7");
        } else {
            List<HexBean> hexesWithCurrentDiceValue = game.fetchHexesWithCurrentDiceValue();
            log.debug("Hexes with current dice value:" + hexesWithCurrentDiceValue.toString());
            mainStageUtil.produceResourcesFromActiveDiceHexes(hexesWithCurrentDiceValue);
        }
    }

    private void buyCard(GameUserBean gameUser, GameBean game, Resources usersResources, Map<String, String> returnedParams) throws PlayException, GameException {
        DevelopmentCards availableDevelopmentCards = game.getAvailableDevelopmentCards();
        DevelopmentCard chosenDevelopmentCard = cardUtil.chooseDevelopmentCard(availableDevelopmentCards);
        log.debug("Card " + chosenDevelopmentCard + " was chosen from the list: " + availableDevelopmentCards);

        cardUtil.giveDevelopmentCardToUser(gameUser, availableDevelopmentCards, chosenDevelopmentCard);

        returnedParams.put("card", chosenDevelopmentCard.name());

        if (GameStage.MAIN.equals(game.getStage())) {
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.WHEAT, 1);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.SHEEP, 1);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.STONE, 1);
        }
    }

    private void useCardYearOfPlenty(GameUserBean gameUser, GameBean game, Resources userResources, String firstResourceString, String secondResourceString) throws PlayException, GameException {
        cardUtil.validateUserDidNotUsedCardsInCurrentTurn(game);
        cardUtil.validateUserDidNotBoughtCardInCurrentTurn(gameUser, DevelopmentCard.YEAR_OF_PLENTY);

        HexType firstResource = toValidResourceType(firstResourceString);
        HexType secondResource = toValidResourceType(secondResourceString);

        cardUtil.giveTwoResourcesToPlayer(userResources, firstResource, secondResource);
        log.debug("Player got resources: {}, {}", firstResource, secondResource);

        cardUtil.takeDevelopmentCardFromPlayer(gameUser, DevelopmentCard.YEAR_OF_PLENTY);
        game.setDevelopmentCardUsed(true);
    }

    private void useCardMonopoly(GameUserBean gameUser, GameBean game, String resourceString, Map<String, String> returnedParams) throws PlayException, GameException {
        cardUtil.validateUserDidNotUsedCardsInCurrentTurn(game);
        cardUtil.validateUserDidNotBoughtCardInCurrentTurn(gameUser, DevelopmentCard.MONOPOLY);

        HexType resource = toValidResourceType(resourceString);

        Integer takenResourcesCount = cardUtil.takeResourceFromRivalsAndGiveItAwayToPlayer(gameUser, game, resource);
        log.debug("Player got {} of {} resources", takenResourcesCount, resource);

        returnedParams.put("resourcesCount", takenResourcesCount.toString());

        cardUtil.takeDevelopmentCardFromPlayer(gameUser, DevelopmentCard.MONOPOLY);
        game.setDevelopmentCardUsed(true);
    }

    private void useCardRoadBuilding(GameUserBean gameUser, GameBean game, Map<String, String> returnedParams) throws PlayException, GameException {
        cardUtil.validateUserDidNotUsedCardsInCurrentTurn(game);
        cardUtil.validateUserDidNotBoughtCardInCurrentTurn(gameUser, DevelopmentCard.ROAD_BUILDING);

        Integer roadsCount = cardUtil.defineRoadsQuantityToBuild(gameUser, game);
        game.setRoadsToBuildMandatory(roadsCount);
        log.debug("Player can build {} road(s) using development card", roadsCount);

        returnedParams.put("roadsCount", roadsCount.toString());

        cardUtil.takeDevelopmentCardFromPlayer(gameUser, DevelopmentCard.ROAD_BUILDING);
        game.setDevelopmentCardUsed(true);
    }

    private void moveRobber(GameUserBean gameUser, GameBean game, Resources userResources, String hexId) throws PlayException, GameException {
        HexBean hexToRob = toValidHex(game, hexId);
        validateHexCouldBeRobbed(hexToRob);

        changeRobbedHex(game, hexToRob);
        log.info("Hex {} successfully robbed", hexId);
        game.setRobberShouldBeMovedMandatory(false);

        List<GameUserBean> playersAtHex = new ArrayList<GameUserBean>(hexToRob.fetchGameUsersWithBuildingsAtNodes());
        playersAtHex.remove(gameUser);
        int playersToRobQuantity = playersAtHex.size();
        if (playersToRobQuantity == 1) {
            GameUserBean playerToRob = playersAtHex.get(0);
            choosePlayerToRob(gameUser, game, userResources, playerToRob.getGameUserId());
        }
        if (playersToRobQuantity > 1) {
            game.setChoosePlayerToRobMandatory(true);
        }
    }

    private void choosePlayerToRob(GameUserBean gameUser, GameBean game, Resources currentUsersResources, String gameUserIdString) throws PlayException, GameException {
        int gameUserId = toValidGameUserId(gameUserIdString);
        choosePlayerToRob(gameUser, game, currentUsersResources, gameUserId);
    }

    private void choosePlayerToRob(GameUserBean gameUser, GameBean game, Resources currentUsersResources, int gameUserId) throws PlayException, GameException {
        GameUserBean robbedGameUser = gameUtil.getGameUserById(gameUserId, game);
        log.info("Robbing player: {}", robbedGameUser);

        validateGameUserCouldBeRobbed(gameUser, game, robbedGameUser);

        if (robbedGameUser.getAchievements().getTotalResources() > 0) {
            Resources robbedUsersResources = robbedGameUser.getResources();
            HexType stolenResourceType = randomUtil.getRandomUsersResource(robbedUsersResources);
            log.info("Resource to rob is {}", stolenResourceType);

            int stolenResourceQuantity = robbedUsersResources.quantityOf(stolenResourceType);
            robbedUsersResources.updateResourceQuantity(stolenResourceType, stolenResourceQuantity - 1);
            int obtainedResourceQuantity = currentUsersResources.quantityOf(stolenResourceType);
            currentUsersResources.updateResourceQuantity(stolenResourceType, obtainedResourceQuantity + 1);
        }
        game.setChoosePlayerToRobMandatory(false);
    }

    private void validateGameUserCouldBeRobbed(GameUserBean gameUser, GameBean game, GameUserBean robbedGameUser) throws PlayException {
        if (gameUser.equals(robbedGameUser)) {
            log.error("Player couldn't rob himself");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        for (HexBean hex : game.getHexes()) {
            if (!hex.isRobbed()) {
                continue;
            }
            if (!hex.fetchGameUsersWithBuildingsAtNodes().remove(robbedGameUser)) {
                log.error("GameUser {} doesn't have any buildings at robbed hex {} and couldn't be robbed", robbedGameUser.getGameUserId(), hex.getId());
                throw new PlayException(ERROR_CODE_ERROR);
            }
            break;
        }
    }

    private int toValidGameUserId(String gameUserIdString) throws GameException {
        int gameUserId;
        try {
            gameUserId = Integer.parseInt(gameUserIdString);
        } catch (Exception e) {
            log.error("Cannot convert gameId to integer value");
            throw new GameException(ERROR_CODE_ERROR);
        }
        return gameUserId;
    }

    private void changeRobbedHex(GameBean game, HexBean hexToRob) {
        for (HexBean hex : game.getHexes()) {
            if (hex.isRobbed()) {
                hex.setRobbed(false);
                break;
            }
        }
        hexToRob.setRobbed(true);
    }

    private void validateHexCouldBeRobbed(HexBean hexToRob) throws PlayException {
        if (hexToRob.isRobbed() || hexToRob.getResourceType().equals(HexType.EMPTY)) {
            log.error("Hex {} cannot be robbed", hexToRob.getId());
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private HexBean toValidHex(GameBean game, String hexIdString) throws PlayException {
        int hexId;
        try {
            hexId = Integer.parseInt(hexIdString);
        } catch (Exception e) {
            log.error("Cannot convert hexId to integer value");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        for (HexBean hex : game.getHexes()) {
            if (hex.getId() == hexId) {
                return hex;
            }
        }

        log.error("Hex {} does not belong to game {}", hexId, game.getGameId());
        throw new PlayException(ERROR_CODE_ERROR);
    }

    private HexType toValidResourceType(String resourceString) throws PlayException {
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
            log.debug("Required action {} is not allowed for {}", requiredAction.name(), gameUser);
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