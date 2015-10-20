package catan.domain.transfer.output.game;

import catan.domain.model.game.Achievements;

public class AchievementsDetails {
    private int displayVictoryPoints;
    private int totalResources;
    private int totalCards;
    private int totalUsedKnights;
    private int longestWayLength;

    public AchievementsDetails() {
    }

    public AchievementsDetails(Achievements achievements) {
        this.displayVictoryPoints = achievements.getDisplayVictoryPoints();
        this.totalResources = achievements.getTotalResources();
        this.totalCards = achievements.getTotalCards();
        this.totalUsedKnights = achievements.getTotalUsedKnights();
        this.longestWayLength = achievements.getLongestWayLength();
    }

    public int getDisplayVictoryPoints() {
        return displayVictoryPoints;
    }

    public void setDisplayVictoryPoints(int displayVictoryPoints) {
        this.displayVictoryPoints = displayVictoryPoints;
    }

    public int getTotalResources() {
        return totalResources;
    }

    public void setTotalResources(int totalResources) {
        this.totalResources = totalResources;
    }

    public int getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(int totalCards) {
        this.totalCards = totalCards;
    }

    public int getTotalUsedKnights() {
        return totalUsedKnights;
    }

    public void setTotalUsedKnights(int totalUsedKnights) {
        this.totalUsedKnights = totalUsedKnights;
    }

    public int getLongestWayLength() {
        return longestWayLength;
    }

    public void setLongestWayLength(int longestWayLength) {
        this.longestWayLength = longestWayLength;
    }
}
