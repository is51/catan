package catan.domain.transfer.output.game;

import catan.domain.model.game.ResourcesBean;

public class ResourcesDetails {
    private int brick;
    private int wood;
    private int sheep;
    private int wheat;
    private int stone;

    public ResourcesDetails() {
    }

    public ResourcesDetails(ResourcesBean resources) {
        this.brick = resources.getBrick();
        this.wood = resources.getWood();
        this.sheep = resources.getSheep();
        this.wheat = resources.getWheat();
        this.stone = resources.getStone();
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
