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
import catan.domain.model.game.DevelopmentCards;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import catan.domain.model.game.TradeProposal;
import catan.domain.model.game.actions.ResourcesParams;
import catan.domain.model.game.types.DevelopmentCard;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.services.PlayService;
import catan.services.util.game.GameUtil;
import catan.services.util.play.AchievementsUtil;
import catan.services.util.play.ActionParamsUtil;
import catan.services.util.play.BuildUtil;
import catan.services.util.play.CardUtil;
import catan.services.util.play.MainStageUtil;
import catan.services.util.play.MessagesUtil;
import catan.services.util.play.PlayUtil;
import catan.services.util.play.PreparationStageUtil;
import catan.services.util.play.ValidationUtil;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service("playService")
@Transactional
public class PlayServiceImpl implements PlayService {
    private Logger log = LoggerFactory.getLogger(PlayService.class);

    //TODO: since we inject preparationStageUtil, mainStageUtil and playUtil to playService and also preparationStageUtil and mainStageUtil to playUtil , I think we have wrong architecture, we should think how to refactor it
    private GameDao gameDao;
    private RandomUtil randomUtil;
    private GameUtil gameUtil;
    private PlayUtil playUtil;
    private BuildUtil buildUtil;
    private CardUtil cardUtil;
    private AchievementsUtil achievementsUtil;
    private ActionParamsUtil actionParamsUtil;
    private PreparationStageUtil preparationStageUtil;
    private MainStageUtil mainStageUtil;
    private MessagesUtil messagesUtil;
    private ValidationUtil validationUtil;

    private ConcurrentMap<Long, Long> locks = new ConcurrentHashMap<Long, Long>();

    @Override
    public Map<String, String> processAction(GameUserActionCode action, UserBean user, String gameId) throws PlayException, GameException {
        return processAction(action, user, gameId, new HashMap<String, String>());
    }

    @Override
    public Map<String, String> processAction(GameUserActionCode action, UserBean user, String gameId, Map<String, String> params) throws PlayException, GameException {
        log.debug("Start process action {} " + (params != null && !params.isEmpty() ? "with params " + params : "without params") + " by {} for game with id: {}", action, user, gameId);
        Map<String, String> returnedParams = new HashMap<String, String>();

        validationUtil.validateUserNotEmpty(user);
        validationUtil.validateGameIdNotEmpty(gameId);

        synchronized (getGameIdSyncObject(Long.parseLong(gameId))) {
            GameBean game = gameUtil.getGameById(gameId);
            GameUserBean gameUser = gameUtil.getGameUserJoinedToGame(user, game);

            try {
                validationUtil.validateGameStatusIsPlaying(game);
                validationUtil.validateActionIsAllowedForUser(gameUser, action);

                doAction(action, gameUser, params, returnedParams);

                playUtil.updateAchievements(game);
                playUtil.finishGameIfTargetVictoryPointsReached(gameUser);
                playUtil.updateAvailableActionsForAllUsers(game);

                gameDao.updateGame(game);

            } finally {
                log.debug("Finish process action {} by {}", action, gameUser);
            }

            return returnedParams;
        }
    }

    private void doAction(GameUserActionCode action, GameUserBean gameUser, Map<String, String> params, Map<String, String> returnedParams) throws PlayException, GameException {
        switch (action) {
            case BUILD_ROAD:
                buildRoad(gameUser, params.get("edgeId"));
                break;
            case BUILD_SETTLEMENT:
                buildSettlement(gameUser, params.get("nodeId"));
                break;
            case BUILD_CITY:
                buildCity(gameUser, params.get("nodeId"));
                break;
            case END_TURN:
                endTurn(gameUser);
                break;
            case THROW_DICE:
                throwDice(gameUser);
                break;
            case BUY_CARD:
                buyCard(gameUser, returnedParams);
                break;
            case USE_CARD_YEAR_OF_PLENTY:
                useCardYearOfPlenty(gameUser, params.get("firstResource"), params.get("secondResource"));
                break;
            case USE_CARD_MONOPOLY:
                useCardMonopoly(gameUser, params.get("resource"), returnedParams);
                break;
            case USE_CARD_ROAD_BUILDING:
                useCardRoadBuilding(gameUser, returnedParams);
                break;
            case USE_CARD_KNIGHT:
                useCardKnight(gameUser);
                break;
            case MOVE_ROBBER:
                moveRobber(gameUser, params.get("hexId"));
                break;
            case CHOOSE_PLAYER_TO_ROB:
                choosePlayerToRob(gameUser, params.get("gameUserId"));
                break;
            case KICK_OFF_RESOURCES:
                kickOffResources(gameUser, params.get("brick"), params.get("wood"), params.get("sheep"), params.get("wheat"), params.get("stone"));
                break;
            case TRADE_PORT:
                tradeResourcesInPort(gameUser, params.get("brick"), params.get("wood"), params.get("sheep"), params.get("wheat"), params.get("stone"));
                break;
            case TRADE_PROPOSE:
                proposeTrade(gameUser, params.get("brick"), params.get("wood"), params.get("sheep"), params.get("wheat"), params.get("stone"));
                break;
            case TRADE_REPLY:
                tradeReply(gameUser, params.get("tradeReply"), params.get("offerId"));
                break;
        }
    }

