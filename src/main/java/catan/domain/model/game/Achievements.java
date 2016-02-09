package catan.domain.model.game;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Embeddable
public class Achievements {
    @Column(name = "ACHIEVEMENTS_DISPLAY_VICTORY_POINTS", nullable = false)
    private int displayVictoryPoints;

    @Column(name = "ACHIEVEMENTS_TOTAL_RESOURCES", nullable = false)
    private int totalResources;

    @Column(name = "ACHIEVEMENTS_TOTAL_CARDS", nullable = false)
    private int totalCards;

    @Column(name = "ACHIEVEMENTS_TOTAL_USED_KNIGHTS", nullable = false)
    private int totalUsedKnights;

    @Column(name = "ACHIEVEMENTS_LONGEST_WAY_LENGTH", nullable = false)
    private int longestWayLength;

    public Achievements() {
    }

    public Achievements(int displayVictoryPoints, int totalResources, int totalCards, int totalUsedKnights, int longestWayLength) {
        this.displayVictoryPoints = displayVictoryPoints;
        this.totalResources = totalResources;
        this.totalCards = totalCards;
        this.totalUsedKnights = totalUsedKnights;
        this.longestWayLength = longestWayLength;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
