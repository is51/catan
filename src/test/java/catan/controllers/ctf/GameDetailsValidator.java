package catan.controllers.ctf;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class GameDetailsValidator {

    private Scenario scenario;

    public GameDetailsValidator(Scenario scenario) {
        this.scenario = scenario;
    }

    public Scenario hasBiggestArmyOwner(int moveOrder) {
        scenario.currentGameDetails.body("biggestArmyOwner", is(scenario.gameUserIdsByMoveOrder.get(moveOrder)));
        return scenario;
    }

    public Scenario doesNotHaveBiggestArmyOwner() {
        scenario.currentGameDetails.body("biggestArmyOwner", nullValue());
        return scenario;
    }
}
