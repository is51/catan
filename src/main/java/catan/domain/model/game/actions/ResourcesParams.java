package catan.domain.model.game.actions;

import catan.domain.model.dashboard.types.HexType;

public class ResourcesParams extends ActionParams {

    private int brick;
    private int wood;
    private int sheep;
    private int wheat;
    private int stone;

    public ResourcesParams() {
    }

    public ResourcesParams(int brick, int wood, int sheep, int wheat, int stone) {
        this.brick = brick;
        this.wood = wood;
        this.sheep = sheep;
        this.wheat = wheat;
        this.stone = stone;
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

    public Integer paramOf(HexType resourceType) {
        switch (resourceType) {
            case BRICK:
                return this.brick;
            case WOOD:
                return this.wood;
            case SHEEP:
                return this.sheep;
            case STONE:
                return this.stone;
            case WHEAT:
                return this.wheat;
            default:
                return null;
        }
    }
}
