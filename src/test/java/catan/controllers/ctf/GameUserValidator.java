package catan.controllers.ctf;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertTrue;

public class GameUserValidator {

    private Scenario scenario;
    private int moveOrder;

    public GameUserValidator(Scenario scenario, int moveOrder) {
        this.scenario = scenario;
        this.moveOrder = moveOrder;
    }

    public Scenario check(String gameUserAttribute, Matcher matcher) {
        scenario.currentGameDetails.body("gameUsers.find { it.moveOrder == " + moveOrder  + "}." + gameUserAttribute, matcher);
        return scenario;
    }

    // TODO: ability to use any type, not only int   ( <T> ? )
    public int getValueOf(String gameUserAttribute) {
        return scenario.currentGameDetails.extract().path("gameUsers.find { it.moveOrder == " + moveOrder + "}." + gameUserAttribute);
    }

    // Can be used later
    /*public Scenario isAvailableActionsEqualTo(String[] actions, boolean isMandatory) {
        check("availableActions.isMandatory", equalTo(isMandatory));
        check("availableActions.list", hasSize(actions.length));
        for (String action : actions) {
            check("availableActions.list.find {it.code == '" + action + "'}", notNullValue());
        }
        return scenario;
    }*/

    public Scenario hasAvailableAction(String action) {
        check("availableActions.list.find {it.code == '" + action + "'}", notNullValue());
        return scenario;
    }

    public Scenario doesntHaveAvailableAction(String action) {
        check("availableActions.list.find {it.code == '" + action + "'}", nullValue());
        return scenario;
    }

    public Scenario resourcesChanged(int brick, int wood, int sheep, int wheat, int stone) {
        assertTrue("To check that resources quantity was changed, you should call 'startTrackResourcesQuantity()' method " +
                "when you want to start tracking of resource quantity and  call 'startTrackResourcesQuantity()' method" +
                "when you want to stop.", scenario.trackResources);
        check("resources", notNullValue());
        check("resources.brick", equalTo(scenario.usersResources.get("p" + moveOrder + "Brick") + brick));
        check("resources.wood", equalTo(scenario.usersResources.get("p" + moveOrder + "Wood") + wood));
        check("resources.sheep", equalTo(scenario.usersResources.get("p" + moveOrder + "Sheep") + sheep));
        check("resources.wheat", equalTo(scenario.usersResources.get("p" + moveOrder + "Wheat") + wheat));
        check("resources.stone", equalTo(scenario.usersResources.get("p" + moveOrder + "Stone") + stone));

        return scenario;
    }

}
