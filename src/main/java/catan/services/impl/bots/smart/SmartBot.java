package catan.services.impl.bots.smart;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.game.actions.ActionDetails;
import catan.services.impl.bots.AbstractBot;
import catan.services.impl.bots.smart.utils.BuildCityUtil;
import catan.services.impl.bots.smart.utils.BuildRoadUtil;
import catan.services.impl.bots.smart.utils.BuildSettlementUtil;
import catan.services.impl.bots.smart.utils.ChoosePlayerToRobUtil;
import catan.services.impl.bots.smart.utils.KickOffResourcesUtil;
import catan.services.impl.bots.smart.utils.MoveRobberUtil;
import catan.services.impl.bots.smart.utils.TradePortUtil;
import catan.services.impl.bots.smart.utils.UseCardMonopolyUtil;
import catan.services.impl.bots.smart.utils.UseCardYearOfPlentyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static catan.domain.model.game.types.GameUserActionCode.BUILD_CITY;
import static catan.domain.model.game.types.GameUserActionCode.BUILD_ROAD;
import static catan.domain.model.game.types.GameUserActionCode.BUILD_SETTLEMENT;
import static catan.domain.model.game.types.GameUserActionCode.BUY_CARD;
import static catan.domain.model.game.types.GameUserActionCode.CHOOSE_PLAYER_TO_ROB;
import static catan.domain.model.game.types.GameUserActionCode.END_TURN;
import static catan.domain.model.game.types.GameUserActionCode.KICK_OFF_RESOURCES;
import static catan.domain.model.game.types.GameUserActionCode.MOVE_ROBBER;
import static catan.domain.model.game.types.GameUserActionCode.THROW_DICE;
import static catan.domain.model.game.types.GameUserActionCode.TRADE_PORT;
import static catan.domain.model.game.types.GameUserActionCode.TRADE_REPLY;
import static catan.domain.model.game.types.GameUserActionCode.USE_CARD_KNIGHT;
import static catan.domain.model.game.types.GameUserActionCode.USE_CARD_MONOPOLY;
import static catan.domain.model.game.types.GameUserActionCode.USE_CARD_ROAD_BUILDING;
import static catan.domain.model.game.types.GameUserActionCode.USE_CARD_YEAR_OF_PLENTY;

@Service("smartBot")
public class SmartBot extends AbstractBot {
    private Logger log = LoggerFactory.getLogger(SmartBot.class);

    @Override
    public String getBotName() {
        return "SMART_BOT";
    }

