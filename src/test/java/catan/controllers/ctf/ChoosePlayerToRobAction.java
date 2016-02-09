package catan.controllers.ctf;

import catan.controllers.util.PlayTestUtil;

public class ChoosePlayerToRobAction {
    private String userToken;
    private Scenario scenario;

    public ChoosePlayerToRobAction(String userToken, Scenario scenario) {
        this.userToken = userToken;
        this.scenario = scenario;
    }

    public Scenario stealResourceFromPlayer(int moveOrder) {
        int gameUserIdToRob = scenario.gameUserIdsByMoveOrder.get(moveOrder);
        scenario.lastApiResponse = PlayTestUtil.choosePlayerToRob(userToken, scenario.gameId, gameUserIdToRob);

        return scenario;
    }
}
