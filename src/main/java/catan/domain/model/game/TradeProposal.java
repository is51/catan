package catan.domain.model.game;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TradeProposal {

    @Column(name = "TRADE_BRICK")
    private int brick;

    @Column(name = "TRADE_WOOD")
    private int wood;

    @Column(name = "TRADE_SHEEP")
    private int sheep;

    @Column(name = "TRADE_WHEAT")
    private int wheat;

    @Column(name = "TRADE_STONE")
    private int stone;

    @Column(name = "TRADE_FINISHED")
    private Boolean finishedTrade;

    public TradeProposal() {
    }

    public TradeProposal(int brick, int wood, int sheep, int wheat, int stone) {
        this.brick = brick;
        this.wood = wood;
        this.sheep = sheep;
        this.wheat = wheat;
        this.stone = stone;
        this.finishedTrade = false;
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

    public Boolean isFinishedTrade() {
        return finishedTrade;
    }

    public void setFinishedTrade(Boolean finishedTrade) {
        this.finishedTrade = finishedTrade;
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
