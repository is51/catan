package catan.domain.transfer.output.dashboard;

import catan.domain.model.dashboard.EdgeBean;

public class EdgeDetails {
    private Integer edgeId;
    private BuildingDetails building;
    private String orientation;
    private Integer upHex;
    private Integer downHex;
    private Integer leftNode;
    private Integer rightNode;

    public EdgeDetails() {
    }

    public EdgeDetails(EdgeBean edge) {
        this.edgeId = edge.getId();
        this.orientation = edge.getOrientation().name();
        this.building = edge.getBuilding() != null
                ? new BuildingDetails(edge.getBuilding())
                : null;
        this.upHex = edge.getUpHex() != null
                ? edge.getUpHex().getId()
                : null;
        this.downHex = edge.getDownHex() != null
                ? edge.getDownHex().getId()
                : null;
        this.leftNode = edge.getLeftNode().getId();
        this.rightNode = edge.getRightNode().getId();
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

    public Integer getUpHex() {
        return upHex;
    }

    public void setUpHex(Integer upHex) {
        this.upHex = upHex;
    }

    public Integer getDownHex() {
        return downHex;
    }

    public void setDownHex(Integer downHex) {
        this.downHex = downHex;
    }

    public Integer getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Integer leftNode) {
        this.leftNode = leftNode;
    }

    public Integer getRightNode() {
        return rightNode;
    }

    public void setRightNode(Integer rightNode) {
        this.rightNode = rightNode;
    }
}
