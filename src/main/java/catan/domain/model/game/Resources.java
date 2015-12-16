package catan.domain.model.game;

import catan.domain.model.dashboard.types.HexType;
import org.apache.commons.lang.builder.ToStringBuilder;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Embeddable
public class Resources {

    @Column(name = "RESOURCE_BRICK", nullable = false)
    private int brick;

    @Column(name = "RESOURCE_WOOD", nullable = false)
    private int wood;

    @Column(name = "RESOURCE_SHEEP", nullable = false)
    private int sheep;

    @Column(name = "RESOURCE_WHEAT", nullable = false)
    private int wheat;

    @Column(name = "RESOURCE_STONE", nullable = false)
    private int stone;

    public Resources() {
    }

    public Resources(int brick, int wood, int sheep, int wheat, int stone) {
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

    public Integer quantityOf(HexType resource) {
        switch (resource) {
            case BRICK:
                return getBrick();
            case WOOD:
                return getWood();
            case SHEEP:
                return getSheep();
            case STONE:
                return getStone();
            case WHEAT:
                return getWheat();
            default:
                return null;
        }
    }

    public void updateResourceQuantity(HexType resource, int resourceQuantity) {
        switch (resource) {
            case BRICK:
                setBrick(resourceQuantity);
                break;
            case WOOD:
                setWood(resourceQuantity);
                break;
            case SHEEP:
                setSheep(resourceQuantity);
                break;
            case STONE:
                setStone(resourceQuantity);
                break;
            case WHEAT:
                setWheat(resourceQuantity);
                break;
        }
    }

    public int calculateSum() {
        return brick + wood + sheep + stone + wheat;
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
