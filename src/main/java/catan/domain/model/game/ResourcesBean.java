package catan.domain.model.game;

import org.apache.commons.lang.builder.ToStringBuilder;
import javax.persistence.*;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Embeddable
public class ResourcesBean {

    @Column(name = "BRICK", nullable = false)
    private int brick;

    @Column(name = "WOOD", nullable = false)
    private int wood;

    @Column(name = "SHEEP", nullable = false)
    private int sheep;

    @Column(name = "WHEAT", nullable = false)
    private int wheat;

    @Column(name = "STONE", nullable = false)
    private int stone;

    public ResourcesBean() {
    }

    public ResourcesBean(int brick, int wood, int sheep, int wheat, int stone) {
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
