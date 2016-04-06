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
        boolean canBuy = false;
        for (ActionDetails action : availableActions.getList()) {
            if (action.getCode().equals(GameUserActionCode.BUILD_ROAD.name())
                    || action.getCode().equals(GameUserActionCode.BUILD_SETTLEMENT.name())
                    || action.getCode().equals(GameUserActionCode.BUILD_CITY.name())
                    || action.getCode().equals(GameUserActionCode.BUY_CARD.name())) {
                canBuy = true;
                break;
            }
        }
        for (ActionDetails action : availableActions.getList()) {
            if (action.getCode().equals(GameUserActionCode.END_TURN.name()) && !canBuy) {
                playService.processAction(GameUserActionCode.END_TURN, refreshedPlayer.getUser(), String.valueOf(game.getGameId()));
                return;
            }

            if (action.getCode().equals(GameUserActionCode.THROW_DICE.name())) {
                playService.processAction(GameUserActionCode.THROW_DICE, refreshedPlayer.getUser(), String.valueOf(game.getGameId()));
                return;
            }

            if (action.getCode().equals(GameUserActionCode.TRADE_REPLY.name())) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tradeReply", "decline");
                params.put("offerId", action.getParams().getOfferId().toString());

                playService.processAction(GameUserActionCode.TRADE_REPLY, refreshedPlayer.getUser(), String.valueOf(game.getGameId()), params);
                return;
            }

            if (action.getCode().equals(GameUserActionCode.MOVE_ROBBER.name())) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hexId", action.getParams().getHexIds().get(0).toString());

                playService.processAction(GameUserActionCode.MOVE_ROBBER, refreshedPlayer.getUser(), String.valueOf(game.getGameId()), params);
                return;
            }

            if (action.getCode().equals(GameUserActionCode.CHOOSE_PLAYER_TO_ROB.name())) {
                String gameUserId = "";
                for (NodeBean nodeBean : game.getNodes()) {
                    if (action.getParams().getNodeIds().get(0).equals(nodeBean.getId())) {
                        gameUserId = nodeBean.getBuilding().getBuildingOwner().getGameUserId().toString();
                    }
                }

                Map<String, String> params = new HashMap<String, String>();
                params.put("gameUserId", gameUserId);

                playService.processAction(GameUserActionCode.CHOOSE_PLAYER_TO_ROB, refreshedPlayer.getUser(), String.valueOf(game.getGameId()), params);
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

                playService.processAction(GameUserActionCode.KICK_OFF_RESOURCES, refreshedPlayer.getUser(), String.valueOf(game.getGameId()), params);
                return;
            }

            if (action.getCode().equals(GameUserActionCode.BUILD_CITY.name())) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("nodeId", action.getParams().getNodeIds().get(0).toString());

                playService.processAction(GameUserActionCode.BUILD_CITY, refreshedPlayer.getUser(), String.valueOf(game.getGameId()), params);
                return;
            }

            if (action.getCode().equals(GameUserActionCode.BUILD_SETTLEMENT.name())) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("nodeId", action.getParams().getNodeIds().get(0).toString());

                playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, refreshedPlayer.getUser(), String.valueOf(game.getGameId()), params);
                return;
            }

            if (action.getCode().equals(GameUserActionCode.BUILD_ROAD.name())) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("edgeId", action.getParams().getEdgeIds().get(0).toString());

                playService.processAction(GameUserActionCode.BUILD_ROAD, refreshedPlayer.getUser(), String.valueOf(game.getGameId()), params);
                return;
            }

            if (action.getCode().equals(GameUserActionCode.BUY_CARD.name())) {
                playService.processAction(GameUserActionCode.BUY_CARD, refreshedPlayer.getUser(), String.valueOf(game.getGameId()));
                return;
            }
            //"TRADE_PORT","params":{"brick":4,"wood":4,"sheep":4,"wheat":4,"stone":4}
            if (action.getCode().equals(GameUserActionCode.BUY_CARD.name())) {
                playService.processAction(GameUserActionCode.BUY_CARD, refreshedPlayer.getUser(), String.valueOf(game.getGameId()));
                return;
            }
        }
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
                if(params.get("params") != null && params.get("params").getAsString().length() > 0){


                }
                //availableActions.getList().add(context.deserialize(je, c);
                continue;
            }


            return availableActions;

        }
    }
}
