package catan.domain.model.game.actions;

import catan.domain.model.game.TradeProposition;

public class TradingParams extends ActionParams {

    private int brick;
    private int wood;
    private int sheep;
    private int wheat;
    private int stone;

    public TradingParams() {
    }

    public TradingParams(int brick, int wood, int sheep, int wheat, int stone) {
        this.brick = brick;
        this.wood = wood;
        this.sheep = sheep;
        this.wheat = wheat;
        this.stone = stone;
    }

    public TradingParams(TradeProposition tradeProposition) {
        this.brick = tradeProposition.getBrick();
        this.wood = tradeProposition.getWood();
        this.sheep = tradeProposition.getSheep();
        this.wheat = tradeProposition.getWheat();
        this.stone = tradeProposition.getStone();
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
}
