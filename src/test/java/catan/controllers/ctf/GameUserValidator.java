package catan.controllers.ctf;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

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

}
