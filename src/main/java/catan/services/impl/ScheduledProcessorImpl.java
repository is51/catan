package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.game.actions.ActionDetails;
import catan.domain.transfer.output.game.actions.AvailableActionsDetails;
import catan.services.PlayService;
import catan.services.ScheduledProcessor;
import catan.services.util.game.GameUtil;
import catan.services.util.play.PlayUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static catan.domain.model.game.types.GameUserActionCode.BUILD_CITY;
import static catan.domain.model.game.types.GameUserActionCode.BUILD_ROAD;
import static catan.domain.model.game.types.GameUserActionCode.BUILD_SETTLEMENT;
import static catan.domain.model.game.types.GameUserActionCode.BUY_CARD;
import static catan.domain.model.game.types.GameUserActionCode.TRADE_PORT;

@Service("scheduledProcessor")
@Transactional
public class ScheduledProcessorImpl implements ScheduledProcessor {
    @Autowired
    private GameDao gameDao;
    @Autowired
    private GameUtil gameUtil;
    @Autowired
    private PlayUtil playUtil;
    @Autowired
    PlayService playService;

    private Set<GameUserBean> automatedPlayers = new HashSet<GameUserBean>();

    @Override
    @Scheduled(fixedDelay = 1000)
    public void monitorPlayerAction() {
        for (GameUserBean player : automatedPlayers) {
            try {
                monitorPlayer(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void monitorPlayer(GameUserBean player) throws PlayException, GameException {
        GameBean game = gameDao.getGameByGameId(player.getGame().getGameId());
        GameUserBean refreshedPlayer = null;
        for (GameUserBean gameUserBean : game.getGameUsers()) {
            if (gameUserBean.getGameUserId().equals(player.getGameUserId())) {
                refreshedPlayer = gameUserBean;
            }
        }
        assert refreshedPlayer != null;

        String availableActionsJson = refreshedPlayer.getAvailableActions();
        AvailableActionsDetails availableActions = new Gson().fromJson(availableActionsJson, AvailableActionsDetails.class);
        //AvailableActions availableActions = playUtil.toAvailableActionsFromJson(availableActionsJson);

        String gameId = String.valueOf(game.getGameId());
        UserBean user = refreshedPlayer.getUser();
        boolean canBuy = false;
        for (ActionDetails action : availableActions.getList()) {
            if (action.getCode().equals(BUY_CARD.name())
                    || (action.getCode().equals(BUILD_ROAD.name()) && action.getParams().getEdgeIds().size() > 0)
                    || (action.getCode().equals(BUILD_SETTLEMENT.name()) && action.getParams().getNodeIds().size() > 0)
                    || (action.getCode().equals(BUILD_CITY.name()) && action.getParams().getNodeIds().size() > 0)) {
                canBuy = true;
                break;
            }
        }

        for (ActionDetails action : availableActions.getList()) {
            if (action.getCode().equals(TRADE_PORT.name()) && !canBuy) {
                tradePort(refreshedPlayer, gameId, user, action);
                break;
            }
        }

        for (ActionDetails action : availableActions.getList()) {
            if (action.getCode().equals(GameUserActionCode.END_TURN.name()) && !canBuy) {
                playService.processAction(GameUserActionCode.END_TURN, user, gameId);
                return;
            }

            if (action.getCode().equals(GameUserActionCode.THROW_DICE.name())) {
                playService.processAction(GameUserActionCode.THROW_DICE, user, gameId);
                return;
            }

            if (action.getCode().equals(GameUserActionCode.TRADE_REPLY.name())) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tradeReply", "decline");
                params.put("offerId", action.getParams().getOfferId().toString());

                playService.processAction(GameUserActionCode.TRADE_REPLY, user, gameId, params);
                return;
            }

            if (action.getCode().equals(GameUserActionCode.MOVE_ROBBER.name())) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hexId", action.getParams().getHexIds().get(0).toString());

                playService.processAction(GameUserActionCode.MOVE_ROBBER, user, gameId, params);
                return;
            }

            if (action.getCode().equals(GameUserActionCode.CHOOSE_PLAYER_TO_ROB.name())) {
                String gameUserId = "";
                for (NodeBean nodeBean : game.getNodes()) {
                    if (action.getParams().getNodeIds().get(0).equals(nodeBean.getAbsoluteId())) {
                        gameUserId = nodeBean.getBuilding().getBuildingOwner().getGameUserId().toString();
                    }
                }

                Map<String, String> params = new HashMap<String, String>();
                params.put("gameUserId", gameUserId);

                playService.processAction(GameUserActionCode.CHOOSE_PLAYER_TO_ROB, user, gameId, params);
                return;
            }

            if (action.getCode().equals(GameUserActionCode.KICK_OFF_RESOURCES.name())) {
                Integer sum = refreshedPlayer.getResources().getBrick() +
                        refreshedPlayer.getResources().getWood() +
                        refreshedPlayer.getResources().getSheep() +
                        refreshedPlayer.getResources().getWheat() +
                        refreshedPlayer.getResources().getStone();
                Integer halfSum = sum / 2;
                //brick=0, wood=2, sheep=1, wheat=4, stone=1}
                Integer brick = refreshedPlayer.getResources().getBrick() >= halfSum
                        ? halfSum
                        : refreshedPlayer.getResources().getBrick();
                Integer wood = refreshedPlayer.getResources().getWood() + brick >= halfSum
                        ? halfSum - brick
                        : refreshedPlayer.getResources().getWood();
                Integer sheep = refreshedPlayer.getResources().getSheep() + brick + wood >= halfSum
                        ? halfSum - brick - wood
                        : refreshedPlayer.getResources().getSheep();
                Integer wheat = refreshedPlayer.getResources().getWheat() + brick + wood + sheep >= halfSum
                        ? halfSum - brick - wood - sheep
                        : refreshedPlayer.getResources().getWheat();
                Integer stone = refreshedPlayer.getResources().getStone() + brick + wood + sheep + wheat >= halfSum
                        ? halfSum - brick - wood - sheep - wheat
                        : refreshedPlayer.getResources().getStone();

                Map<String, String> params = new HashMap<String, String>();
                params.put("brick", brick.toString());
                params.put("wood", wood.toString());
                params.put("sheep", sheep.toString());
                params.put("wheat", wheat.toString());
                params.put("stone", stone.toString());

                playService.processAction(GameUserActionCode.KICK_OFF_RESOURCES, user, gameId, params);
                return;
            }

            if (canBuy) {
                if (action.getCode().equals(BUILD_CITY.name()) && action.getParams().getNodeIds().size() > 0) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("nodeId", action.getParams().getNodeIds().get(0).toString());

                    playService.processAction(BUILD_CITY, user, gameId, params);
                    return;
                }

                if (action.getCode().equals(BUILD_SETTLEMENT.name()) && action.getParams().getNodeIds().size() > 0) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("nodeId", action.getParams().getNodeIds().get(0).toString());

                    playService.processAction(BUILD_SETTLEMENT, user, gameId, params);
                    return;
                }

                if (action.getCode().equals(BUILD_ROAD.name()) && action.getParams().getEdgeIds().size() > 0) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("edgeId", action.getParams().getEdgeIds().get(0).toString());

                    playService.processAction(BUILD_ROAD, user, gameId, params);
                    return;
                }

                if (action.getCode().equals(BUY_CARD.name())) {
                    playService.processAction(BUY_CARD, user, gameId);
                    return;
                }
            }
        }
    }

    private void tradePort(GameUserBean player, String gameId, UserBean user, ActionDetails action) throws PlayException, GameException {
        Map<String, Integer> sell = new HashMap<String, Integer>();
        Map<String, Integer> buy = new HashMap<String, Integer>();
        Integer sumSell = 0;
        Integer sumBuy = 0;

        if (player.getResources().getBrick() >= action.getParams().getBrick()) {
            int numberOrResourcesCanBuy = player.getResources().getBrick() / action.getParams().getBrick();
            int buyBricks = action.getParams().getBrick() * numberOrResourcesCanBuy;
            sell.put("brick", buyBricks);
            sumSell += numberOrResourcesCanBuy;
        }
        if (player.getResources().getWood() >= action.getParams().getWood()) {
            int numberOrResourcesCanBuy = player.getResources().getWood() / action.getParams().getWood();
            int buyWood = action.getParams().getWood() * numberOrResourcesCanBuy;
            sell.put("wood", buyWood);
            sumSell += numberOrResourcesCanBuy;
        }
        if (player.getResources().getSheep() >= action.getParams().getSheep()) {
            int numberOrResourcesCanBuy = player.getResources().getSheep() / action.getParams().getSheep();
            int buySheep = action.getParams().getSheep() * numberOrResourcesCanBuy;
            sell.put("sheep", buySheep);
            sumSell += numberOrResourcesCanBuy;

        }
        if (player.getResources().getWheat() >= action.getParams().getWheat()) {
            int numberOrResourcesCanBuy = player.getResources().getWheat() / action.getParams().getWheat();
            int buyWheat = action.getParams().getWheat() * numberOrResourcesCanBuy;
            sell.put("wheat", buyWheat);
            sumSell += numberOrResourcesCanBuy;
        }
        if (player.getResources().getStone() >= action.getParams().getStone()) {
            int numberOrResourcesCanBuy = player.getResources().getStone() / action.getParams().getStone();
            int buyStone = action.getParams().getStone() * numberOrResourcesCanBuy;
            sell.put("stone", buyStone);
            sumSell += numberOrResourcesCanBuy;
        }

        if (sumSell == 0) {
            return;
        }

        if (player.getResources().getBrick() == 0) {
            buy.put("brick", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getWood() == 0) {
            buy.put("wood", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getSheep() == 0) {
            buy.put("sheep", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getWheat() == 0) {
            buy.put("wheat", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getStone() == 0) {
            buy.put("stone", sumSell);
            sumBuy += sumSell;
        }

        if(!sumBuy.equals(sumSell)){
            if (!sell.keySet().contains("brick")) {
                buy.put("brick", sumSell);
                sumBuy += sumSell;
            } else if (!sell.keySet().contains("wood")) {
                buy.put("wood", sumSell);
                sumBuy += sumSell;
            } else if (!sell.keySet().contains("sheep")) {
                buy.put("sheep", sumSell);
                sumBuy += sumSell;
            } else if (!sell.keySet().contains("wheat")) {
                buy.put("wheat", sumSell);
                sumBuy += sumSell;
            } else if (!sell.keySet().contains("stone")) {
                buy.put("stone", sumSell);
                sumBuy += sumSell;
            }
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", buy.get("brick") != null ? buy.get("brick").toString() : (sell.get("brick") != null ? "-" + sell.get("brick") : "0"));
        params.put("wood", buy.get("wood") != null ? buy.get("wood").toString() : (sell.get("wood") != null ? "-" + sell.get("wood") : "0"));
        params.put("sheep", buy.get("sheep") != null ? buy.get("sheep").toString() : (sell.get("sheep") != null ? "-" + sell.get("sheep") : "0"));
        params.put("wheat", buy.get("wheat") != null ? buy.get("wheat").toString() : (sell.get("wheat") != null ? "-" + sell.get("wheat") : "0"));
        params.put("stone", buy.get("stone") != null ? buy.get("stone").toString() : (sell.get("stone") != null ? "-" + sell.get("stone") : "0"));

        playService.processAction(TRADE_PORT, user, gameId, params);
    }

    @Override
    public Set<GameUserBean> getAutomatedPlayers() {
        return automatedPlayers;
    }

    public class CustomDeserializer implements JsonDeserializer<AvailableActions> {

        @Override
        public AvailableActions deserialize(JsonElement json, Type typeOfT,
                                            JsonDeserializationContext context) throws JsonParseException {

            AvailableActions availableActions = new AvailableActions();
            availableActions.setList(new ArrayList<Action>());
            availableActions.setIsMandatory(false);

            JsonObject jo = json.getAsJsonObject();
            JsonArray list = jo.get("list").getAsJsonArray();

            for (JsonElement jsonElement : list) {
                JsonObject params = jsonElement.getAsJsonObject().get("params").getAsJsonObject();
                if (params.get("params") != null && params.get("params").getAsString().length() > 0) {


                }
                //availableActions.getList().add(context.deserialize(je, c);
                continue;
            }


            return availableActions;

        }
    }
}
