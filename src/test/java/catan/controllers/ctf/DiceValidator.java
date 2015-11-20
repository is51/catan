package catan.controllers.ctf;

import sun.tools.java.ClassDefinition;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class DiceValidator {
    private final Scenario scenario;

    public DiceValidator(Scenario scenario) {
        this.scenario = scenario;
    }


    public Scenario isThrown() {
        scenario.currentGameDetails.body("dice.thrown", equalTo(true));

        return scenario;
    }

    public Scenario isNotThrown() {
        scenario.currentGameDetails.body("dice.thrown", equalTo(false));

        return scenario;
    }

    public Scenario hasNoValues() {
        scenario.currentGameDetails.body("dice.value", nullValue());
        scenario.currentGameDetails.body("dice.first", nullValue());
        scenario.currentGameDetails.body("dice.second", nullValue());

        return scenario;
    }

    public Scenario hasValues(int firstValue, int secondValue) {
        scenario.currentGameDetails.body("dice.value", equalTo(firstValue + secondValue));
        scenario.currentGameDetails.body("dice.first", equalTo(firstValue));
        scenario.currentGameDetails.body("dice.second", equalTo(secondValue));

        return scenario;
    }
}
