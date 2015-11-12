package catan.domain.transfer.output.game;

import catan.domain.model.game.Achievements;

public class AchievementsDetails {
    private Integer displayVictoryPoints;
    private Integer realVictoryPoints;
    private Integer totalResources;
    private Integer totalCards;
    private Integer totalUsedKnights;
    private Integer longestWayLength;

    public AchievementsDetails() {
    }

    public AchievementsDetails(Achievements achievements) {
        this.displayVictoryPoints = achievements.getDisplayVictoryPoints();
        this.totalResources = achievements.getTotalResources();
        this.totalCards = achievements.getTotalCards();
        this.totalUsedKnights = achievements.getTotalUsedKnights();
        this.longestWayLength = achievements.getLongestWayLength();
    }

    public AchievementsDetails(Achievements achievements, int extraVictoryPoints) {
        this(achievements);
        this.realVictoryPoints = achievements.getDisplayVictoryPoints() + extraVictoryPoints;
    }

    public Integer getRealVictoryPoints() {
        return realVictoryPoints;
    }

    public void setRealVictoryPoints(Integer realVictoryPoints) {
        this.realVictoryPoints = realVictoryPoints;
    }

    public Integer getDisplayVictoryPoints() {
        return displayVictoryPoints;
    }

    public void setDisplayVictoryPoints(Integer displayVictoryPoints) {
        this.displayVictoryPoints = displayVictoryPoints;
    }

    public Integer getTotalResources() {
        return totalResources;
    }

    public void setTotalResources(Integer totalResources) {
        this.totalResources = totalResources;
    }

    public Integer getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(Integer totalCards) {
        this.totalCards = totalCards;
    }

    public Integer getTotalUsedKnights() {
        return totalUsedKnights;
    }

    public void setTotalUsedKnights(Integer totalUsedKnights) {
        this.totalUsedKnights = totalUsedKnights;
    }

    public Integer getLongestWayLength() {
        return longestWayLength;
    }

    public void setLongestWayLength(Integer longestWayLength) {
        this.longestWayLength = longestWayLength;
    }
}
