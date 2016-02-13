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

    @Column(name = "TRADE_ID")
    private Integer offerId;

    public TradeProposal() {
    }

    public TradeProposal(int brick, int wood, int sheep, int wheat, int stone, Integer offerId) {
        this.brick = brick;
        this.wood = wood;
        this.sheep = sheep;
        this.wheat = wheat;
        this.stone = stone;
        this.offerId = offerId;
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

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
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