    @Override
    public void processActionsInOrder(GameUserBean player, UserBean user, String gameId,
                                      ActionDetails moveRobberAction, ActionDetails choosePlayerToRobAction,
                                      ActionDetails kickOffResourcesAction, ActionDetails useCardKnightAction,
                                      ActionDetails useCardRoadBuildingAction, ActionDetails useCardYearOfPlentyAction,
                                      ActionDetails useCardMonopolyAction, ActionDetails throwDiceAction,
                                      ActionDetails buildCityAction, ActionDetails buildSettlementAction,
                                      ActionDetails buildRoadAction, ActionDetails buyCardAction,
                                      ActionDetails tradePortAction, ActionDetails tradeReplyAction,
                                      ActionDetails endTurnAction, boolean isMandatory, boolean cardsAreOver) throws PlayException, GameException {
        String botName = getBotName();
        UserBean username = player.getUser();

        log.debug("{}: check available actions for player {} in game {}", botName, username, gameId);

        if (moveRobberAction != null) {
            log.debug("{}: move robber, for player {} in game {}", botName, username, gameId);
            moveRobber(player, gameId, moveRobberAction);
            return;
        }

        if (choosePlayerToRobAction != null) {
            log.debug("{}: choose player to rob, for player {} in game {}", botName, username, gameId);
            choosePlayerToRob(player, gameId, choosePlayerToRobAction);
            return;
        }

        if (kickOffResourcesAction != null) {
            log.debug("{}: kick off resources, for player {} in game {}", botName, username, gameId);
            kickOffResources(player, gameId);
            return;
        }

        if (useCardKnightAction != null) {
            log.debug("{}: try use card KNIGHT, for player {} in game {}", botName, username, gameId);
            boolean diceThrown = throwDiceAction == null;
            boolean cardUsed = useCardKnight(player, gameId, diceThrown);
            if (cardUsed) {
                return;
            }
            log.debug("{}: card KNIGHT was not used, for player {} in game {}", botName, username, gameId);

        }

        if (throwDiceAction != null) {
            log.debug("{}: throw dice, for player {} in game {}", botName, username, gameId);
            throwDice(player, gameId);
            return;
        }

        if (tradePortAction != null) {
            log.debug("{}: try trade with port, for player {} in game {}", botName, username, gameId);
            boolean tradeSuccessful = tradePort(player, gameId, tradePortAction, cardsAreOver);
            if (tradeSuccessful) {
                return;
            }
            log.debug("{}: trade was not performed, for player {} in game {}", botName, username, gameId);
        }

        if (buildCityAction != null) {
            log.debug("{}: build city, for player {} in game {}", botName, username, gameId);
            buildCity(player, gameId, buildCityAction);
            return;
        }

        if (buildSettlementAction != null && (isMandatory || player.getBuildingsCount().getSettlements() < 5)) {
            log.debug("{}: build settlement, for player {} in game {}", botName, username, gameId);
            buildSettlement(player, gameId, buildSettlementAction);
            return;
        }

        if (buildRoadAction != null) {
            boolean needIncreaseLongestRoad = needIncreaseLongestRoad(player);

            boolean needRoadForNewSettlement = true;
            for (NodeBean node : player.getGame().getNodes()) {
                if (node.couldBeUsedForBuildingSettlementByGameUserInMainStage(player)) {
                    needRoadForNewSettlement = false;
                    break;
                }
            }

            boolean enoughResourcesForSettlement = false;
            if(player.getResources().getWood() > 1 && player.getResources().getBrick() > 1){
                enoughResourcesForSettlement = true;
            }

            if (isMandatory
                || (player.getBuildingsCount().getSettlements() < 5 && needRoadForNewSettlement)
                || (player.getBuildingsCount().getSettlements() < 5 && enoughResourcesForSettlement)
                || (player.getBuildingsCount().getSettlements() >= 5 && needIncreaseLongestRoad)) {
                log.debug("{}: try build road, for player {} in game {}", botName, username, gameId);
                boolean roadBuilt = buildRoad(player, gameId, buildRoadAction, isMandatory);
                if (roadBuilt) {
                    return;
                }

                log.debug("{}: road was not build, for player {} in game {}", botName, username, gameId);
            }
        }

        if (buyCardAction != null && !cardsAreOver) {
            //TODO: Buy card only if I am not going to build settlement or city soon
            log.debug("{}: buy card, for player {} in game {}", botName, username, gameId);
            buyCard(player, gameId);
            return;
        }

        if (useCardRoadBuildingAction != null) {
            log.debug("{}: try use card ROAD_BUILDING, for player {} in game {}", botName, username, gameId);
            boolean cardUsed = useCardRoadBuilding(player, gameId);
            if (cardUsed) {
                return;
            }
            log.debug("{}: card ROAD_BUILDING was not used, for player {} in game {}", botName, username, gameId);
        }

        if (useCardYearOfPlentyAction != null) {
            log.debug("{}: try use card YEAR_OF_PLENTY, for player {} in game {}", botName, username, gameId);
            //TODO: use useCardYearOfPlenty if possible
            boolean cardUsed = useCardYearOfPlenty(player, gameId);
            if (cardUsed) {
                return;
            }
            log.debug("{}: card YEAR_OF_PLENTY was not used, for player {} in game {}", botName, username, gameId);
        }

        if (useCardMonopolyAction != null) {
            log.debug("{}: try use card MONOPOLY, for player {} in game {}", botName, username, gameId);
            boolean cardUsed = useCardMonopoly(player, gameId);
            if (cardUsed) {
                return;
            }
            log.debug("{}: card MONOPOLY was not used, for player {} in game {}", botName, username, gameId);
        }

        if (tradeReplyAction != null) {
            //TODO: Accept trade if it is useful for player
            log.debug("{}: trade reply, for player {} in game {}", botName, username, gameId);
            tradeReply(player, gameId, tradeReplyAction);
            return;
        }


        if (endTurnAction != null) {
            log.debug("{}: end turn, for player {} in game {}", botName, username, gameId);
            endTurn(player, gameId);
            return;
        }

    }

