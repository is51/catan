package catan.services.impl.bots.smart.utils;

import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameUserBean;
import catan.services.impl.bots.smart.SmartBot;

import java.util.List;

public class MoveRobberUtil extends SmartBotUtil{
    public static int getHexIdOfHexWithBestProfitToMoveRobber(GameUserBean player, List<Integer> hexIdsToMoveRob) {
        int hexIdWithMaxProbability = 0;
        double maxProbability = 0;

        GameUserBean playerToRob = chooseOpponentPlayerWithBestCurrentResult(player, player.getGame().getGameUsers());

        for (HexBean hex : player.getGame().getHexes()) {
            double hexDiceResourceSumProbability = 0;

            if (hex.getDice() == null || hex.getDice() == 7) {
                continue;
            }

            boolean ignoreCurrentHex = true;
            for (NodeBean nodeAtHex : hex.getNodes().listAllNotNullItems()) {
                if (nodeAtHex.hasBuildingBelongsToUser(playerToRob)) {
                    ignoreCurrentHex = false;
                    int resourceQuantityMultiplier = nodeAtHex.getBuilding().getBuilt().equals(NodeBuiltType.SETTLEMENT) ? 1 : 2;
                    hexDiceResourceSumProbability += SmartBot.hexProbabilities.get(hex.getDice()) * resourceQuantityMultiplier;
                }
            }
            //TODO: ignore hex, if player has building on this hex
            if (ignoreCurrentHex) {
                continue;
            }

            for (Integer hexId : hexIdsToMoveRob) {
                if (hexId.equals(hex.getAbsoluteId()) && maxProbability <= hexDiceResourceSumProbability) {
                    hexIdWithMaxProbability = hexId;
                    maxProbability = hexDiceResourceSumProbability;
                }
            }
        }

        return hexIdWithMaxProbability;
    }
}
