package catan.services.impl.bots.smart.utils;

import catan.domain.model.game.GameUserBean;

import java.util.HashMap;
import java.util.Map;

public class KickOffResourcesUtil extends SmartBotUtil {
    public static Map<String, String> getResourcesToKickOff(GameUserBean player) {
        Integer sum = player.getResources().getBrick() +
                player.getResources().getWood() +
                player.getResources().getSheep() +
                player.getResources().getWheat() +
                player.getResources().getStone();
        Integer halfSum = sum / 2;

        //TODO: take resources in correct order, not just starting from brick
        Integer brick = player.getResources().getBrick() >= halfSum
                ? halfSum
                : player.getResources().getBrick();
        Integer wood = player.getResources().getWood() + brick >= halfSum
                ? halfSum - brick
                : player.getResources().getWood();
        Integer sheep = player.getResources().getSheep() + brick + wood >= halfSum
                ? halfSum - brick - wood
                : player.getResources().getSheep();
        Integer wheat = player.getResources().getWheat() + brick + wood + sheep >= halfSum
                ? halfSum - brick - wood - sheep
                : player.getResources().getWheat();
        Integer stone = player.getResources().getStone() + brick + wood + sheep + wheat >= halfSum
                ? halfSum - brick - wood - sheep - wheat
                : player.getResources().getStone();


        Map<String, String> resourcesKickOffCount = new HashMap<String, String>();
        resourcesKickOffCount.put("brick", brick.toString());
        resourcesKickOffCount.put("wood", wood.toString());
        resourcesKickOffCount.put("sheep", sheep.toString());
        resourcesKickOffCount.put("wheat", wheat.toString());
        resourcesKickOffCount.put("stone", stone.toString());

        return resourcesKickOffCount;
    }
}
