package catan.controllers.ctf;

import catan.controllers.util.PlayTestUtil;

public class TradePortAction extends TradeAction{

    public TradePortAction(String userToken, Scenario scenario) {
        super(userToken, scenario);
    }

    public Scenario withResources(int brick, int wood, int sheep, int wheat, int stone) {
        scenario.lastApiResponse = PlayTestUtil.tradePort(userToken, scenario.gameId, brick, wood, sheep, wheat, stone);

        return scenario;
    }
}
