package catan.domain.model.game;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Embeddable
public class DevelopmentCardsBean {

    @Column(name = "DEV_CARD_KNIGHT", nullable = false)
    private int knight;

    @Column(name = "DEV_CARD_VICTORY_POINT", nullable = false)
    private int victoryPoint;

    @Column(name = "DEV_CARD_ROAD_BUILDING", nullable = false)
    private int roadBuilding;

    @Column(name = "DEV_CARD_MONOPOLY", nullable = false)
    private int monopoly;

    @Column(name = "DEV_CARD_YEAR_OF_PLENTY", nullable = false)
    private int yearOfPlenty;

    public DevelopmentCardsBean() {
    }

    public DevelopmentCardsBean(int knight, int victoryPoint, int roadBuilding, int monopoly, int yearOfPlenty) {
        this.knight = knight;
        this.victoryPoint = victoryPoint;
        this.roadBuilding = roadBuilding;
        this.monopoly = monopoly;
        this.yearOfPlenty = yearOfPlenty;
    }

    public int getKnight() {
        return knight;
    }

    public void setKnight(int knight) {
        this.knight = knight;
    }

    public int getVictoryPoint() {
        return victoryPoint;
    }

    public void setVictoryPoint(int victoryPoint) {
        this.victoryPoint = victoryPoint;
    }

    public int getRoadBuilding() {
        return roadBuilding;
    }

    public void setRoadBuilding(int roadBuilding) {
        this.roadBuilding = roadBuilding;
    }

    public int getMonopoly() {
        return monopoly;
    }

    public void setMonopoly(int monopoly) {
        this.monopoly = monopoly;
    }

    public int getYearOfPlenty() {
        return yearOfPlenty;
    }

    public void setYearOfPlenty(int yearOfPlenty) {
        this.yearOfPlenty = yearOfPlenty;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
