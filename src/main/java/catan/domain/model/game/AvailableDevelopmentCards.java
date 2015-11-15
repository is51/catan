package catan.domain.model.game;

import catan.domain.model.game.types.DevelopmentCard;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Embeddable
public class AvailableDevelopmentCards {

    @Column(name = "AVAILABLE_KNIGHT", nullable = false)
    private int knight;

    @Column(name = "AVAILABLE_VICTORY_POINT", nullable = false)
    private int victoryPoint;

    @Column(name = "AVAILABLE_ROAD_BUILDING", nullable = false)
    private int roadBuilding;

    @Column(name = "AVAILABLE_MONOPOLY", nullable = false)
    private int monopoly;

    @Column(name = "AVAILABLE_YEAR_OF_PLENTY", nullable = false)
    private int yearOfPlenty;

    public AvailableDevelopmentCards() {
    }

    public AvailableDevelopmentCards(int knight, int victoryPoint, int roadBuilding, int monopoly, int yearOfPlenty) {
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

    public Integer takeAvailableDevCardQuantity(DevelopmentCard developmentCard) {
        switch (developmentCard) {
            case KNIGHT:
                return getKnight();
            case VICTORY_POINT:
                return getVictoryPoint();
            case ROAD_BUILDING:
                return getRoadBuilding();
            case MONOPOLY:
                return getMonopoly();
            case YEAR_OF_PLENTY:
                return getYearOfPlenty();
            default:
                return null;
        }
    }

    public void decreaseAvailableDevCardQuantityByOne(DevelopmentCard developmentCard) {
        int newQuantityOfDevelopmentCard = takeAvailableDevCardQuantity(developmentCard) - 1;
        switch (developmentCard) {
            case KNIGHT:
                setKnight(newQuantityOfDevelopmentCard);
                break;
            case VICTORY_POINT:
                setVictoryPoint(newQuantityOfDevelopmentCard);
                break;
            case ROAD_BUILDING:
                setRoadBuilding(newQuantityOfDevelopmentCard);
                break;
            case MONOPOLY:
                setMonopoly(newQuantityOfDevelopmentCard);
                break;
            case YEAR_OF_PLENTY:
                setYearOfPlenty(newQuantityOfDevelopmentCard);
                break;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
