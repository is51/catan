package catan.domain.transfer.output.dashboard;

import catan.domain.model.dashboard.NodeBean;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodeDetails {
    private Integer nodeId;
    private String port;
    private BuildingDetails building;
    private String orientation;
    private LinkIdsDetails hexesIds;
    private LinkIdsDetails edgesIds;

    public NodeDetails() {
    }

    public NodeDetails(NodeBean node) {
        this.nodeId = node.getAbsoluteId();
        this.port = node.getPort().name();
        this.orientation = node.getOrientation().name();
        this.building = node.getBuilding() != null ? new BuildingDetails(node.getBuilding()) : null;
        this.hexesIds = new LinkIdsDetails(node.getHexes());
        this.edgesIds = new LinkIdsDetails(node.getEdges());
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
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

    public LinkIdsDetails getEdgesIds() {
        return edgesIds;
    }

    public void setEdgesIds(LinkIdsDetails edgesIds) {
        this.edgesIds = edgesIds;
    }
}
