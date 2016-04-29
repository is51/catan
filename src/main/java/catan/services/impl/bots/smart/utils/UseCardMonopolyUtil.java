package catan.services.impl.bots.smart.utils;

import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameUserBean;
import catan.domain.transfer.output.game.actions.ActionDetails;
import catan.domain.transfer.output.game.actions.ActionParamsDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UseCardMonopolyUtil extends SmartBotUtil {
    public static String getRequiredResourceFromAllPlayers(GameUserBean player) {

        return "stone";
    }

    public static Map<HexType, String> calculateTradePortResourceCombination(GameUserBean player, ActionDetails action) {
        if(needOneResourceForSettlement(player)){
            return combinationIfNeedResourcesForSettlement(player, action.getParams());
        } else if(needOneTypeResourceForCity(player)){
            return combinationIfNeedResourcesForCity(player, action.getParams());
        } else if(needOneResourceForRoad(player)){
            return combinationIfNeedResourcesForRoad(player, action.getParams());
        } else if(needOneResourceForCard(player)){
            return combinationIfNeedResourcesForCard(player, action.getParams());
        } else {
            return combinationIfNeedRareResource(player, action.getParams());
        }
    }

    private static Map<HexType, String> combinationIfNeedRareResource(GameUserBean player, ActionParamsDetails params) {
        return null;
    }

    private static Map<HexType, String> combinationIfNeedResourcesForCard(GameUserBean player, ActionParamsDetails params) {
        return null;
    }

    private static Map<HexType, String> combinationIfNeedResourcesForRoad(GameUserBean player, ActionParamsDetails params) {
        return null;
    }

    private static Map<HexType, String> combinationIfNeedResourcesForSettlement(GameUserBean player, ActionParamsDetails params) {
        List<HexType> requiredResources = requiredResourcesForSettlement(player);
        Map<HexType, String> tradeResourceCombination = new HashMap<HexType, String>();

        combinationForResource(requiredResources, tradeResourceCombination, HexType.STONE, 0, params.getStone(), player.getResources().getStone());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.BRICK, 1, params.getBrick(), player.getResources().getBrick());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.WOOD, 1, params.getWood(), player.getResources().getWood());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.SHEEP, 1, params.getSheep(), player.getResources().getSheep());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.WHEAT, 1, params.getWheat(), player.getResources().getBrick());

        return tradeResourceCombination;
    }


    private static Map<HexType, String> combinationIfNeedResourcesForCity(GameUserBean player, ActionParamsDetails params) {
        List<HexType> requiredResources = requiredResourcesForCity(player);
        Map<HexType, String> tradeResourceCombination = new HashMap<HexType, String>();

        combinationForResource(requiredResources, tradeResourceCombination, HexType.BRICK, 0, params.getBrick(), player.getResources().getBrick());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.WOOD, 0, params.getWood(), player.getResources().getWood());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.SHEEP, 0, params.getSheep(), player.getResources().getSheep());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.WHEAT, 2, params.getWheat(), player.getResources().getWheat());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.STONE, 3, params.getStone(), player.getResources().getStone());

        return tradeResourceCombination;
    }

    private static void combinationForResource(List<HexType> requiredResources,
                                               Map<HexType, String> tradeResourceCombination,
                                               HexType resourceToSell,
                                               int resourceLeftLimit,
                                               Integer resourceToSellRatio,
                                               int resourceToSellCurrentCount) {
        if (resourceToSellCurrentCount >= (resourceToSellRatio + resourceLeftLimit)) {
            int numberOrResourcesCanBuyForCurrentResource = resourceToSellCurrentCount / (resourceToSellRatio + resourceLeftLimit);
            int numberOfResourcesToBuyForCurrentResource = numberOrResourcesCanBuyForCurrentResource <= requiredResources.size()
                    ? numberOrResourcesCanBuyForCurrentResource
                    : requiredResources.size();
            int numberOfResourcesToSell = resourceToSellRatio * numberOfResourcesToBuyForCurrentResource;

            tradeResourceCombination.put(resourceToSell, String.valueOf(-numberOfResourcesToSell));
            for(int i = 0; i < numberOfResourcesToBuyForCurrentResource; i++){
                if(tradeResourceCombination.get(requiredResources.get(i)) != null){
                    Integer currentBuyValue = Integer.valueOf(tradeResourceCombination.get(requiredResources.get(i)));
                    tradeResourceCombination.put(requiredResources.get(i), String.valueOf(currentBuyValue + 1));
                } else {
                    tradeResourceCombination.put(requiredResources.get(i), String.valueOf(1));
                }
            }

            requiredResources.removeAll(tradeResourceCombination.keySet());
        }
    }

    private static List<HexType> requiredResourcesForSettlement(GameUserBean player) {
        List<HexType> requiredResources = new ArrayList<HexType>();
        if(player.getResources().getBrick() == 0){
            requiredResources.add(HexType.BRICK);
        }
        if(player.getResources().getWood() == 0){
            requiredResources.add(HexType.WOOD);
        }
        if(player.getResources().getSheep() == 0){
            requiredResources.add(HexType.SHEEP);
        }
        if(player.getResources().getWheat() == 0){
            requiredResources.add(HexType.WHEAT);
        }

        return requiredResources;
    }

    private static List<HexType> requiredResourcesForCity(GameUserBean player) {
        List<HexType> requiredResources = new ArrayList<HexType>();
        int wheatRequiredForCity = 2;
        int stoneRequiredForCity = 3;

        if(player.getResources().getWheat() < wheatRequiredForCity){
            for(int i = 0; i < (wheatRequiredForCity - player.getResources().getWheat()); i++){
                requiredResources.add(HexType.WHEAT);
            }
        }

        if(player.getResources().getStone() < stoneRequiredForCity){
            for(int i = 0; i < (stoneRequiredForCity - player.getResources().getStone()); i++) {
                requiredResources.add(HexType.STONE);
            }
        }

        return requiredResources;
    }

    private static List<HexType> requiredResourcesForRoad(GameUserBean player) {
        List<HexType> requiredResources = new ArrayList<HexType>();
        if(player.getResources().getBrick() == 0){
            requiredResources.add(HexType.BRICK);
        }
        if(player.getResources().getWood() == 0){
            requiredResources.add(HexType.WOOD);
        }

        return requiredResources;
    }

    private static boolean needOneResourceForSettlement(GameUserBean player)  {
        if (player.getBuildingsCount().getSettlements() >= 5){
           return false;
        }

        int resourcesNeeded = 0;
        boolean hasPlaceForNewSettlement = false;

        for (NodeBean node : player.getGame().getNodes()) {
            if(node.couldBeUsedForBuildingSettlementByGameUserInMainStage(player)){
                return true;
            }
        }

        return false;
    }

    private static boolean needOneTypeResourceForCity(GameUserBean player) {
        if (player.getBuildingsCount().getSettlements() >= 5){
            return true;
        }

        if((player.getBuildingsCount().getCities() < 3 && player.getBuildingsCount().getSettlements() < 4)
                || requiredResourcesForCity(player).size() > 1){
            return false;
        }

        for (NodeBean node : player.getGame().getNodes()) {
            if(node.hasBuildingBelongsToUser(player) && node.getBuilding().getBuilt() == NodeBuiltType.SETTLEMENT){
                return true;
            }
        }

        return false;
    }

    private static boolean needOneResourceForCard(GameUserBean player) {
        return false;
    }

    private static boolean needOneResourceForRoad(GameUserBean player) {
        return false;
    }
}
