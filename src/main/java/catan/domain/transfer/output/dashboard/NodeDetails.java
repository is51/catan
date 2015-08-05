package catan.domain.transfer.output.dashboard;

public class NodeDetails {
    private Integer nodeId;
    private String port;
    private BuildingDetails building;
    private Integer upHexId;
    private Integer rightDownHexId;
    private Integer leftDownHexId;
    private Integer rightUpEdgeId;
    private Integer dowEdgeId;
    private Integer leftUpEdgeId;

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