    public static boolean needIncreaseLongestRoad(GameUserBean player) {
        boolean needBuildRoad = true;

        String myUsername = player.getUser().getUsername();
        GameUserBean longestWayOwner = player.getGame().getLongestWayOwner();
        if (longestWayOwner != null && myUsername.equals(longestWayOwner.getUser().getUsername())) {
            int longestWayAfterMine = 0;
            for (GameUserBean gameUser : player.getGame().getGameUsers()) {
                int longestWayOfThisPlayer = gameUser.getAchievements().getLongestWayLength();
                if (!gameUser.getUser().getUsername().equals(myUsername) && longestWayOfThisPlayer > longestWayAfterMine) {
                    longestWayAfterMine = longestWayOfThisPlayer;
                }
            }

            if (player.getAchievements().getLongestWayLength() > longestWayAfterMine + 2) {
                // skip building longest road and focus on buying cards and trading if I am owner of longest road
                // and it is long enough so that other players will not build longer road
                needBuildRoad = false;
            }
        }

        return needBuildRoad;
    }

    private boolean useCardMonopoly(GameUserBean player, String gameId) throws PlayException, GameException {
        try {
            String resource = UseCardMonopolyUtil.getRequiredResourceFromAllPlayers(player);

            Map<String, String> params = new HashMap<String, String>();
            params.put("resource", resource);

            playService.processAction(USE_CARD_MONOPOLY, player.getUser(), gameId, params);
            return true;
        } catch (PlayException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean useCardYearOfPlenty(GameUserBean player, String gameId) throws GameException {
        try {
            List<String> resources = UseCardYearOfPlentyUtil.getTwoRequiredResources(player);

            Map<String, String> params = new HashMap<String, String>();
            params.put("firstResource", resources.get(0));
            params.put("secondResource", resources.get(1));

            playService.processAction(USE_CARD_MONOPOLY, player.getUser(), gameId, params);
            playService.processAction(USE_CARD_YEAR_OF_PLENTY, player.getUser(), gameId);
            return true;
        } catch (PlayException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean useCardRoadBuilding(GameUserBean player, String gameId) throws GameException {
        try {
            playService.processAction(USE_CARD_ROAD_BUILDING, player.getUser(), gameId);
            return true;
        } catch (PlayException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean useCardKnight(GameUserBean player, String gameId, boolean diceThrown) throws GameException {
        GameBean game = player.getGame();
        boolean shouldUseKnightCard = false;
        for (HexBean hex : game.getHexes()) {
            if (!hex.isRobbed()) {
                continue;
            }

            for (NodeBean node : hex.getNodes().listAllNotNullItems()) {
                if (node.hasBuildingBelongsToUser(player)) {
                    shouldUseKnightCard = true;
                }
            }

        }

        if (shouldUseKnightCard || (player.getDevelopmentCards().getKnight() > 1 && diceThrown)) {
            try {
                playService.processAction(USE_CARD_KNIGHT, player.getUser(), gameId);
                return true;
            } catch (PlayException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void buyCard(GameUserBean player, String gameId) throws GameException, PlayException {
        playService.processAction(BUY_CARD, player.getUser(), gameId);
    }

    private boolean buildRoad(GameUserBean player, String gameId, ActionDetails buildRoadAction, boolean isMandatory) throws PlayException, GameException {
        log.debug("{}: build road, for player {} in game {}", getBotName(), player.getUser(), player.getGame().getGameId());

        List<Integer> edgeIds = buildRoadAction.getParams().getEdgeIds();
        String edgeIdToBuildRoad = BuildRoadUtil.getEdgeIdOfBestPlaceToBuildRoad(player, 3, edgeIds);

        Map<String, String> params = new HashMap<String, String>();
        params.put("edgeId", edgeIdToBuildRoad);

        playService.processAction(BUILD_ROAD, player.getUser(), gameId, params);
        return true;
    }

    private void buildSettlement(GameUserBean player, String gameId, ActionDetails buildSettlementAction) throws PlayException, GameException {
        List<Integer> nodeIdsToBuildSettlement = buildSettlementAction.getParams().getNodeIds();
        String nodeIdToBuildSettlement = BuildSettlementUtil.geNodeIdOfBestPlaceToBuildSettlement(player, nodeIdsToBuildSettlement);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", nodeIdToBuildSettlement);

        GameUserActionCode buildSettlementActionCode = BUILD_SETTLEMENT;
        playService.processAction(buildSettlementActionCode, player.getUser(), gameId, params);
    }

    private void buildCity(GameUserBean player, String gameId, ActionDetails buildCityAction) throws PlayException, GameException {
        List<Integer> nodeIdsToBuildCity = buildCityAction.getParams().getNodeIds();
        String nodeIdToBuildCity = BuildCityUtil.geNodeIdOfBestSettlementToBuildCity(player, nodeIdsToBuildCity);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", nodeIdToBuildCity);

        playService.processAction(BUILD_CITY, player.getUser(), gameId, params);
    }

    private void moveRobber(GameUserBean player, String gameId, ActionDetails moveRobberAction) throws PlayException, GameException {
        List<Integer> hexIdsToMoveRob = moveRobberAction.getParams().getHexIds();
        int hexIdWithMaxProbability = MoveRobberUtil.getHexIdOfHexWithBestProfitToMoveRobber(player, hexIdsToMoveRob);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", String.valueOf(hexIdWithMaxProbability));

        playService.processAction(MOVE_ROBBER, player.getUser(), gameId, params);
    }

    private void kickOffResources(GameUserBean player, String gameId) throws PlayException, GameException {
        Map<HexType, String> resourcesKickOffCount = KickOffResourcesUtil.getResourcesToKickOff(player);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", resourcesKickOffCount.get(HexType.BRICK) != null ? resourcesKickOffCount.get(HexType.BRICK) : "0");
        params.put("wood", resourcesKickOffCount.get(HexType.WOOD) != null ? resourcesKickOffCount.get(HexType.WOOD) : "0");
        params.put("sheep", resourcesKickOffCount.get(HexType.SHEEP) != null ? resourcesKickOffCount.get(HexType.SHEEP) : "0");
        params.put("wheat", resourcesKickOffCount.get(HexType.WHEAT) != null ? resourcesKickOffCount.get(HexType.WHEAT) : "0");
        params.put("stone", resourcesKickOffCount.get(HexType.STONE) != null ? resourcesKickOffCount.get(HexType.STONE) : "0");

        playService.processAction(KICK_OFF_RESOURCES, player.getUser(), gameId, params);
    }

    private void choosePlayerToRob(GameUserBean player, String gameId, ActionDetails choosePlayerToRobAction) throws PlayException, GameException {
        String gameUserId = ChoosePlayerToRobUtil.getGameUserIdOfPlayerToRob(player, choosePlayerToRobAction);

        Map<String, String> params = new HashMap<String, String>();
        params.put("gameUserId", gameUserId);

        playService.processAction(CHOOSE_PLAYER_TO_ROB, player.getUser(), gameId, params);
    }

    private void throwDice(GameUserBean player, String gameId) throws PlayException, GameException {
        playService.processAction(THROW_DICE, player.getUser(), gameId);
    }

    private void endTurn(GameUserBean player, String gameId) throws PlayException, GameException {
        playService.processAction(END_TURN, player.getUser(), gameId);
    }

    private void tradeReply(GameUserBean player, String gameId, ActionDetails tradeReplyAction) throws PlayException, GameException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("tradeReply", "decline");
        params.put("offerId", tradeReplyAction.getParams().getOfferId().toString());

        playService.processAction(TRADE_REPLY, player.getUser(), gameId, params);
    }

    private boolean tradePort(GameUserBean player, String gameId, ActionDetails action, boolean cardsAreOver) throws PlayException, GameException {
        Map<HexType, String> tradeResourceCombination = TradePortUtil.calculateTradePortResourceCombination(player, action, cardsAreOver);
        if (tradeResourceCombination.isEmpty()) {
            return false;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", tradeResourceCombination.get(HexType.BRICK) != null ? tradeResourceCombination.get(HexType.BRICK) : "0");
        params.put("wood", tradeResourceCombination.get(HexType.WOOD) != null ? tradeResourceCombination.get(HexType.WOOD) : "0");
        params.put("sheep", tradeResourceCombination.get(HexType.SHEEP) != null ? tradeResourceCombination.get(HexType.SHEEP) : "0");
        params.put("wheat", tradeResourceCombination.get(HexType.WHEAT) != null ? tradeResourceCombination.get(HexType.WHEAT) : "0");
        params.put("stone", tradeResourceCombination.get(HexType.STONE) != null ? tradeResourceCombination.get(HexType.STONE) : "0");

        playService.processAction(TRADE_PORT, player.getUser(), gameId, params);
        return true;
    }

}
