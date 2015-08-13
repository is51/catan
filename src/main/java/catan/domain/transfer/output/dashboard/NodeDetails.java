package catan.domain.transfer.output.dashboard;

import catan.domain.model.dashboard.NodeBean;

public class NodeDetails {
    private Integer nodeId;
    private String port;
    private BuildingDetails building;
    private String orientation;
    private Integer upHexId;
    private Integer rightDownHexId;
    private Integer leftDownHexId;
    private Integer rightUpEdgeId;
    private Integer dowEdgeId;
    private Integer leftUpEdgeId;

    public NodeDetails() {
    }

    public NodeDetails(NodeBean node) {
        this.nodeId = node.getId();
        this.port = node.getPort().name();
        this.orientation = node.getOrientation().name();
        this.building = node.getBuilding() != null
                ? new BuildingDetails(node.getBuilding())
                : null;
        this.upHexId =  node.getUpHex() != null
                ? node.getUpHex().getId()
                : null;
        this.rightDownHexId = node.getRightDownHex() != null
                ? node.getRightDownHex().getId()
                : null;
        this.leftDownHexId = node.getLeftDownHex() != null
                ? node.getLeftDownHex().getId()
                : null;
        this.rightUpEdgeId = node.getRightUpEdge() != null
                ? node.getRightUpEdge().getId()
                : null;
        this.dowEdgeId = node.getDownEdge() != null
                ? node.getDownEdge().getId()
                : null;
        this.leftUpEdgeId = node.getLeftUpEdge() != null
                ? node.getLeftUpEdge().getId()
                : null;
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

    public Integer getUpHexId() {
        return upHexId;
    }

    public void setUpHexId(Integer upHexId) {
        this.upHexId = upHexId;
    }

    public Integer getRightDownHexId() {
        return rightDownHexId;
    }

    public void setRightDownHexId(Integer rightDownHexId) {
        this.rightDownHexId = rightDownHexId;
    }

    public Integer getLeftDownHexId() {
        return leftDownHexId;
    }

    public void setLeftDownHexId(Integer leftDownHexId) {
        this.leftDownHexId = leftDownHexId;
    }

    public Integer getRightUpEdgeId() {
        return rightUpEdgeId;
    }

    public void setRightUpEdgeId(Integer rightUpEdgeId) {
        this.rightUpEdgeId = rightUpEdgeId;
    }

    public Integer getDowEdgeId() {
        return dowEdgeId;
    }

    public void setDowEdgeId(Integer dowEdgeId) {
        this.dowEdgeId = dowEdgeId;
    }

    public Integer getLeftUpEdgeId() {
        return leftUpEdgeId;
    }

    public void setLeftUpEdgeId(Integer leftUpEdgeId) {
        this.leftUpEdgeId = leftUpEdgeId;
    }
}
