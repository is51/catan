package catan.controllers.ctf;

import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
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

    public Scenario hasDisplayedMessage(String message) {
        check("displayedMessage", equalTo(message));
        return scenario;
    }

    public Scenario doesntHaveDisplayedMessage() {
        check("displayedMessage", nullValue());
        return scenario;
    }

    public Scenario hasLongestWayLength(int length) {
        check("achievements.longestWayLength", is(length));
        return scenario;
    }

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

    public Scenario hasVictoryPoints(int victoryPoints) {
        check("achievements.displayVictoryPoints", is(victoryPoints));
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

    public LogValidator logWithCode(String actionCode) {
        check("log", notNullValue());
        check("log.find {it.code == '" + actionCode + "'}", notNullValue());

        return new LogValidator(scenario, actionCode);
    }

    public class LogValidator extends Scenario{
        private String actionCode;

        public LogValidator(Scenario scenario, String actionCode) {
            cloneFrom(scenario);
            this.actionCode = actionCode;
        }

        public LogValidator hasMessage(String message) {
            check("log.find {it.code == '" + actionCode + "'}.message", equalTo(message));
            return this;
        }

        public LogValidator isDisplayedOnTop() {
            check("log.find {it.code == '" + actionCode + "'}.displayedOnTop", equalTo(true));
            return this;
        }

        public LogValidator isHidden() {
            check("log.find {it.code == '" + actionCode + "'}.displayedOnTop", equalTo(false));
            return this;
        }
    }

    public class ActionParameterValidator extends Scenario{
        private String action;
        public ActionParameterValidator(Scenario scenario, String action) {
            cloneFrom(scenario);
            this.action = action;
        }

        public ActionParameterValidator withParameters(String... params){
            check("availableActions.list.find {it.code == '" + action + "'}.params", notNullValue());
            for(String param : params){
                String[] keyValue = param.split("=");
                String parameterName = keyValue[0];
                String strParamValue = keyValue[1];

                if(strParamValue.contains("[") && strParamValue.contains("]")){
                    compareArrayParameter(parameterName, strParamValue);
                } else {
                    compareSingleParameter(parameterName, strParamValue);
                }
            }

            return this;
        }

        public ActionParameterValidator withoutParameters(){
            check("availableActions.list.find {it.code == '" + action + "'}.params", nullValue());

            return this;
        }

        public ActionParameterValidator withNotification(String notification){
            check("availableActions.list.find {it.code == '" + action + "'}.notifyMessage", equalTo(notification));
            check("availableActions.list.find {it.code == '" + action + "'}.notify", equalTo(true));

            return this;
        }

        public ActionParameterValidator withoutNotification(){
            check("availableActions.list.find {it.code == '" + action + "'}.notify", equalTo(false));

            return this;
        }

        public ActionParameterValidator and() {
            return this;
        }

        private void compareArrayParameter(String parameterName, String strArrayParamValue) {
            String[] strItems = strArrayParamValue.replaceAll("\\[", "").replaceAll("\\]", "").split(", ");
            List<Integer> intItems = new ArrayList<Integer>();
            for(String strItem : strItems){
                try {
                    intItems.add(Integer.parseInt(strItem));
                } catch (NumberFormatException ignored) {};
            }
            check("availableActions.list.find {it.code == '" + action + "'}.params." + parameterName,
                    intItems.size() > 0 ? hasSize(intItems.size()) : hasItems(strItems.length));
            check("availableActions.list.find {it.code == '" + action + "'}.params." + parameterName,
                    intItems.size() > 0 ? hasItems(intItems.toArray()) : hasItems(strItems));
        }

        private void compareSingleParameter(String parameterName, String strParamValue) {
            Integer intParamValue;
            try{
                intParamValue = Integer.valueOf(strParamValue);
            } catch (Exception e){
                intParamValue = null;
            }

            check("availableActions.list.find {it.code == '" + action + "'}.params." + parameterName,
                    equalTo(intParamValue != null ? intParamValue : strParamValue));
        }
    }
}
