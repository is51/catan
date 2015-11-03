package catan.controllers;

import catan.controllers.game.GameTestUtil;
import com.jayway.restassured.response.ValidatableResponse;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class MapValidator {

    private final ValidatableResponse currentGameDetails;
    private final String path;
    private final Scenario scenario;

    public MapValidator(Scenario scenario, ValidatableResponse currentGameDetails, String path) {
        this.scenario = scenario;
        this.currentGameDetails = currentGameDetails;
        this.path = path;
    }

    public Scenario buildingIsEmpty() {
        currentGameDetails.body(path + ".building", nullValue());
        return scenario;
    }

    public Scenario hasBuiltSettlement() {
        currentGameDetails.body(path + ".building.built", equalTo("SETTLEMENT"));
        return scenario;
    }

    public Scenario buildingBelongsToPlayer(int moveOrder) {
        int gameUserId = scenario.userIdsByMoveOrder.get(moveOrder);

        currentGameDetails.body(path + ".building.ownerGameUserId", is(gameUserId));
        return scenario;
    }

}
