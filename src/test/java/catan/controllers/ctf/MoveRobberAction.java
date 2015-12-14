package catan.controllers.ctf;

import catan.controllers.util.PlayTestUtil;

public class MoveRobberAction {
    private String userToken;
    private Scenario scenario;

    public MoveRobberAction(String userToken, Scenario scenario) {
        this.userToken = userToken;
        this.scenario = scenario;
    }

    public Scenario toCoordinates(int x, int y) {
        int hexId = scenario.currentGameDetails.extract().path(
                "map.hexes" +
                        ".findAll { it.x == " + x + " }" +
                        ".find { it.y == " + y + " }" +
                        ".hexId");
        scenario.lastApiResponse = PlayTestUtil.moveRobber(userToken, scenario.gameId, hexId);
        return scenario;
    }
}
