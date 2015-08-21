package catan.domain.transfer.output.dashboard;

import catan.domain.model.dashboard.EdgeBean;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EdgeDetails {
    private Integer edgeId;
    private BuildingDetails building;
    private String orientation;
    private LinkDetails hexes;
    private LinkDetails nodes;

    public EdgeDetails() {
    }

    public EdgeDetails(EdgeBean edge) {
        this.edgeId = edge.getId();
        this.orientation = edge.getOrientation().name();
        this.building = edge.getBuilding() != null ? new BuildingDetails(edge.getBuilding()) : null;
        this.hexes = new LinkDetails(edge.getHexes());
        this.nodes = new LinkDetails(edge.getNodes());
    }

    public Integer getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(Integer edgeId) {
        this.edgeId = edgeId;
    }

    public BuildingDetails getBuilding() {
        return building;
    }

    public void setBuilding(BuildingDetails building) {
        this.building = building;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public LinkDetails getHexes() {
        return hexes;
    }

    public void setHexes(LinkDetails hexes) {
        this.hexes = hexes;
    }

    public LinkDetails getNodes() {
        return nodes;
    }

    public void setNodes(LinkDetails nodes) {
        this.nodes = nodes;
    }
}
