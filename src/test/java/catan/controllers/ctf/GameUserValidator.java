package catan.controllers.ctf;

import org.hamcrest.Matcher;

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

}
