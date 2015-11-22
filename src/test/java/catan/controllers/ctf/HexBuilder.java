package catan.controllers.ctf;

import catan.domain.model.dashboard.Coordinates;
import catan.domain.model.dashboard.types.HexType;

public class HexBuilder {

    private final Scenario scenario;
    private final HexType hexType;
    private final Integer diceValue;

    public HexBuilder(Scenario scenario, HexType hexType, Integer diceValue) {
        this.scenario = scenario;
        this.hexType = hexType;
        this.diceValue = diceValue;
    }

    public Scenario atCoordinates(int x, int y){
        scenario.randomUtil.setNextHexType(new Coordinates(x, y), hexType);
        scenario.randomUtil.setNextHexDiceNumber(new Coordinates(x, y), diceValue);

        return scenario;
    }

}
