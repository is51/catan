package catan.services.impl.bots.smart.utils;

import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameUserBean;
import catan.domain.transfer.output.game.actions.ActionDetails;
import catan.domain.transfer.output.game.actions.ActionParamsDetails;
import catan.services.impl.bots.smart.SmartBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradePortUtil extends SmartBotUtil {
    private static final Map<HexType, String> emptyResourceCombination = new HashMap<HexType, String>();

    public static Map<HexType, String> calculateTradePortResourceCombination(GameUserBean player, ActionDetails action, boolean cardsAreOver) {
        if (noResourcesToSell(player, action.getParams())) {
            return emptyResourceCombination;
        }

        //TODO: reimplement to have list of required resources with each action and list of available resources after trade
        // if have enough resources to buuld settlement, then can trade for other resources

        if(needResourcesForSettlement(player)){
            return combinationIfNeedResourcesForSettlement(player, action.getParams());
        } else if(needResourcesForCity(player)){
            return combinationIfNeedResourcesForCity(player, action.getParams());
        }  else if(needResourcesForRoad(player, cardsAreOver)){
            return combinationIfNeedResourcesForRoad(player, action.getParams());
        } else {
            return combinationIfNeedResourcesForCard(player, action.getParams());
        }
    }

    private static boolean noResourcesToSell(GameUserBean player, ActionParamsDetails params) {
        return player.getResources().getBrick() < params.getBrick()
            && player.getResources().getWood() < params.getWood()
            && player.getResources().getSheep() < params.getSheep()
            && player.getResources().getWheat() < params.getWheat()
            && player.getResources().getStone() < params.getStone();
    }

    private static Map<HexType, String> combinationIfNeedResourcesForSettlement(GameUserBean player, ActionParamsDetails params) {
        List<HexType> requiredResources = requiredResourcesForSettlement(player);
        if(requiredResources == null || requiredResources.isEmpty()){
            return emptyResourceCombination;
        }
        Map<HexType, String> tradeResourceCombination = new HashMap<HexType, String>();

        combinationForResource(requiredResources, tradeResourceCombination, HexType.STONE, 0, params.getStone(), player.getResources().getStone());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.BRICK, 1, params.getBrick(), player.getResources().getBrick());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.WOOD, 1, params.getWood(), player.getResources().getWood());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.SHEEP, 1, params.getSheep(), player.getResources().getSheep());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.WHEAT, 1, params.getWheat(), player.getResources().getWheat());

        return tradeResourceCombination;
    }


    private static Map<HexType, String> combinationIfNeedResourcesForCity(GameUserBean player, ActionParamsDetails params) {
        List<HexType> requiredResources = requiredResourcesForCity(player);
        if(requiredResources == null || requiredResources.isEmpty()){
            return emptyResourceCombination;
        }
        Map<HexType, String> tradeResourceCombination = new HashMap<HexType, String>();

        combinationForResource(requiredResources, tradeResourceCombination, HexType.BRICK, 0, params.getBrick(), player.getResources().getBrick());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.WOOD, 0, params.getWood(), player.getResources().getWood());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.SHEEP, 0, params.getSheep(), player.getResources().getSheep());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.WHEAT, 2, params.getWheat(), player.getResources().getWheat());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.STONE, 3, params.getStone(), player.getResources().getStone());

        return tradeResourceCombination;
    }

    private static Map<HexType, String> combinationIfNeedResourcesForRoad(GameUserBean player, ActionParamsDetails params) {
        List<HexType> requiredResources = requiredResourcesForRoad(player);
        if(requiredResources == null || requiredResources.isEmpty()){
            return emptyResourceCombination;
        }
        Map<HexType, String> tradeResourceCombination = new HashMap<HexType, String>();

        combinationForResource(requiredResources, tradeResourceCombination, HexType.SHEEP, 0, params.getSheep(), player.getResources().getSheep());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.WHEAT, 0, params.getWheat(), player.getResources().getWheat());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.STONE, 0, params.getStone(), player.getResources().getStone());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.BRICK, 1, params.getBrick(), player.getResources().getBrick());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.WOOD, 1, params.getWood(), player.getResources().getWood());

        return tradeResourceCombination;
    }

    private static Map<HexType, String> combinationIfNeedResourcesForCard(GameUserBean player, ActionParamsDetails params) {
        List<HexType> requiredResources = requiredResourcesForRoad(player);
        if(requiredResources == null || requiredResources.isEmpty()){
            return emptyResourceCombination;
        }
        Map<HexType, String> tradeResourceCombination = new HashMap<HexType, String>();

        combinationForResource(requiredResources, tradeResourceCombination, HexType.SHEEP, 1, params.getSheep(), player.getResources().getSheep());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.WHEAT, 1, params.getWheat(), player.getResources().getWheat());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.STONE, 1, params.getStone(), player.getResources().getStone());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.BRICK, 0, params.getBrick(), player.getResources().getBrick());
        combinationForResource(requiredResources, tradeResourceCombination, HexType.WOOD, 0, params.getWood(), player.getResources().getWood());

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

    private static boolean needResourcesForSettlement(GameUserBean player) {
        if(player.getBuildingsCount().getSettlements() >= 5){
            return false;
        }

        for (NodeBean node : player.getGame().getNodes()) {
            if(node.couldBeUsedForBuildingSettlementByGameUserInMainStage(player)){
                return true;
            }
        }

        return false;
    }

    private static boolean needResourcesForCity(GameUserBean player) {
        if((player.getBuildingsCount().getCities() < 3 && player.getBuildingsCount().getSettlements() < 4)
            || requiredResourcesForCity(player).size() > 2){
            return false;
        }

        for (NodeBean node : player.getGame().getNodes()) {
            if(node.hasBuildingBelongsToUser(player) && node.getBuilding().getBuilt() == NodeBuiltType.SETTLEMENT){
                return true;
            }
        }

        return false;
    }

    private static boolean needResourcesForRoad(GameUserBean player, boolean cardsAreOver) {
        if(cardsAreOver) {
            // we can only build roads if there are no cards and we don't have resources to build settlements
            return true;
        }

        if (SmartBot.needIncreaseLongestRoad(player)) {
            // no need to build road if we are the longest way owner
            return true;
        }

        return false;

    }
}
