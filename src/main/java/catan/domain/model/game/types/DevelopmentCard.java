package catan.domain.model.game.types;

public enum DevelopmentCard {
    KNIGHT("dev_card_knight"),
    VICTORY_POINT("dev_card_victory_point"),
    ROAD_BUILDING("dev_card_road_building"),
    MONOPOLY("dev_card_monopoly"),
    YEAR_OF_PLENTY("dev_card_year_of_plenty");

    private final String patternName;

    DevelopmentCard(String patternName) {
        this.patternName = patternName;
    }

    public String getPatternName() {
        return this.patternName;
    }
}
