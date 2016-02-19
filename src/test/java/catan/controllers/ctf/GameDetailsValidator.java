package catan.controllers.ctf;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class GameDetailsValidator {

    private Scenario scenario;

    public GameDetailsValidator(Scenario scenario) {
        this.scenario = scenario;
    }

    public Scenario hasBiggestArmyOwner(int moveOrder) {
        scenario.currentGameDetails.body("biggestArmyOwnerId", is(scenario.gameUserIdsByMoveOrder.get(moveOrder)));
        return scenario;
    }

    public Scenario doesNotHaveBiggestArmyOwner() {
        scenario.currentGameDetails.body("biggestArmyOwnerId", nullValue());
        return scenario;
    }

    public Scenario hasLongestWayOwner(int moveOrder) {
        scenario.currentGameDetails.body("longestWayOwnerId", is(scenario.gameUserIdsByMoveOrder.get(moveOrder)));
        return scenario;
    }

    public Scenario doesNotHaveLongestWayOwner() {
        scenario.currentGameDetails.body("longestWayOwnerId", nullValue());
        return scenario;
    }
}
