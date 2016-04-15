package catan.services.impl.bots.smart.utils;

import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameUserBean;

import java.util.List;

public class BuildCityUtil extends SmartBotUtil {

    public static String geNodeIdOfBestSettlementToBuildCity(GameUserBean player, List<Integer> nodeIdsToBuildCity) {
        NodeBean nodeToBuildCity = SmartBotUtil.getNodeWithNeighbourHexMaxProbability(player, nodeIdsToBuildCity);

        return nodeToBuildCity != null
                ? nodeToBuildCity.getAbsoluteId().toString()
                : nodeIdsToBuildCity.get((int) (Math.random() * nodeIdsToBuildCity.size())).toString();
    }
}
