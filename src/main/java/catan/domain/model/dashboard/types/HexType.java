package catan.domain.model.dashboard.types;

public enum HexType {
    BRICK("resource_server"),
    WOOD("resource_cable"),
    SHEEP("resource_developer"),
    WHEAT("resource_building"),
    STONE("resource_consultant"),
    EMPTY("");

    private final String patternName;

    HexType(String patternName) {
        this.patternName = patternName;
    }

    public String getPatternName() {
        return this.patternName;
    }
}
