package catan.domain.transfer.output.dashboard;

import catan.domain.model.dashboard.EdgeBean;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EdgeDetails {
    private Integer edgeId;
    private BuildingDetails building;
    private String orientation;
    private LinkIdsDetails hexIds;
    private LinkIdsDetails nodeIds;

    public EdgeDetails() {
    }

    public EdgeDetails(EdgeBean edge) {
        this.edgeId = edge.getId();
        this.orientation = edge.getOrientation().name();
        this.building = edge.getBuilding() != null ? new BuildingDetails(edge.getBuilding()) : null;
        this.hexIds = new LinkIdsDetails(edge.getHexes());
        this.nodeIds = new LinkIdsDetails(edge.getNodes());
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

    public LinkIdsDetails getHexIds() {
        return hexIds;
    }

    public void setHexIds(LinkIdsDetails hexIds) {
        this.hexIds = hexIds;
    }

    public LinkIdsDetails getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(LinkIdsDetails nodeIds) {
        this.nodeIds = nodeIds;
    }
}
