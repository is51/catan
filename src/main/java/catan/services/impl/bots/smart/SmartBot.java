package catan.services.impl.bots.smart;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
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
                                      ActionDetails endTurnAction, boolean cardsAreOver) throws PlayException, GameException {
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
            //TODO: kickoff not needed resources
            log.debug("{}: kick off resources, for player {} in game {}", botName, username, gameId);
            kickOffResources(player, gameId);
            return;
        }

        if (useCardKnightAction != null) {
            log.debug("{}: try use card KNIGHT, for player {} in game {}", botName, username, gameId);
            boolean cardUsed = useCardKnight(player, gameId);
            if (cardUsed) {
                return;
            }
            log.debug("{}: card KNIGHT was not used, for player {} in game {}", botName, username, gameId);

        }

        if (useCardRoadBuildingAction != null) {
            log.debug("{}: try use card ROAD_BUILDING, for player {} in game {}", botName, username, gameId);
            //TODO: use useCardRoadBuilding if possible
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
            //TODO: use useCardMonopoly if possible
            boolean cardUsed = useCardMonopoly(player, gameId);
            if (cardUsed) {
                return;
            }
            log.debug("{}: card MONOPOLY was not used, for player {} in game {}", botName, username, gameId);
        }

        if (throwDiceAction != null) {
            log.debug("{}: throw dice, for player {} in game {}", botName, username, gameId);
            //TODO: use knight if possible
            throwDice(player, gameId);
            return;
        }

        if (buildCityAction != null) {
            log.debug("{}: build city, for player {} in game {}", botName, username, gameId);
            buildCity(player, gameId, buildCityAction);
            return;
        }

        if (buildSettlementAction != null) {
            log.debug("{}: build settlement, for player {} in game {}", botName, username, gameId);
            buildSettlement(player, gameId, buildSettlementAction);
            return;
        }

        if (buildRoadAction != null) {
            log.debug("{}: try build road, for player {} in game {}", botName, username, gameId);
            boolean roadBuilt = buildRoad(player, gameId, buildRoadAction);
            if (roadBuilt) {
                return;
            }
            log.debug("{}: road was not build, for player {} in game {}", botName, username, gameId);
        }

        if (buyCardAction != null && !cardsAreOver) {
            //TODO: Buy card if needed
            log.debug("{}: buy card, for player {} in game {}", botName, username, gameId);
            buyCard(player, gameId);
            return;
        }

        if (tradePortAction != null) {
            log.debug("{}: try trade with port, for player {} in game {}", botName, username, gameId);
            //TODO: trade required resources
            boolean tradeSuccessful = tradePort(player, gameId, tradePortAction);
            if (tradeSuccessful) {
                return;
            }
            log.debug("{}: trade was not performed, for player {} in game {}", botName, username, gameId);
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

    private boolean useCardMonopoly(GameUserBean player, String gameId) throws PlayException, GameException {
        try {
            playService.processAction(USE_CARD_MONOPOLY, player.getUser(), gameId);
            return true;
        } catch (PlayException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean useCardYearOfPlenty(GameUserBean player, String gameId) throws GameException {
        try {
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

    private boolean useCardKnight(GameUserBean player, String gameId) throws GameException {
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

        if (shouldUseKnightCard || player.getDevelopmentCards().getKnight() > 1) {
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

    private boolean buildRoad(GameUserBean player, String gameId, ActionDetails buildRoadAction) throws PlayException, GameException {
        log.debug("{}: end turn, for player {} in game {}", getBotName(), player.getUser(), player.getGame().getGameId());
        if (BuildRoadUtil.hasPlaceForNextBuilding(player)) {
            log.debug("{}: player already has place for next building, skip build road to save resources, for player {} in game {}",
                    getBotName(), player.getUser(), player.getGame().getGameId());
            return false;
        }

        List<Integer> edgeIds = buildRoadAction.getParams().getEdgeIds();
        String edgeIdToBuildRoad = BuildRoadUtil.getEdgeIdOfBestPlaceToBuildRoad(player, 2, edgeIds);

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
        Map<String, String> resourcesKickOffCount = KickOffResourcesUtil.getResourcesToKickOff(player);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", resourcesKickOffCount.get("brick"));
        params.put("wood", resourcesKickOffCount.get("wood"));
        params.put("sheep", resourcesKickOffCount.get("sheep"));
        params.put("wheat", resourcesKickOffCount.get("wheat"));
        params.put("stone", resourcesKickOffCount.get("stone"));

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

    private boolean tradePort(GameUserBean player, String gameId, ActionDetails action) throws PlayException, GameException {
        Map<String, String> tradeResourceDirections = TradePortUtil.calculateTradePortResourceCombination(player, action);
        if (tradeResourceDirections.isEmpty()) {
            return false;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", tradeResourceDirections.get("brick"));
        params.put("wood", tradeResourceDirections.get("wood"));
        params.put("sheep", tradeResourceDirections.get("sheep"));
        params.put("wheat", tradeResourceDirections.get("wheat"));
        params.put("stone", tradeResourceDirections.get("stone"));

        playService.processAction(TRADE_PORT, player.getUser(), gameId, params);
        return true;
    }

}
