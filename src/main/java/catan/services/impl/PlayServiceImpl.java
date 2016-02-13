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
import catan.domain.model.game.Achievements;
import catan.domain.model.game.DevelopmentCards;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import catan.domain.model.game.TradeProposal;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.actions.TradingParams;
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
    public static final String OFFER_ALREADY_ACCEPTED_ERROR = "OFFER_ALREADY_ACCEPTED";

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
        log.debug("Start process action {} " + (params != null && !params.isEmpty() ? "with params " + params : "without params") + " by {} for game with id: {}", action, user, gameId);
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

        log.debug("Finish process action {} by {}", action, gameUser);
        return returnedParams;
    }

    private void doAction(GameUserActionCode action, UserBean user, GameUserBean gameUser, GameBean game, Map<String, String> params, Map<String, String> returnedParams) throws PlayException, GameException {
        Resources usersResources = gameUser.getResources();
        switch (action) {
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
            case USE_CARD_KNIGHT:
                useCardKnight(gameUser, game);
                break;
            case MOVE_ROBBER:
                moveRobber(gameUser, game, usersResources, params.get("hexId"));
                break;
            case CHOOSE_PLAYER_TO_ROB:
                choosePlayerToRob(gameUser, game, usersResources, params.get("gameUserId"));
                break;
            case KICK_OFF_RESOURCES:
                kickOffResources(gameUser, game, usersResources, params.get("brick"), params.get("wood"), params.get("sheep"), params.get("wheat"), params.get("stone"));
                break;
            case TRADE_PORT:
                tradeResourcesInPort(gameUser, game, usersResources, params.get("brick"), params.get("wood"), params.get("sheep"), params.get("wheat"), params.get("stone"));
                break;
            case TRADE_PROPOSE:
                proposeTrade(gameUser, game, usersResources, params.get("brick"), params.get("wood"), params.get("sheep"), params.get("wheat"), params.get("stone"));
                break;
            case TRADE_REPLY:
                tradeReply(gameUser, game, usersResources, params.get("tradeReply"));
                break;
        }
    }

    private void buildRoad(UserBean user, GameBean game, Resources usersResources, String edgeId) throws PlayException, GameException {
        EdgeBean edgeToBuildOn = (EdgeBean) buildUtil.getValidMapElementByIdToBuildOn(edgeId, new ArrayList<MapElement>(game.getEdges()));
        GameStage gameStage = game.getStage();
        buildUtil.validateUserCanBuildRoadOnEdge(user, edgeToBuildOn, gameStage);
        buildUtil.buildRoadOnEdge(user, edgeToBuildOn);

        preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);

        Integer mandatoryRoads = game.getRoadsToBuildMandatory();
        if (mandatoryRoads > 0) {
            game.setRoadsToBuildMandatory(mandatoryRoads - 1);
        }

        if (GameStage.MAIN.equals(gameStage) && mandatoryRoads == 0) {
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
                for (GameUserBean currentGameUser : game.getGameUsers()) {
                    currentGameUser.setAvailableTradeReply(false);
                }
                mainStageUtil.updateNextMove(game);
                break;
        }
    }

    private void throwDice(GameBean game) {
        Integer diceFirstValue = randomUtil.getRandomDiceNumber();
        Integer diceSecondValue = randomUtil.getRandomDiceNumber();
        log.info("Current dice value is " + (diceFirstValue + diceSecondValue) + " (First dice: " + diceFirstValue + ", Second dice: " + diceSecondValue + ")");

        game.setDiceFirstValue(diceFirstValue);
        game.setDiceSecondValue(diceSecondValue);
        game.setDiceThrown(true);
        if (isRobbersActivity(diceFirstValue, diceSecondValue)) {
            log.debug("Robber activity is started, checking if players should kick-off resources");
            checkIfPlayersShouldKickOffResources(game);
        } else {
            List<HexBean> hexesWithCurrentDiceValue = game.fetchHexesWithCurrentDiceValue();
            log.debug("Producing resources form hexes with current dice value:" + (hexesWithCurrentDiceValue.isEmpty() ? "" : "\n\t\t") + hexesWithCurrentDiceValue.toString());
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

    private void useCardKnight(GameUserBean gameUser, GameBean game) throws PlayException, GameException {
        cardUtil.validateUserDidNotUsedCardsInCurrentTurn(game);
        cardUtil.validateUserDidNotBoughtCardInCurrentTurn(gameUser, DevelopmentCard.KNIGHT);
        cardUtil.takeDevelopmentCardFromPlayer(gameUser, DevelopmentCard.KNIGHT);

        game.setRobberShouldBeMovedMandatory(true);
        game.setDevelopmentCardUsed(true);

        Achievements achievements = gameUser.getAchievements();
        int totalUsedKnightsNew = achievements.getTotalUsedKnights() + 1;

        achievements.setTotalUsedKnights(totalUsedKnightsNew);
        updateBiggestArmyOwner(gameUser, game);
    }

    private void updateBiggestArmyOwner(GameUserBean gameUser, GameBean game) {
        int totalUsedKnights = gameUser.getAchievements().getTotalUsedKnights();
        if (totalUsedKnights < 3) {
            return;
        }

        Integer biggestArmyOwnerId = game.getBiggestArmyOwner();
        if (biggestArmyOwnerId != null) {
            for (GameUserBean currentGameUser : game.getGameUsers()) {
                if (!currentGameUser.getGameUserId().equals(biggestArmyOwnerId)) {
                    continue;
                }
                if (currentGameUser.getAchievements().getTotalUsedKnights() >= totalUsedKnights) {
                    return;
                }
                break;
            }
        }
        game.setBiggestArmyOwner(gameUser.getGameUserId());
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
        log.info("Robbing {}", robbedGameUser);

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

    private void kickOffResources(GameUserBean gameUser, GameBean game, Resources userResources, String brickString, String woodString, String sheepString, String wheatString, String stoneString) throws PlayException, GameException {

        int usersBrickQuantity = userResources.getBrick();
        int usersWoodQuantity = userResources.getWood();
        int usersSheepQuantity = userResources.getSheep();
        int usersWheatQuantity = userResources.getWheat();
        int usersStoneQuantity = userResources.getStone();

        int brickQuantityToKickOff = toValidResourceQuantityToKickOff(brickString, usersBrickQuantity);
        int woodQuantityToKickOff = toValidResourceQuantityToKickOff(woodString, usersWoodQuantity);
        int sheepQuantityToKickOff = toValidResourceQuantityToKickOff(sheepString, usersSheepQuantity);
        int wheatQuantityToKickOff = toValidResourceQuantityToKickOff(wheatString, usersWheatQuantity);
        int stoneQuantityToKickOff = toValidResourceQuantityToKickOff(stoneString, usersStoneQuantity);

        int sumOfResourcesKickingOff = brickQuantityToKickOff + woodQuantityToKickOff + sheepQuantityToKickOff + wheatQuantityToKickOff + stoneQuantityToKickOff;
        int sumOfUsersResources = gameUser.getAchievements().getTotalResources();
        validateSumOfResourcesToKickOffIsTheHalfOfTotalResources(sumOfUsersResources, sumOfResourcesKickingOff);

        userResources.setBrick(usersBrickQuantity - brickQuantityToKickOff);
        userResources.setWood(usersWoodQuantity - woodQuantityToKickOff);
        userResources.setSheep(usersSheepQuantity - sheepQuantityToKickOff);
        userResources.setWheat(usersWheatQuantity - wheatQuantityToKickOff);
        userResources.setStone(usersStoneQuantity - stoneQuantityToKickOff);

        gameUser.setKickingOffResourcesMandatory(false);
        checkRobberShouldBeMovedMandatory(game);
    }

    private void tradeResourcesInPort(GameUserBean gameUser, GameBean game, Resources userResources, String brickString, String woodString, String sheepString, String wheatString, String stoneString) throws PlayException, GameException {

        int usersBrickQuantity = userResources.getBrick();
        int usersWoodQuantity = userResources.getWood();
        int usersSheepQuantity = userResources.getSheep();
        int usersWheatQuantity = userResources.getWheat();
        int usersStoneQuantity = userResources.getStone();

        TradingParams tradingParams = mainStageUtil.calculateTradingParams(gameUser, game);
        int brickSellCoefficient = tradingParams.getBrick();
        int woodSellCoefficient = tradingParams.getWood();
        int sheepSellCoefficient = tradingParams.getSheep();
        int wheatSellCoefficient = tradingParams.getWheat();
        int stoneSellCoefficient = tradingParams.getStone();

        int brickQuantityToTrade = toValidResourceQuantityToTradeInPort(brickString, usersBrickQuantity, brickSellCoefficient);
        int woodQuantityToTrade = toValidResourceQuantityToTradeInPort(woodString, usersWoodQuantity, woodSellCoefficient);
        int sheepQuantityToTrade = toValidResourceQuantityToTradeInPort(sheepString, usersSheepQuantity, sheepSellCoefficient);
        int wheatQuantityToTrade = toValidResourceQuantityToTradeInPort(wheatString, usersWheatQuantity, wheatSellCoefficient);
        int stoneQuantityToTrade = toValidResourceQuantityToTradeInPort(stoneString, usersStoneQuantity, stoneSellCoefficient);

        int tradingBalance = (brickQuantityToTrade < 0 ? brickQuantityToTrade / brickSellCoefficient : brickQuantityToTrade)
                           + (woodQuantityToTrade < 0 ? woodQuantityToTrade / woodSellCoefficient : woodQuantityToTrade)
                           + (sheepQuantityToTrade < 0 ? sheepQuantityToTrade / sheepSellCoefficient : sheepQuantityToTrade)
                           + (wheatQuantityToTrade < 0 ? wheatQuantityToTrade / wheatSellCoefficient : wheatQuantityToTrade)
                           + (stoneQuantityToTrade < 0 ? stoneQuantityToTrade / stoneSellCoefficient : stoneQuantityToTrade);
        validateTradeBalanceIsZero(tradingBalance);
        validateTradeIsNotEmpty(brickQuantityToTrade, woodQuantityToTrade, sheepQuantityToTrade, wheatQuantityToTrade, stoneQuantityToTrade);

        userResources.setBrick(usersBrickQuantity + brickQuantityToTrade);
        userResources.setWood(usersWoodQuantity + woodQuantityToTrade);
        userResources.setSheep(usersSheepQuantity + sheepQuantityToTrade);
        userResources.setWheat(usersWheatQuantity + wheatQuantityToTrade);
        userResources.setStone(usersStoneQuantity + stoneQuantityToTrade);
    }

    private void validateTradeIsNotEmpty(int brick, int wood, int sheep, int wheat, int stone) throws PlayException {
        if (brick == 0 && wood == 0 && sheep == 0 && wheat == 0 && stone == 0) {
            log.error("Trading request is empty. All requested resources has zero quantity");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private void proposeTrade(GameUserBean gameUser, GameBean game, Resources userResources, String brickString, String woodString, String sheepString, String wheatString, String stoneString) throws PlayException, GameException {

        int brick = toValidResourceQuantityToTrade(brickString, userResources.getBrick());
        int wood = toValidResourceQuantityToTrade(woodString, userResources.getWood());
        int sheep = toValidResourceQuantityToTrade(sheepString, userResources.getSheep());
        int wheat = toValidResourceQuantityToTrade(wheatString, userResources.getWheat());
        int stone = toValidResourceQuantityToTrade(stoneString, userResources.getStone());

        validateResourceQuantityToSellAndToBuyInTradePropose(brick, wood, sheep, wheat, stone);

        for (GameUserBean currentGameUser : game.getGameUsers()) {
            if (!currentGameUser.equals(gameUser)) {
                currentGameUser.setAvailableTradeReply(true);
            }
        }

        game.setTradeProposal(new TradeProposal(brick, wood, sheep, wheat, stone));
    }

    private void tradeReply(GameUserBean gameUser, GameBean game, Resources userResources, String reply) throws PlayException, GameException {
        TradeProposal tradeProposal = game.getTradeProposal();
        gameUser.setAvailableTradeReply(false);

        if (reply.equals("decline")) {
            for (GameUserBean currentGameUser : game.getGameUsers()) {
                if (currentGameUser.isAvailableTradeReply()) {
                    return;
                }
            }
        }

        if (reply.equals("accept")) {
            validateOfferIsNotAcceptedBefore(tradeProposal);

            int brick = tradeProposal.getBrick();
            int wood = tradeProposal.getWood();
            int sheep = tradeProposal.getSheep();
            int wheat = tradeProposal.getWheat();
            int stone = tradeProposal.getStone();
            validateUserHasRequestedResources(gameUser, brick, wood, sheep, wheat, stone);

            tradeProposal.setFinishedTrade(true);
            Integer currentMove = game.getCurrentMove();
            for (GameUserBean currentGameUser : game.getGameUsers()) {
                if (currentGameUser.getMoveOrder() == currentMove) {
                    currentGameUser.getResources().addResources(brick, wood, sheep, wheat, stone);
                    break;
                }
            }
            userResources.addResources(-brick, -wood, -sheep, -wheat, -stone);
        }

        tradeProposal.setFinishedTrade(true);
    }

    private void validateOfferIsNotAcceptedBefore(TradeProposal tradeProposal) throws PlayException {
        if (tradeProposal != null && tradeProposal.isFinishedTrade() != null && tradeProposal.isFinishedTrade()) {
            log.error("Trade proposal already accepted");
            throw new PlayException(OFFER_ALREADY_ACCEPTED_ERROR);
        }
    }

    private void validateUserHasRequestedResources(GameUserBean gameUser, int brick, int wood, int sheep, int wheat, int stone) throws PlayException {
        Resources userResources = gameUser.getResources();
        if (userResources.getBrick() < brick
                || userResources.getWood() < wood
                || userResources.getSheep() < sheep
                || userResources.getWheat() < wheat
                || userResources.getStone() < stone) {
            log.debug("User has not enough resources ({}) to accept trade proposal: [{}, {}, {}, {}, {}]", userResources, brick, wood, sheep, wheat, stone);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private void validateResourceQuantityToSellAndToBuyInTradePropose(int... resourcesQuantity) throws PlayException {
        int buyingResources = 0;
        int sellingResources = 0;
        for (int resourceQuantity : resourcesQuantity) {
            if (resourceQuantity > 0) {
                buyingResources += resourceQuantity;
            } else {
                sellingResources += resourceQuantity;
            }
        }

        if (buyingResources == 0) {
            log.error("User cannot propose trade without any resources to buy");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        if (sellingResources == 0) {
            log.error("User cannot propose trade without any resources to sell");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private void validateTradeBalanceIsZero(int tradingBalance) throws PlayException {
        if (tradingBalance != 0) {
            log.error("Trade balance is not zero: {}. Wrong resources quantity to trade", tradingBalance);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private void checkRobberShouldBeMovedMandatory(GameBean game) {
        for (GameUserBean gameUserIterated : game.getGameUsers()) {
            if (gameUserIterated.isKickingOffResourcesMandatory()) {
                return;
            }
        }
        game.setRobberShouldBeMovedMandatory(true);
    }

    private void validateSumOfResourcesToKickOffIsTheHalfOfTotalResources(int sumOfUsersResources, int sumOfResourcesKickingOff) throws PlayException {
        if (sumOfResourcesKickingOff != sumOfUsersResources / 2) {
            log.error("Wrong resources quantity: {}", sumOfResourcesKickingOff);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private int toValidResourceQuantity(String resourceQuantityString) throws PlayException {
        try {
            return Integer.parseInt(resourceQuantityString);
        } catch (Exception e) {
            log.error("Cannot convert resourceQuantity to integer value: {}", resourceQuantityString);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    private int toValidResourceQuantityToKickOff(String resourceQuantityString, int usersResourceQuantity) throws PlayException {
        int resourceQuantity = toValidResourceQuantity(resourceQuantityString);

        if (resourceQuantity < 0) {
            log.error("Resource quantity could not be below 0");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        if (resourceQuantity > usersResourceQuantity) {
            log.error("User cannot kick of more resources than he has: {} / {}", resourceQuantity, usersResourceQuantity);
            throw new PlayException(ERROR_CODE_ERROR);
        }

        return resourceQuantity;
    }

    private int toValidResourceQuantityToTrade(String resourceQuantityString, int usersResourceQuantity) throws PlayException {
        int resourceQuantity = toValidResourceQuantity(resourceQuantityString);

        if (-resourceQuantity > usersResourceQuantity) {
            log.error("User cannot sell more resources than he has: {} / {}", -resourceQuantity, usersResourceQuantity);
            throw new PlayException(ERROR_CODE_ERROR);
        }

        return resourceQuantity;
    }


    private int toValidResourceQuantityToTradeInPort(String resourceQuantityString, int usersResourceQuantity, int tradeCoefficient) throws PlayException {
        int resourceQuantity = toValidResourceQuantityToTrade(resourceQuantityString, usersResourceQuantity);

        if (resourceQuantity < 0 && resourceQuantity % tradeCoefficient != 0) {
            log.error("Invalid resource quantity to sell: {} should be divisible by {}", -resourceQuantity, tradeCoefficient);
            throw new PlayException(ERROR_CODE_ERROR);
        }

        return resourceQuantity;
    }


    private void checkIfPlayersShouldKickOffResources(GameBean game) {
        boolean noOneNeedsToKickOfResources = true;
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getAchievements().getTotalResources() > 7) {
                gameUser.setKickingOffResourcesMandatory(true);
                noOneNeedsToKickOfResources = false;
            }
        }

        if (noOneNeedsToKickOfResources) {
            game.setRobberShouldBeMovedMandatory(true);
        }
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
        if (requiredAction.equals(GameUserActionCode.TRADE_REPLY) && gameUser.isAvailableTradeReply()) {
            return;
        }

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