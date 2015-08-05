package catan.domain.transfer.output.dashboard;

public class EdgeDetails {
    private Integer edgeId;
    private BuildingDetails building;
    private Integer upHex;
    private Integer downHex;
    private Integer leftNode;
    private Integer rightNode;

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
