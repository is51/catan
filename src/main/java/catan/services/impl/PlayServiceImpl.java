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
import catan.services.util.play.MessagesUtil;
import catan.services.util.play.PlayUtil;
import catan.services.util.play.RobberUtil;
import catan.services.util.play.TradeUtil;
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

    private GameDao gameDao;
    private RandomUtil randomUtil;
    private GameUtil gameUtil;
    private PlayUtil playUtil;
    private BuildUtil buildUtil;
    private CardUtil cardUtil;
    private AchievementsUtil achievementsUtil;
    private ActionParamsUtil actionParamsUtil;
    private TradeUtil tradeUtil;
    private RobberUtil robberUtil;

    private ConcurrentMap<Long, Long> locks = new ConcurrentHashMap<Long, Long>();

    public static final String ERROR_CODE_ERROR = "ERROR";

    @Override
    public Map<String, String> processAction(GameUserActionCode action, UserBean user, String gameId) throws PlayException, GameException {
        return processAction(action, user, gameId, new HashMap<String, String>());
    }

    @Override
    public Map<String, String> processAction(GameUserActionCode action, UserBean user, String gameId, Map<String, String> params) throws PlayException, GameException {
        log.debug("Start process action {} " + (params != null && !params.isEmpty() ? "with params " + params : "without params") + " by {} for game with id: {}", action, user, gameId);
        Map<String, String> returnedParams = new HashMap<String, String>();

        playUtil.validateUserNotEmpty(user);
        playUtil.validateGameIdNotEmpty(gameId);

        synchronized (getGameIdSyncObject(Long.parseLong(gameId))) {
            GameBean game = gameUtil.getGameById(gameId);
            GameUserBean gameUser = gameUtil.getGameUserJoinedToGame(user, game);

            try {
                playUtil.validateGameStatusIsPlaying(game);
                playUtil.validateActionIsAllowedForUser(gameUser, action);

                doAction(action, gameUser, params, returnedParams);

                achievementsUtil.updateAchievements(game);
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
        buildUtil.validateUserCanBuildRoadOnEdge(edgeToBuildOn);
        buildUtil.buildRoadOnEdge(edgeToBuildOn);

        if (GameStage.MAIN.equals(game.getStage()) && game.getRoadsToBuildMandatory() == 0) {
            game.fetchActiveGameUser().getResources().addResources(-1, -1, 0, 0, 0);
        }
        playUtil.updateRoadsToBuildMandatory(game);
        achievementsUtil.updateLongestWayLength(gameUser);
    }

    private void buildSettlement(GameUserBean gameUser, String nodeId) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        NodeBean nodeToBuildOn = (NodeBean) buildUtil.getValidMapElementByIdToBuildOn(nodeId, new ArrayList<MapElement>(game.getNodes()));
        buildUtil.validateUserCanBuildSettlementOnNode(nodeToBuildOn);
        buildUtil.buildOnNode(nodeToBuildOn, NodeBuiltType.SETTLEMENT);

        if (GameStage.MAIN.equals(game.getStage())) {
            game.fetchActiveGameUser().getResources().addResources(-1, -1, -1, -1, 0);
        }
        playUtil.distributeResourcesForLastBuildingInPreparation(nodeToBuildOn);
        achievementsUtil.updateLongestWayLengthIfInterrupted(nodeToBuildOn);
    }

    private void buildCity(GameUserBean gameUser, String nodeId) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        NodeBean nodeToBuildOn = (NodeBean) buildUtil.getValidMapElementByIdToBuildOn(nodeId, new ArrayList<MapElement>(game.getNodes()));
        buildUtil.validateUserCanBuildCityOnNode(nodeToBuildOn);
        buildUtil.buildOnNode(nodeToBuildOn, NodeBuiltType.CITY);

        if (GameStage.MAIN.equals(game.getStage())) {
            game.fetchActiveGameUser().getResources().addResources(0, 0, 0, -2, -3);
        }
        playUtil.distributeResourcesForLastBuildingInPreparation(nodeToBuildOn);
    }

    private void endTurn(GameUserBean gameUser) throws GameException {
        GameBean game = gameUser.getGame();

        playUtil.resetDices(game);
        playUtil.resetDevCardUsage(gameUser);
        playUtil.resetTradeReplies(game);
        playUtil.updateNextMove(game);
        playUtil.updateGameStage(game);
    }

    private void throwDice(GameUserBean gameUser) {
        Integer diceFirstValue = randomUtil.getRandomDiceNumber();
        Integer diceSecondValue = randomUtil.getRandomDiceNumber();
        GameBean game = gameUser.getGame();

        playUtil.setDiceValues(diceFirstValue, diceSecondValue, game);
        robberUtil.activateRobberIfNeeded(game);
        playUtil.produceResourcesFromActiveDiceHexes(game);
    }

    private void buyCard(GameUserBean gameUser, Map<String, String> returnedParams) throws PlayException, GameException {
        GameBean game = gameUser.getGame();

        DevelopmentCard chosenDevelopmentCard = cardUtil.chooseDevelopmentCard(game);
        log.debug("Card " + chosenDevelopmentCard + " was chosen from the list: " + game.getAvailableDevelopmentCards());

        cardUtil.giveDevelopmentCardToUser(gameUser, chosenDevelopmentCard);

        returnedParams.put("card", chosenDevelopmentCard.name());

        if (GameStage.MAIN.equals(game.getStage())) {
            gameUser.getResources().addResources(0, 0, -1, -1, -1);
        }
    }

    private void useCardYearOfPlenty(GameUserBean gameUser, String firstResourceString, String secondResourceString) throws PlayException, GameException {
        cardUtil.validateUserDidNotUsedCardsInCurrentTurn(gameUser);
        cardUtil.validateUserDidNotBoughtCardInCurrentTurn(gameUser, DevelopmentCard.YEAR_OF_PLENTY);

        HexType firstResource = ValidationUtil.toValidResourceType(firstResourceString, log);
        HexType secondResource = ValidationUtil.toValidResourceType(secondResourceString, log);

        cardUtil.giveTwoResourcesToPlayer(gameUser.getResources(), firstResource, secondResource);
        log.debug("Player got resources: {}, {}", firstResource, secondResource);

        cardUtil.takeDevelopmentCardFromPlayer(gameUser, DevelopmentCard.YEAR_OF_PLENTY);
        gameUser.getGame().setDevelopmentCardUsed(true);
    }

    private void useCardMonopoly(GameUserBean gameUser, String resourceString, Map<String, String> returnedParams) throws PlayException, GameException {
        cardUtil.validateUserDidNotUsedCardsInCurrentTurn(gameUser);
        cardUtil.validateUserDidNotBoughtCardInCurrentTurn(gameUser, DevelopmentCard.MONOPOLY);

        HexType resource = ValidationUtil.toValidResourceType(resourceString, log);

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
        MessagesUtil.updateDisplayedMsg(gameUser, "help_msg_move_robber");
        game.setDevelopmentCardUsed(true);

        achievementsUtil.increaseTotalUsedKnightsByOne(gameUser);
        achievementsUtil.updateBiggestArmyOwner(gameUser);
    }

    private void moveRobber(GameUserBean gameUser, String hexAbsoluteId) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        HexBean hexToRob = robberUtil.toValidHex(game, hexAbsoluteId);
        robberUtil.validateHexCouldBeRobbed(hexToRob);

        robberUtil.changeRobbedHex(hexToRob);
        game.setRobberShouldBeMovedMandatory(false);
        MessagesUtil.clearUsersMsgs(game);

        List<GameUserBean> playersAtHex = new ArrayList<GameUserBean>(hexToRob.fetchGameUsersWithBuildingsAtNodes());
        playersAtHex.remove(gameUser);
        int playersToRobQuantity = playersAtHex.size();
        if (playersToRobQuantity == 1) {
            choosePlayerToRob(playersAtHex.get(0));
        }
        if (playersToRobQuantity > 1) {
            game.setChoosePlayerToRobMandatory(true);
            MessagesUtil.updateDisplayedMsg(gameUser, "help_msg_choose_player_to_rob");
        }
    }

    private void choosePlayerToRob(GameUserBean gameUser, String robbedGameUserIdString) throws PlayException, GameException {
        int robbedGameUserId = ValidationUtil.toValidNumber(robbedGameUserIdString, log);
        GameUserBean robbedGameUser = gameUtil.getGameUserById(robbedGameUserId, gameUser.getGame());
        choosePlayerToRob(robbedGameUser);
    }

    private void choosePlayerToRob(GameUserBean robbedGameUser) throws PlayException, GameException {
        GameBean game = robbedGameUser.getGame();
        Resources currentUsersResources = robbedGameUser.getGame().fetchActiveGameUser().getResources();
        log.info("Robbing {}", robbedGameUser);

        robberUtil.validateGameUserCouldBeRobbed(robbedGameUser);

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
        MessagesUtil.clearUsersMsgs(game);
    }

    private void kickOffResources(GameUserBean gameUser, String brickString, String woodString, String sheepString, String wheatString, String stoneString) throws PlayException, GameException {
        Resources usersResources = gameUser.getResources();
        int usersBrickQuantity = usersResources.getBrick();
        int usersWoodQuantity = usersResources.getWood();
        int usersSheepQuantity = usersResources.getSheep();
        int usersWheatQuantity = usersResources.getWheat();
        int usersStoneQuantity = usersResources.getStone();

        int brickQuantityToKickOff = robberUtil.toValidResourceQuantityToKickOff(brickString, usersBrickQuantity);
        int woodQuantityToKickOff = robberUtil.toValidResourceQuantityToKickOff(woodString, usersWoodQuantity);
        int sheepQuantityToKickOff = robberUtil.toValidResourceQuantityToKickOff(sheepString, usersSheepQuantity);
        int wheatQuantityToKickOff = robberUtil.toValidResourceQuantityToKickOff(wheatString, usersWheatQuantity);
        int stoneQuantityToKickOff = robberUtil.toValidResourceQuantityToKickOff(stoneString, usersStoneQuantity);

        int sumOfResourcesKickingOff = brickQuantityToKickOff + woodQuantityToKickOff + sheepQuantityToKickOff + wheatQuantityToKickOff + stoneQuantityToKickOff;
        int sumOfUsersResources = gameUser.getAchievements().getTotalResources();
        robberUtil.validateSumOfResourcesToKickOffIsTheHalfOfTotalResources(sumOfUsersResources, sumOfResourcesKickingOff);

        usersResources.setBrick(usersBrickQuantity - brickQuantityToKickOff);
        usersResources.setWood(usersWoodQuantity - woodQuantityToKickOff);
        usersResources.setSheep(usersSheepQuantity - sheepQuantityToKickOff);
        usersResources.setWheat(usersWheatQuantity - wheatQuantityToKickOff);
        usersResources.setStone(usersStoneQuantity - stoneQuantityToKickOff);

        gameUser.setKickingOffResourcesMandatory(false);
        robberUtil.checkRobberShouldBeMovedMandatory(gameUser.getGame());
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

        int brickQuantityToTrade = tradeUtil.toValidResourceQuantityToTradeInPort(brickString, usersBrickQuantity, brickSellCoefficient);
        int woodQuantityToTrade = tradeUtil.toValidResourceQuantityToTradeInPort(woodString, usersWoodQuantity, woodSellCoefficient);
        int sheepQuantityToTrade = tradeUtil.toValidResourceQuantityToTradeInPort(sheepString, usersSheepQuantity, sheepSellCoefficient);
        int wheatQuantityToTrade = tradeUtil.toValidResourceQuantityToTradeInPort(wheatString, usersWheatQuantity, wheatSellCoefficient);
        int stoneQuantityToTrade = tradeUtil.toValidResourceQuantityToTradeInPort(stoneString, usersStoneQuantity, stoneSellCoefficient);

        int tradingBalance = (brickQuantityToTrade < 0 ? brickQuantityToTrade / brickSellCoefficient : brickQuantityToTrade)
                + (woodQuantityToTrade < 0 ? woodQuantityToTrade / woodSellCoefficient : woodQuantityToTrade)
                + (sheepQuantityToTrade < 0 ? sheepQuantityToTrade / sheepSellCoefficient : sheepQuantityToTrade)
                + (wheatQuantityToTrade < 0 ? wheatQuantityToTrade / wheatSellCoefficient : wheatQuantityToTrade)
                + (stoneQuantityToTrade < 0 ? stoneQuantityToTrade / stoneSellCoefficient : stoneQuantityToTrade);
        tradeUtil.validateTradeBalanceIsZero(tradingBalance);
        tradeUtil.validateTradeIsNotEmpty(brickQuantityToTrade, woodQuantityToTrade, sheepQuantityToTrade, wheatQuantityToTrade, stoneQuantityToTrade);

        usersResources.setBrick(usersBrickQuantity + brickQuantityToTrade);
        usersResources.setWood(usersWoodQuantity + woodQuantityToTrade);
        usersResources.setSheep(usersSheepQuantity + sheepQuantityToTrade);
        usersResources.setWheat(usersWheatQuantity + wheatQuantityToTrade);
        usersResources.setStone(usersStoneQuantity + stoneQuantityToTrade);
    }

    private void proposeTrade(GameUserBean gameUser, String brickString, String woodString, String sheepString, String wheatString, String stoneString) throws PlayException, GameException {
        GameBean game = gameUser.getGame();
        Resources usersResources = gameUser.getResources();
        int brick = tradeUtil.toValidResourceQuantityToTrade(brickString, usersResources.getBrick());
        int wood = tradeUtil.toValidResourceQuantityToTrade(woodString, usersResources.getWood());
        int sheep = tradeUtil.toValidResourceQuantityToTrade(sheepString, usersResources.getSheep());
        int wheat = tradeUtil.toValidResourceQuantityToTrade(wheatString, usersResources.getWheat());
        int stone = tradeUtil.toValidResourceQuantityToTrade(stoneString, usersResources.getStone());

        tradeUtil.validateResourceQuantityToSellAndToBuyInTradePropose(brick, wood, sheep, wheat, stone);

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
        Integer offerId = ValidationUtil.toValidNumber(offerIdString, log);
        TradeProposal tradeProposal = game.getTradeProposal();
        tradeUtil.validateThereIsNoNewOffers(offerId, tradeProposal);
        gameUser.setAvailableTradeReply(false);

        if (reply.equals("decline")) {
            for (GameUserBean gameUserIterated : game.getGameUsers()) {
                if (gameUserIterated.isAvailableTradeReply()) {
                    return;
                }
            }
        }

        if (reply.equals("accept")) {
            tradeUtil.validateOfferIsNotAcceptedBefore(tradeProposal);

            int brick = tradeProposal.getBrick();
            int wood = tradeProposal.getWood();
            int sheep = tradeProposal.getSheep();
            int wheat = tradeProposal.getWheat();
            int stone = tradeProposal.getStone();
            tradeUtil.validateUserHasRequestedResources(gameUser, brick, wood, sheep, wheat, stone);

            game.fetchActiveGameUser().getResources().addResources(brick, wood, sheep, wheat, stone);
            gameUser.getResources().addResources(-brick, -wood, -sheep, -wheat, -stone);
        }

        tradeProposal.setOfferId(null);
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
    public void setTradeUtil(TradeUtil tradeUtil) {
        this.tradeUtil = tradeUtil;
    }

    @Autowired
    public void setRobberUtil(RobberUtil robberUtil) {
        this.robberUtil = robberUtil;
    }
}