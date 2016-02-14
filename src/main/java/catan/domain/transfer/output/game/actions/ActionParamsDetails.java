package catan.domain.transfer.output.game.actions;

import catan.domain.transfer.output.game.ResourcesDetails;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionParamsDetails {
    private Integer brick;
    private Integer wood;
    private Integer sheep;
    private Integer wheat;
    private Integer stone;
    private ResourcesDetails resources;
    private Integer offerId;

    public ActionParamsDetails() {
    }

    public Integer getBrick() {
        return brick;
    }

    public void setBrick(Integer brick) {
        this.brick = brick;
    }

    public Integer getWood() {
        return wood;
    }

    public void setWood(Integer wood) {
        this.wood = wood;
    }

    public Integer getSheep() {
        return sheep;
    }

    public void setSheep(Integer sheep) {
        this.sheep = sheep;
    }

    public Integer getWheat() {
        return wheat;
    }

    public void setWheat(Integer wheat) {
        this.wheat = wheat;
    }

    public Integer getStone() {
        return stone;
    }

    public void setStone(int stone) {
        this.stone = stone;
    }

    public ResourcesDetails getResources() {
        return resources;
    }

    public void setResources(ResourcesDetails resources) {
        this.resources = resources;
    }

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }
}
