package catan.domain.model.game;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TradeProposition {

    @Column(name = "TRADE_BRICK", nullable = false)
    private int brick;

    @Column(name = "TRADE_WOOD", nullable = false)
    private int wood;

    @Column(name = "TRADE_SHEEP", nullable = false)
    private int sheep;

    @Column(name = "TRADE_WHEAT", nullable = false)
    private int wheat;

    @Column(name = "TRADE_STONE", nullable = false)
    private int stone;

    @Column(name = "TRADE_ACCEPTED", nullable = false)
    private Boolean acceptedTrade;

    public TradeProposition() {
    }

    public TradeProposition(int brick, int wood, int sheep, int wheat, int stone) {
        this.brick = brick;
        this.wood = wood;
        this.sheep = sheep;
        this.wheat = wheat;
        this.stone = stone;
        this.acceptedTrade = false;
    }

    public int getBrick() {
        return brick;
    }

    public void setBrick(int brick) {
        this.brick = brick;
    }

    public int getWood() {
        return wood;
    }

    public void setWood(int wood) {
        this.wood = wood;
    }

    public int getSheep() {
        return sheep;
    }

    public void setSheep(int sheep) {
        this.sheep = sheep;
    }

    public int getWheat() {
        return wheat;
    }

    public void setWheat(int wheat) {
        this.wheat = wheat;
    }

    public int getStone() {
        return stone;
    }

    public void setStone(int stone) {
        this.stone = stone;
    }

    public Boolean isAcceptedTrade() {
        return acceptedTrade;
    }

    public void setAcceptedTrade(Boolean acceptedTrade) {
        this.acceptedTrade = acceptedTrade;
    }

    @Override
    public String toString() {
        return "Resources{" +
                "brick=" + brick +
                ", wood=" + wood +
                ", sheep=" + sheep +
                ", wheat=" + wheat +
                ", stone=" + stone +
                '}';
    }
}
