package catan.controllers.ctf;

public abstract class TradeAction {
    protected String userToken;
    protected Scenario scenario;

    public TradeAction(String userToken, Scenario scenario) {
        this.userToken = userToken;
        this.scenario = scenario;
    }

    public abstract Scenario withResources(int brick, int wood, int sheep, int wheat, int stone);
}
