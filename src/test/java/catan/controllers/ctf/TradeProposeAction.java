package catan.controllers.ctf;

import catan.controllers.util.PlayTestUtil;

public class TradeProposeAction extends TradeAction{

    public TradeProposeAction(String userToken, Scenario scenario) {
        super(userToken, scenario);
    }

    public Scenario withResources(int brick, int wood, int sheep, int wheat, int stone) {
        scenario.lastApiResponse = PlayTestUtil.tradePropose(userToken, scenario.gameId, brick, wood, sheep, wheat, stone);

        return scenario;
    }
}
