package catan.domain.transfer.output.dashboard;

import catan.domain.model.dashboard.EdgeBean;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EdgeDetails {
    private Integer edgeId;
    private BuildingDetails building;
    private String orientation;
    private LinkIdsDetails hexesIds;
    private LinkIdsDetails nodesIds;

    public EdgeDetails() {
    }

    public EdgeDetails(EdgeBean edge) {
        this.edgeId = edge.getAbsoluteId();
        this.orientation = edge.getOrientation().name();
        this.building = edge.getBuilding() != null ? new BuildingDetails(edge.getBuilding()) : null;
        this.hexesIds = new LinkIdsDetails(edge.getHexes());
        this.nodesIds = new LinkIdsDetails(edge.getNodes());
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

    public LinkIdsDetails getHexesIds() {
        return hexesIds;
    }

    public void setHexesIds(LinkIdsDetails hexesIds) {
        this.hexesIds = hexesIds;
    }

    public LinkIdsDetails getNodesIds() {
        return nodesIds;
    }

    public void setNodesIds(LinkIdsDetails nodesIds) {
        this.nodesIds = nodesIds;
    }
}
