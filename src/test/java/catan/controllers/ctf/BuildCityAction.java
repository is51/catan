package catan.controllers.ctf;

import catan.controllers.util.PlayTestUtil;

public class BuildCityAction {
    private String userToken;
    private Scenario scenario;

    public BuildCityAction(String userToken, Scenario scenario) {
        this.userToken = userToken;
        this.scenario = scenario;
    }

    public Scenario atNode(int x, int y, String nodePosition) {
        int nodeIdToBuild = scenario.currentGameDetails.extract().path(
                "map.hexes" +
                        ".findAll { it.x == " + x + " }" +
                        ".find { it.y == " + y + " }" +
                        ".nodesIds." + nodePosition + "Id");
        scenario.lastApiResponse = PlayTestUtil.buildCity(userToken, scenario.gameId, nodeIdToBuild);
        return scenario;
    }
}