    private void buildRoad(GameUserBean gameUser, String edgeId) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        EdgeBean edgeToBuildOn = (EdgeBean) buildUtil.getValidMapElementByIdToBuildOn(edgeId, new ArrayList<MapElement>(game.getEdges()));
        GameStage gameStage = game.getStage();
        buildUtil.validateUserCanBuildRoadOnEdge(gameUser, edgeToBuildOn, gameStage);
        buildUtil.buildRoadOnEdge(gameUser, edgeToBuildOn);

        preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);

        Integer mandatoryRoads = game.getRoadsToBuildMandatory();
        if (mandatoryRoads > 0) {
            game.setRoadsToBuildMandatory(mandatoryRoads - 1);
        }

        if (GameStage.MAIN.equals(gameStage) && mandatoryRoads == 0) {
            Resources usersResources = gameUser.getResources();
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.BRICK, 1);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.WOOD, 1);
        }

        achievementsUtil.updateLongestWayLength(game, gameUser);
    }

    private void buildSettlement(GameUserBean gameUser, String nodeId) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        NodeBean nodeToBuildOn = (NodeBean) buildUtil.getValidMapElementByIdToBuildOn(nodeId, new ArrayList<MapElement>(game.getNodes()));
        buildUtil.validateUserCanBuildSettlementOnNode(gameUser, game.getStage(), nodeToBuildOn);
        buildUtil.buildOnNode(gameUser, nodeToBuildOn, NodeBuiltType.SETTLEMENT);

        if (GameStage.MAIN.equals(game.getStage())) {
            Resources usersResources = gameUser.getResources();
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.BRICK, 1);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.WOOD, 1);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.WHEAT, 1);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.SHEEP, 1);
            achievementsUtil.updateLongestWayLengthIfInterrupted(game, gameUser, nodeToBuildOn);
        } else if (GameStage.PREPARATION.equals(game.getStage())) {
            preparationStageUtil.distributeResourcesForLastBuilding(nodeToBuildOn);
            preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);
        }
    }

    private void buildCity(GameUserBean gameUser, String nodeId) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        NodeBean nodeToBuildOn = (NodeBean) buildUtil.getValidMapElementByIdToBuildOn(nodeId, new ArrayList<MapElement>(game.getNodes()));
        buildUtil.validateUserCanBuildCityOnNode(gameUser, game.getStage(), nodeToBuildOn);
        buildUtil.buildOnNode(gameUser, nodeToBuildOn, NodeBuiltType.CITY);

        if (GameStage.MAIN.equals(game.getStage())) {
            Resources usersResources = gameUser.getResources();
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.WHEAT, 2);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.STONE, 3);
        } else if (GameStage.PREPARATION.equals(game.getStage())) {
            preparationStageUtil.distributeResourcesForLastBuilding(nodeToBuildOn);
            preparationStageUtil.updateCurrentCycleInitialBuildingNumber(game);
        }
    }

    private void endTurn(GameUserBean gameUser) throws GameException {
        GameBean game = gameUser.getGame();
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

    private void throwDice(GameUserBean gameUser) {
        Integer diceFirstValue = randomUtil.getRandomDiceNumber();
        Integer diceSecondValue = randomUtil.getRandomDiceNumber();
        log.info("Current dice value is " + (diceFirstValue + diceSecondValue) + " (First dice: " + diceFirstValue + ", Second dice: " + diceSecondValue + ")");

        GameBean game = gameUser.getGame();
        game.setDiceFirstValue(diceFirstValue);
        game.setDiceSecondValue(diceSecondValue);
        game.setDiceThrown(true);
        if (isRobbersActivity(diceFirstValue, diceSecondValue)) {
            log.debug("Robber activity is started, checking if players should kick-off resources");
            checkIfPlayersShouldKickOffResources(gameUser);
        } else {
            List<HexBean> hexesWithCurrentDiceValue = game.fetchHexesWithCurrentDiceValue();
            log.debug("Producing resources form hexes with current dice value:" + (hexesWithCurrentDiceValue.isEmpty() ? "" : "\n\t\t") + hexesWithCurrentDiceValue.toString());
            mainStageUtil.produceResourcesFromActiveDiceHexes(hexesWithCurrentDiceValue);
        }
    }

    private void buyCard(GameUserBean gameUser, Map<String, String> returnedParams) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        DevelopmentCards availableDevelopmentCards = game.getAvailableDevelopmentCards();
        DevelopmentCard chosenDevelopmentCard = cardUtil.chooseDevelopmentCard(availableDevelopmentCards);
        log.debug("Card " + chosenDevelopmentCard + " was chosen from the list: " + availableDevelopmentCards);

        cardUtil.giveDevelopmentCardToUser(gameUser, availableDevelopmentCards, chosenDevelopmentCard);

        returnedParams.put("card", chosenDevelopmentCard.name());

        if (GameStage.MAIN.equals(game.getStage())) {
            Resources usersResources = gameUser.getResources();
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.WHEAT, 1);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.SHEEP, 1);
            mainStageUtil.takeResourceFromPlayer(usersResources, HexType.STONE, 1);
        }
    }

    private void useCardYearOfPlenty(GameUserBean gameUser, String firstResourceString, String secondResourceString) throws PlayException, GameException {
        cardUtil.validateUserDidNotUsedCardsInCurrentTurn(gameUser);
        cardUtil.validateUserDidNotBoughtCardInCurrentTurn(gameUser, DevelopmentCard.YEAR_OF_PLENTY);

        HexType firstResource = validationUtil.toValidResourceType(firstResourceString);
        HexType secondResource = validationUtil.toValidResourceType(secondResourceString);

        cardUtil.giveTwoResourcesToPlayer(gameUser.getResources(), firstResource, secondResource);
        log.debug("Player got resources: {}, {}", firstResource, secondResource);

        cardUtil.takeDevelopmentCardFromPlayer(gameUser, DevelopmentCard.YEAR_OF_PLENTY);
        gameUser.getGame().setDevelopmentCardUsed(true);
    }

    private void useCardMonopoly(GameUserBean gameUser, String resourceString, Map<String, String> returnedParams) throws PlayException, GameException {
        cardUtil.validateUserDidNotUsedCardsInCurrentTurn(gameUser);
        cardUtil.validateUserDidNotBoughtCardInCurrentTurn(gameUser, DevelopmentCard.MONOPOLY);

        HexType resource = validationUtil.toValidResourceType(resourceString);

        Integer takenResourcesCount = cardUtil.takeResourceFromRivalsAndGiveItAwayToPlayer(gameUser, resource);
        log.debug("Player got {} of {} resources", takenResourcesCount, resource);

        returnedParams.put("resourcesCount", takenResourcesCount.toString());

        cardUtil.takeDevelopmentCardFromPlayer(gameUser, DevelopmentCard.MONOPOLY);
        gameUser.getGame().setDevelopmentCardUsed(true);
    }

    private void useCardRoadBuilding(GameUserBean gameUser, Map<String, String> returnedParams) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        cardUtil.validateUserDidNotUsedCardsInCurrentTurn(gameUser);
        cardUtil.validateUserDidNotBoughtCardInCurrentTurn(gameUser, DevelopmentCard.ROAD_BUILDING);

        Integer roadsCount = cardUtil.defineRoadsQuantityToBuild(gameUser);
        game.setRoadsToBuildMandatory(roadsCount);
        log.debug("Player can build {} road(s) using development card", roadsCount);

        returnedParams.put("roadsCount", roadsCount.toString());

        cardUtil.takeDevelopmentCardFromPlayer(gameUser, DevelopmentCard.ROAD_BUILDING);
        game.setDevelopmentCardUsed(true);
    }

    private void useCardKnight(GameUserBean gameUser) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        cardUtil.validateUserDidNotUsedCardsInCurrentTurn(gameUser);
        cardUtil.validateUserDidNotBoughtCardInCurrentTurn(gameUser, DevelopmentCard.KNIGHT);
        cardUtil.takeDevelopmentCardFromPlayer(gameUser, DevelopmentCard.KNIGHT);

        game.setRobberShouldBeMovedMandatory(true);
        messagesUtil.updateDisplayedMessage(gameUser, "help_msg_move_robber");
        game.setDevelopmentCardUsed(true);

        achievementsUtil.increaseTotalUsedKnightsByOne(gameUser);
        achievementsUtil.updateBiggestArmyOwner(gameUser);
    }

    private void moveRobber(GameUserBean gameUser, String hexAbsoluteId) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        HexBean hexToRob = validationUtil.toValidHex(game, hexAbsoluteId);
        validationUtil.validateHexCouldBeRobbed(hexToRob);

        playUtil.changeRobbedHex(hexToRob);
        log.info("Hex {} successfully robbed", hexAbsoluteId);
        game.setRobberShouldBeMovedMandatory(false);
        messagesUtil.clearUsersMsgs(game);

        List<GameUserBean> playersAtHex = new ArrayList<GameUserBean>(hexToRob.fetchGameUsersWithBuildingsAtNodes());
        playersAtHex.remove(gameUser);
        int playersToRobQuantity = playersAtHex.size();
        if (playersToRobQuantity == 1) {
            GameUserBean playerToRob = playersAtHex.get(0);
            choosePlayerToRob(gameUser, playerToRob.getGameUserId());
        }
        if (playersToRobQuantity > 1) {
            game.setChoosePlayerToRobMandatory(true);
            messagesUtil.updateDisplayedMessage(gameUser, "help_msg_choose_player_to_rob");
        }
    }

    private void choosePlayerToRob(GameUserBean gameUser, String robbedGameUserIdString) throws PlayException, GameException {
        int robbedGameUserId = validationUtil.toValidNumber(robbedGameUserIdString);
        choosePlayerToRob(gameUser, robbedGameUserId);
    }

    private void choosePlayerToRob(GameUserBean gameUser, int robbedGameUserId) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        Resources currentUsersResources = gameUser.getResources();
        GameUserBean robbedGameUser = gameUtil.getGameUserById(robbedGameUserId, game);
        log.info("Robbing {}", robbedGameUser);

        validationUtil.validateGameUserCouldBeRobbed(robbedGameUser);

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
        messagesUtil.clearUsersMsgs(game);
    }

    private void kickOffResources(GameUserBean gameUser, String brickString, String woodString, String sheepString, String wheatString, String stoneString) throws PlayException, GameException {
        Resources usersResources = gameUser.getResources();
        int usersBrickQuantity = usersResources.getBrick();
        int usersWoodQuantity = usersResources.getWood();
        int usersSheepQuantity = usersResources.getSheep();
        int usersWheatQuantity = usersResources.getWheat();
        int usersStoneQuantity = usersResources.getStone();

        int brickQuantityToKickOff = validationUtil.toValidResourceQuantityToKickOff(brickString, usersBrickQuantity);
        int woodQuantityToKickOff = validationUtil.toValidResourceQuantityToKickOff(woodString, usersWoodQuantity);
        int sheepQuantityToKickOff = validationUtil.toValidResourceQuantityToKickOff(sheepString, usersSheepQuantity);
        int wheatQuantityToKickOff = validationUtil.toValidResourceQuantityToKickOff(wheatString, usersWheatQuantity);
        int stoneQuantityToKickOff = validationUtil.toValidResourceQuantityToKickOff(stoneString, usersStoneQuantity);

        int sumOfResourcesKickingOff = brickQuantityToKickOff + woodQuantityToKickOff + sheepQuantityToKickOff + wheatQuantityToKickOff + stoneQuantityToKickOff;
        int sumOfUsersResources = gameUser.getAchievements().getTotalResources();
        validationUtil.validateSumOfResourcesToKickOffIsTheHalfOfTotalResources(sumOfUsersResources, sumOfResourcesKickingOff);

        usersResources.setBrick(usersBrickQuantity - brickQuantityToKickOff);
        usersResources.setWood(usersWoodQuantity - woodQuantityToKickOff);
        usersResources.setSheep(usersSheepQuantity - sheepQuantityToKickOff);
        usersResources.setWheat(usersWheatQuantity - wheatQuantityToKickOff);
        usersResources.setStone(usersStoneQuantity - stoneQuantityToKickOff);

        gameUser.setKickingOffResourcesMandatory(false);
        checkRobberShouldBeMovedMandatory(gameUser.getGame());
    }

    private void tradeResourcesInPort(GameUserBean gameUser, String brickString, String woodString, String sheepString, String wheatString, String stoneString) throws PlayException, GameException {
        Resources usersResources = gameUser.getResources();
        int usersBrickQuantity = usersResources.getBrick();
        int usersWoodQuantity = usersResources.getWood();
        int usersSheepQuantity = usersResources.getSheep();
        int usersWheatQuantity = usersResources.getWheat();
        int usersStoneQuantity = usersResources.getStone();

        ResourcesParams resourcesParams = actionParamsUtil.calculateTradePortParams(gameUser);
        int brickSellCoefficient = resourcesParams.getBrick();
        int woodSellCoefficient = resourcesParams.getWood();
        int sheepSellCoefficient = resourcesParams.getSheep();
        int wheatSellCoefficient = resourcesParams.getWheat();
        int stoneSellCoefficient = resourcesParams.getStone();

        int brickQuantityToTrade = validationUtil.toValidResourceQuantityToTradeInPort(brickString, usersBrickQuantity, brickSellCoefficient);
        int woodQuantityToTrade = validationUtil.toValidResourceQuantityToTradeInPort(woodString, usersWoodQuantity, woodSellCoefficient);
        int sheepQuantityToTrade = validationUtil.toValidResourceQuantityToTradeInPort(sheepString, usersSheepQuantity, sheepSellCoefficient);
        int wheatQuantityToTrade = validationUtil.toValidResourceQuantityToTradeInPort(wheatString, usersWheatQuantity, wheatSellCoefficient);
        int stoneQuantityToTrade = validationUtil.toValidResourceQuantityToTradeInPort(stoneString, usersStoneQuantity, stoneSellCoefficient);

        int tradingBalance = (brickQuantityToTrade < 0 ? brickQuantityToTrade / brickSellCoefficient : brickQuantityToTrade)
                + (woodQuantityToTrade < 0 ? woodQuantityToTrade / woodSellCoefficient : woodQuantityToTrade)
                + (sheepQuantityToTrade < 0 ? sheepQuantityToTrade / sheepSellCoefficient : sheepQuantityToTrade)
                + (wheatQuantityToTrade < 0 ? wheatQuantityToTrade / wheatSellCoefficient : wheatQuantityToTrade)
                + (stoneQuantityToTrade < 0 ? stoneQuantityToTrade / stoneSellCoefficient : stoneQuantityToTrade);
        validationUtil.validateTradeBalanceIsZero(tradingBalance);
        validationUtil.validateTradeIsNotEmpty(brickQuantityToTrade, woodQuantityToTrade, sheepQuantityToTrade, wheatQuantityToTrade, stoneQuantityToTrade);

        usersResources.setBrick(usersBrickQuantity + brickQuantityToTrade);
        usersResources.setWood(usersWoodQuantity + woodQuantityToTrade);
        usersResources.setSheep(usersSheepQuantity + sheepQuantityToTrade);
        usersResources.setWheat(usersWheatQuantity + wheatQuantityToTrade);
        usersResources.setStone(usersStoneQuantity + stoneQuantityToTrade);
    }

    private void proposeTrade(GameUserBean gameUser, String brickString, String woodString, String sheepString, String wheatString, String stoneString) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        Resources usersResources = gameUser.getResources();
        int brick = validationUtil.toValidResourceQuantityToTrade(brickString, usersResources.getBrick());
        int wood = validationUtil.toValidResourceQuantityToTrade(woodString, usersResources.getWood());
        int sheep = validationUtil.toValidResourceQuantityToTrade(sheepString, usersResources.getSheep());
        int wheat = validationUtil.toValidResourceQuantityToTrade(wheatString, usersResources.getWheat());
        int stone = validationUtil.toValidResourceQuantityToTrade(stoneString, usersResources.getStone());

        validationUtil.validateResourceQuantityToSellAndToBuyInTradePropose(brick, wood, sheep, wheat, stone);

        for (GameUserBean gameUserIterated : game.getGameUsers()) {
            if (!gameUserIterated.equals(gameUser)) {
                gameUserIterated.setAvailableTradeReply(true);
            }
        }

        Integer newOfferId = randomUtil.generateRandomOfferId(10000);
        game.setTradeProposal(new TradeProposal(brick, wood, sheep, wheat, stone, newOfferId));
    }

    private void tradeReply(GameUserBean gameUser, String reply, String offerIdString) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        Integer offerId = validationUtil.toValidNumber(offerIdString);
        TradeProposal tradeProposal = game.getTradeProposal();
        validationUtil.validateThereIsNoNewOffers(offerId, tradeProposal);
        gameUser.setAvailableTradeReply(false);

        if (reply.equals("decline")) {
            for (GameUserBean gameUserIterated : game.getGameUsers()) {
                if (gameUserIterated.isAvailableTradeReply()) {
                    return;
                }
            }
        }

        if (reply.equals("accept")) {
            validationUtil.validateOfferIsNotAcceptedBefore(tradeProposal);

            int brick = tradeProposal.getBrick();
            int wood = tradeProposal.getWood();
            int sheep = tradeProposal.getSheep();
            int wheat = tradeProposal.getWheat();
            int stone = tradeProposal.getStone();
            validationUtil.validateUserHasRequestedResources(gameUser, brick, wood, sheep, wheat, stone);

            game.fetchActiveGameUser().getResources().addResources(brick, wood, sheep, wheat, stone);
            gameUser.getResources().addResources(-brick, -wood, -sheep, -wheat, -stone);
        }

        tradeProposal.setOfferId(null);
    }

    private void checkRobberShouldBeMovedMandatory(GameBean game) {
        for (GameUserBean gameUserIterated : game.getGameUsers()) {
            if (gameUserIterated.isKickingOffResourcesMandatory()) {
                messagesUtil.updateDisplayedMessage(game.fetchActiveGameUser(), "help_msg_wait_for_kicking_off_res");
                return;
            }
        }
        game.setRobberShouldBeMovedMandatory(true);
        messagesUtil.updateDisplayedMessage(game.fetchActiveGameUser(), "help_msg_move_robber");
    }

    private void checkIfPlayersShouldKickOffResources(GameUserBean gameUser) {
        GameBean game = gameUser.getGame();
        boolean shouldResourcesBeKickedOff = false;
        for (GameUserBean gameUserIterated : game.getGameUsers()) {
            if (gameUserIterated.getAchievements().getTotalResources() > 7) {
                gameUserIterated.setKickingOffResourcesMandatory(true);
                shouldResourcesBeKickedOff = true;
            }
        }

        if (shouldResourcesBeKickedOff) {
            messagesUtil.updateDisplayedMessage(gameUser, "help_msg_wait_for_kicking_off_res");
            return;
        }
        game.setRobberShouldBeMovedMandatory(true);
        messagesUtil.updateDisplayedMessage(gameUser, "help_msg_move_robber");
    }

    private boolean isRobbersActivity(Integer diceFirstValue, Integer diceSecondValue) {
        return diceFirstValue + diceSecondValue == 7;
    }

    private Object getGameIdSyncObject(final Long id) {
        locks.putIfAbsent(id, id);

        return locks.get(id);
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
    public void setAchievementsUtil(AchievementsUtil achievementsUtil) {
        this.achievementsUtil = achievementsUtil;
    }

    @Autowired
    public void setActionParamsUtil(ActionParamsUtil actionParamsUtil) {
        this.actionParamsUtil = actionParamsUtil;
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
    public void setMessagesUtil(MessagesUtil messagesUtil) {
        this.messagesUtil = messagesUtil;
    }

    @Autowired
    public void setValidationUtil(ValidationUtil validationUtil) {
        this.validationUtil = validationUtil;
    }
}