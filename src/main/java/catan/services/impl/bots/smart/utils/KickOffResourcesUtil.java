package catan.services.impl.bots.smart.utils;

import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KickOffResourcesUtil extends SmartBotUtil {
    public static Map<HexType, String> getResourcesToKickOff(GameUserBean player) {
        Integer requiredKickOffSum = player.getResources().calculateSum()/2;


        //TODO: take resources in correct order, not just starting from brick
        Map<HexType, String> resourcesKickOffCount;

        if(hasResourcesForRoad(player)){
            resourcesKickOffCount = combinationIfHasResourcesForRoad(player, requiredKickOffSum);
        } else if(hasResourcesForSettlement(player)) {
            resourcesKickOffCount = combinationIfHasResourcesForSettlement(player, requiredKickOffSum);
        } else if(hasResourcesForCity(player)){
            resourcesKickOffCount = combinationIfHasResourcesForCity(player, requiredKickOffSum);
        } else if(hasResourcesForCard(player)){
            resourcesKickOffCount = combinationIfHasResourcesForCard(player, requiredKickOffSum);
        } else if(endOfGame(player)){
            resourcesKickOffCount = defaultEndGameCombination(player, requiredKickOffSum);
        } else {
            resourcesKickOffCount = defaultBeginGameCombination(requiredKickOffSum, player.getResources());
        }


        return resourcesKickOffCount;
    }

    private static boolean hasResourcesForRoad(GameUserBean player) {
        return false;
    }

    private static boolean hasResourcesForSettlement(GameUserBean player) {
        return false;
    }

    private static boolean hasResourcesForCity(GameUserBean player) {
        return false;
    }

    private static boolean hasResourcesForCard(GameUserBean player) {
        return false;
    }

    private static boolean endOfGame(GameUserBean player) {
        return false;
    }

    private static Map<HexType, String> combinationIfHasResourcesForRoad(GameUserBean player, Integer halfSum) {
        return null;
    }

    private static Map<HexType, String> combinationIfHasResourcesForSettlement(GameUserBean player, Integer halfSum) {
        return null;
    }

    private static Map<HexType, String> combinationIfHasResourcesForCity(GameUserBean player, Integer halfSum) {
        return null;
    }

    private static Map<HexType, String> combinationIfHasResourcesForCard(GameUserBean player, Integer halfSum) {
        return null;
    }

    private static Map<HexType, String> defaultEndGameCombination(GameUserBean player, Integer halfSum) {
        return null;
    }


    private static Map<HexType, String> defaultBeginGameCombination(Integer halfSum, Resources resources) {
        List<HexType> priorityOrderDesc = new LinkedList<HexType>();
        priorityOrderDesc.add(HexType.BRICK);   // 0
        priorityOrderDesc.add(HexType.WOOD);    // 1
        priorityOrderDesc.add(HexType.SHEEP);   // 2
        priorityOrderDesc.add(HexType.WHEAT);   // 3
        priorityOrderDesc.add(HexType.STONE);   // 4

        Integer fourth = resources.quantityOf(priorityOrderDesc.get(4)) >= halfSum
                ? halfSum
                : resources.quantityOf(priorityOrderDesc.get(4));
        Integer third = resources.quantityOf(priorityOrderDesc.get(3)) + fourth >= halfSum
                ? halfSum - fourth
                : resources.quantityOf(priorityOrderDesc.get(3));
        Integer second = resources.quantityOf(priorityOrderDesc.get(2)) + fourth + third >= halfSum
                ? halfSum - fourth - third
                : resources.quantityOf(priorityOrderDesc.get(2));
        Integer first = resources.quantityOf(priorityOrderDesc.get(1)) + fourth + third + second >= halfSum
                ? halfSum - fourth - third - second
                : resources.quantityOf(priorityOrderDesc.get(1));
        Integer zero = resources.quantityOf(priorityOrderDesc.get(0)) + fourth + third + second + first >= halfSum
                ? halfSum - fourth - third - second - first
                : resources.quantityOf(priorityOrderDesc.get(1));


        Map<HexType, String> resourcesKickOffCount = new HashMap<HexType, String>();
        resourcesKickOffCount.put(priorityOrderDesc.get(4), fourth.toString());
        resourcesKickOffCount.put(priorityOrderDesc.get(3), third.toString());
        resourcesKickOffCount.put(priorityOrderDesc.get(2), second.toString());
        resourcesKickOffCount.put(priorityOrderDesc.get(1), first.toString());
        resourcesKickOffCount.put(priorityOrderDesc.get(0), zero.toString());

        return resourcesKickOffCount;
    }
}
