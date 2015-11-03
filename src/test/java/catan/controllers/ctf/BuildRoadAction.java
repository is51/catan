package catan.controllers.ctf;

import catan.controllers.util.PlayTestUtil;

public class BuildRoadAction {
    private String userToken;
    private Scenario scenario;

    public BuildRoadAction(String userToken, Scenario scenario) {
        this.userToken = userToken;
        this.scenario = scenario;
    }

    public Scenario atEdge(int x, int y, String edgePosition) {
        int edgeIdToBuild = scenario.currentGameDetails.extract().path(
                "map.hexes" +
                        ".findAll { it.x == " + x + " }" +
                        ".find { it.y == " + y + " }" +
                        ".edgesIds." + edgePosition + "Id");
        scenario.lastApiResponse = PlayTestUtil.buildRoad(userToken, scenario.gameId, edgeIdToBuild);
        return scenario;
    }
}
