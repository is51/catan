package catan.domain.model.game;

import catan.domain.model.game.types.DevelopmentCard;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Embeddable
public class DevelopmentCards {

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

    public DevelopmentCards() {
    }

    public DevelopmentCards(int knight, int victoryPoint, int roadBuilding, int monopoly, int yearOfPlenty) {
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

    public Integer quantityOf(DevelopmentCard developmentCard) {
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

    public void decreaseQuantityByOne(DevelopmentCard developmentCard) {
        int newQuantity = quantityOf(developmentCard) - 1;
        updateQuantity(developmentCard, newQuantity);
    }

    public void increaseQuantityByOne(DevelopmentCard developmentCard) {
        int newQuantity = quantityOf(developmentCard) + 1;
        updateQuantity(developmentCard, newQuantity);
    }

    public void updateQuantity(DevelopmentCard developmentCard, int newQuantity) {
        switch (developmentCard) {
            case KNIGHT:
                setKnight(newQuantity);
                break;
            case VICTORY_POINT:
                setVictoryPoint(newQuantity);
                break;
            case ROAD_BUILDING:
                setRoadBuilding(newQuantity);
                break;
            case MONOPOLY:
                setMonopoly(newQuantity);
                break;
            case YEAR_OF_PLENTY:
                setYearOfPlenty(newQuantity);
                break;
        }
    }

    public int calculateSum() {
        return knight + victoryPoint + roadBuilding + monopoly + yearOfPlenty;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
