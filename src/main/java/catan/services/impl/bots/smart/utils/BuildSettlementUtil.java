package catan.services.impl.bots.smart.utils;

import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameUserBean;

import java.util.List;

public class BuildSettlementUtil extends SmartBotUtil {

    public static String geNodeIdOfBestPlaceToBuildSettlement(GameUserBean player, List<Integer> nodeIdsToBuildSettlement) {
        NodeBean nodeToBuildSettlement = SmartBotUtil.getNodeWithNeighbourHexMaxProbability(player, nodeIdsToBuildSettlement);

        return nodeToBuildSettlement != null
                ? nodeToBuildSettlement.getAbsoluteId().toString()
                : nodeIdsToBuildSettlement.get((int) (Math.random() * nodeIdsToBuildSettlement.size())).toString();
    }
}
