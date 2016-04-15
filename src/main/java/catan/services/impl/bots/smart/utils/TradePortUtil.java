package catan.services.impl.bots.smart.utils;

import catan.domain.model.game.GameUserBean;
import catan.domain.transfer.output.game.actions.ActionDetails;

import java.util.HashMap;
import java.util.Map;

public class TradePortUtil extends SmartBotUtil {
    public static Map<String, String> calculateTradePortResourceCombination(GameUserBean player, ActionDetails action) {
        Map<String, String> tradeResourceDirections = new HashMap<String, String>();

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
            return tradeResourceDirections;
        }

        if (player.getResources().getStone() == 0) {
            buy.put("stone", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getWheat() == 0) {
            buy.put("wheat", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getSheep() == 0) {
            buy.put("sheep", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getWood() == 0) {
            buy.put("wood", sumSell);
            sumBuy += sumSell;
        } else if (player.getResources().getBrick() == 0) {
            buy.put("brick", sumSell);
            sumBuy += sumSell;
        }

        if (!sumBuy.equals(sumSell)) {
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

        tradeResourceDirections.put("brick", buy.get("brick") != null
                ? buy.get("brick").toString()
                : (sell.get("brick") != null ? "-" + sell.get("brick") : "0"));
        tradeResourceDirections.put("wood", buy.get("wood") != null
                ? buy.get("wood").toString()
                : (sell.get("wood") != null ? "-" + sell.get("wood") : "0"));
        tradeResourceDirections.put("sheep", buy.get("sheep") != null
                ? buy.get("sheep").toString()
                : (sell.get("sheep") != null ? "-" + sell.get("sheep") : "0"));
        tradeResourceDirections.put("wheat", buy.get("wheat") != null
                ? buy.get("wheat").toString()
                : (sell.get("wheat") != null ? "-" + sell.get("wheat") : "0"));
        tradeResourceDirections.put("stone", buy.get("stone") != null
                ? buy.get("stone").toString()
                : (sell.get("stone") != null ? "-" + sell.get("stone") : "0"));

        return tradeResourceDirections;
    }
}
