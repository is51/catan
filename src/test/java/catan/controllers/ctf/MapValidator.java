package catan.controllers.ctf;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class MapValidator {

    public static final String BUILT_ATTRIBUTE = ".built";
    public static final String OWNER_ATTRIBUTE = ".ownerGameUserId";

    private Scenario scenario;
    private final int x;
    private final int y;
    private String mapElementPosition;
    private String mapElementType;

    public MapValidator(Scenario scenario, int x, int y, String mapElementPosition, String mapElementType) {
        this.scenario = scenario;
        this.x = x;
        this.y = y;
        this.mapElementPosition = mapElementPosition;
        this.mapElementType = mapElementType;
    }

    public Scenario buildingIsEmpty() {
        assertBuilding(null, is(nullValue()));
        return scenario;
    }

    public Scenario hasBuiltSettlement() {
        assertBuilding(null, is(notNullValue()));
        assertBuilding(BUILT_ATTRIBUTE, equalTo("SETTLEMENT"));
        return scenario;
    }

    public Scenario buildingBelongsToPlayer(int moveOrder) {
        int gameUserId = scenario.gameUserIdsByMoveOrder.get(moveOrder);
        assertBuilding(null, is(notNullValue()));
        assertBuilding(BUILT_ATTRIBUTE, equalTo("SETTLEMENT"));
        assertBuilding(OWNER_ATTRIBUTE, is(gameUserId));
        return scenario;
    }

    private void assertBuilding(String buildingAttribute, Matcher matcher) {
        int mapElementId = getMapElementId();
        scenario.currentGameDetails.body(
                "map." + mapElementType + "s" +
                        ".find {it." + mapElementType + "Id == " + mapElementId + "}" +
                        ".building" + (buildingAttribute != null ? buildingAttribute : ""), matcher);
    }

    private int getMapElementId() {
        return scenario.currentGameDetails.extract().path(
                "map.hexes" +
                        ".findAll { it.x == " + x + " }" +
                        ".find { it.y == " + y + " }" +
                        "." + mapElementType + "sIds." + mapElementPosition + "Id");
    }

}
