package catan.controllers.ctf;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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

    public ActionParameterValidator hasAvailableAction(String action) {
        check("availableActions.list.find {it.code == '" + action + "'}", notNullValue());
        return new ActionParameterValidator(scenario, action);
    }

    public Scenario hasMandatoryAvailableAction(String action) {
        check("availableActions.list.find {it.code == '" + action + "'}", notNullValue());
        check("availableActions.isMandatory", equalTo(true));
        return scenario;
    }

    public Scenario doesntHaveAvailableAction(String action) {
        check("availableActions.list.find {it.code == '" + action + "'}", nullValue());
        return scenario;
    }

    public Scenario hasUsedKnights(int usedKnights) {
        check("achievements.totalUsedKnights", is(usedKnights));
        return scenario;
    }

    public Scenario resourcesQuantityChangedBy(int brickDiff, int woodDiff, int sheepDiff, int wheatDiff, int stoneDiff) {
        assertTrue("To check that resources quantity was changed, you should call 'startTrackResourcesQuantity()' method " +
                "when you want to start tracking of resource quantity and  call 'stopTrackResourcesQuantity()' method" +
                "when you want to stop.", scenario.trackResources);

        check("resources", notNullValue());
        check("resources.brick", equalTo(scenario.usersResources.get("user" + moveOrder + "brick") + brickDiff));
        check("resources.wood",  equalTo(scenario.usersResources.get("user" + moveOrder + "wood") + woodDiff));
        check("resources.sheep", equalTo(scenario.usersResources.get("user" + moveOrder + "sheep") + sheepDiff));
        check("resources.wheat", equalTo(scenario.usersResources.get("user" + moveOrder + "wheat") + wheatDiff));
        check("resources.stone", equalTo(scenario.usersResources.get("user" + moveOrder + "stone") + stoneDiff));

        int sumOfDiff = brickDiff + woodDiff + sheepDiff + wheatDiff + stoneDiff;
        check("achievements.totalResources", equalTo(scenario.usersResources.get("user" + moveOrder + "totalResources") + sumOfDiff));

        return scenario;
    }

    public Scenario devCardsQuantityChangedBy(int knightDiff, int victoryPointDiff, int roadBuildingDiff, int monopolyDiff, int yearOfPlentyDiff) {
        assertTrue("To check that dev cards quantity was changed, you should call 'startTrackDevCardsQuantity()' method " +
                "when you want to start tracking of dev cards quantity and call 'stopTrackDevCardsQuantity()' method" +
                "when you want to stop.", scenario.trackCards);

        check("developmentCards", notNullValue());
        check("developmentCards.knight",        equalTo(scenario.usersCards.get("user" + moveOrder + "knight") + knightDiff));
        check("developmentCards.victoryPoint",  equalTo(scenario.usersCards.get("user" + moveOrder + "victoryPoint") + victoryPointDiff));
        check("developmentCards.roadBuilding",  equalTo(scenario.usersCards.get("user" + moveOrder + "roadBuilding") + roadBuildingDiff));
        check("developmentCards.monopoly",      equalTo(scenario.usersCards.get("user" + moveOrder + "monopoly") + monopolyDiff));
        check("developmentCards.yearOfPlenty",  equalTo(scenario.usersCards.get("user" + moveOrder + "yearOfPlenty") + yearOfPlentyDiff));

        int sumOfDiff = knightDiff + victoryPointDiff + roadBuildingDiff + monopolyDiff + yearOfPlentyDiff;
        check("achievements.totalCards", equalTo(scenario.usersCards.get("user" + moveOrder + "totalCards") + sumOfDiff));

        return scenario;
    }

    public class ActionParameterValidator extends Scenario{
        private String action;
        public ActionParameterValidator(Scenario scenario, String action) {
            cloneFrom(scenario);
            this.action = action;
        }

        public Scenario withParameters(String... params){
            check("availableActions.list.find {it.code == '" + action + "'}.params", notNullValue());
            for(String param : params){
                String[] keyValue = param.split("=");

                String key = keyValue[0];
                String strValue = keyValue[1];
                Integer intValue;
                try{
                    intValue = Integer.valueOf(strValue);
                } catch (Exception e){
                    intValue = null;
                }

                check("availableActions.list.find {it.code == '" + action + "'}.params." + key, equalTo(intValue != null ? intValue : strValue));
            }

            return scenario;
        }

        public Scenario withoutParameters(){
            check("availableActions.list.find {it.code == '" + action + "'}.params", nullValue());

            return scenario;
        }
    }
}
