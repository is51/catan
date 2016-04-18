package catan.services.impl.bots.smart.utils;

import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.game.GameUserBean;
import catan.services.impl.bots.smart.SmartBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MoveRobberUtil extends SmartBotUtil {
    private static Logger log = LoggerFactory.getLogger(MoveRobberUtil.class);

    public static int getHexIdOfHexWithBestProfitToMoveRobber(GameUserBean player, List<Integer> hexIdsToMoveRob) {
        int hexIdWithMaxProbability = -1;
        double maxProbability = -1;

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
                    //TODO: if there are several hexes with the same hexDiceResourceSumProbability, choose that affect also another player
                } else if (nodeAtHex.hasBuildingBelongsToUser(player)) {
                    ignoreCurrentHex = true;
                    break;
                }
            }
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

        if(hexIdWithMaxProbability == -1){
            hexIdWithMaxProbability = hexIdsToMoveRob.get((int) (Math.random() * hexIdsToMoveRob.size()));
            log.debug("Hex without player building was not found, using hex with id: " + hexIdWithMaxProbability);
        }

        return hexIdWithMaxProbability;
    }
}
