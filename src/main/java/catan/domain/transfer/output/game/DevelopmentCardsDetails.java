package catan.domain.transfer.output.game;

import catan.domain.model.game.DevelopmentCardsBean;

public class DevelopmentCardsDetails {
    private int knight;
    private int victoryPoint;
    private int roadBuilding;
    private int monopoly;
    private int yearOfPlenty;

    public DevelopmentCardsDetails() {
    }

    public DevelopmentCardsDetails(DevelopmentCardsBean developmentCards) {
        this.knight = developmentCards.getKnight();
        this.victoryPoint = developmentCards.getVictoryPoint();
        this.roadBuilding = developmentCards.getRoadBuilding();
        this.monopoly = developmentCards.getMonopoly();
        this.yearOfPlenty = developmentCards.getYearOfPlenty();
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

}
