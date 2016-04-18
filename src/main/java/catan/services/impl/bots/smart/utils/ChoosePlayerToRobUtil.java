package catan.services.impl.bots.smart.utils;

import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.transfer.output.game.actions.ActionDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChoosePlayerToRobUtil extends SmartBotUtil {
    public static String getGameUserIdOfPlayerToRob(GameUserBean player, ActionDetails choosePlayerToRobAction) {
        List<Integer> availableNodeIds = choosePlayerToRobAction.getParams().getNodeIds();
        Set<GameUserBean> playersToRob = new HashSet<GameUserBean>();
        for (NodeBean node : player.getGame().getNodes()) {
            if(node.getBuilding() != null && availableNodeIds.contains(node.getAbsoluteId())){
                playersToRob.add(node.getBuilding().getBuildingOwner());
            }
        }

        GameUserBean playerToRob = chooseOpponentPlayerWithBestCurrentResult(player, playersToRob);

        return playerToRob.getGameUserId().toString();
    }
}
