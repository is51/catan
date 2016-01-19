package catan.controllers.ctf;

import catan.controllers.util.PlayTestUtil;

public class TradeAction {
    private String userToken;
    private Scenario scenario;

    public TradeAction(String userToken, Scenario scenario) {
        this.userToken = userToken;
        this.scenario = scenario;
    }

    public Scenario withResources(int brick, int wood, int sheep, int wheat, int stone) {
        scenario.lastApiResponse = PlayTestUtil.tradePort(userToken, scenario.gameId, brick, wood, sheep, wheat, stone);

        return scenario;
    }
}
