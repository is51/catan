package catan.domain.transfer.output.game.actions;

import catan.domain.transfer.output.game.ResourcesDetails;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionParamsDetails {
    private Integer brick;
    private Integer wood;
    private Integer sheep;
    private Integer wheat;
    private Integer stone;
    private ResourcesDetails resources;
    private Integer offerId;
    private List<Integer> nodeIds;
    private List<Integer> edgeIds;
    private List<Integer> hexIds;

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

    public void setStone(Integer stone) {
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

    public List<Integer> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(List<Integer> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public List<Integer> getEdgeIds() {
        return edgeIds;
    }

    public void setEdgeIds(List<Integer> edgeIds) {
        this.edgeIds = edgeIds;
    }

    public List<Integer> getHexIds() {
        return hexIds;
    }

    public void setHexIds(List<Integer> hexIds) {
        this.hexIds = hexIds;
    }
}
