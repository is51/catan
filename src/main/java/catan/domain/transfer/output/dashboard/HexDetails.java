package catan.domain.transfer.output.dashboard;

import catan.domain.model.dashboard.HexBean;

public class HexDetails {
    private Integer hexId;
    private Integer x;
    private Integer y;
    private String type;
    private Integer dice;
    private boolean robbed;
    private Integer upNodeId;
    private Integer rightUpNodeId;
    private Integer rightDownNodeId;
    private Integer downNodeId;
    private Integer leftDownNodeId;
    private Integer leftUpNodeId;
    private Integer rightUpEdgeId;
    private Integer rightEdgeId;
    private Integer rightDownEdgeId;
    private Integer leftDownEdgeId;
    private Integer leftEdgeId;
    private Integer leftUpEdgeId;

    public HexDetails(HexBean hex) {
        this.hexId = hex.getId();
        this.x = hex.getCoordinates().getxCoordinate();
        this.y = hex.getCoordinates().getyCoordinate();
        this.type = hex.getResourceType().name();
        this.dice = hex.getDice();
        this.robbed = hex.isRobbed();
        this.upNodeId = hex.getUpNode().getId();
        this.rightUpNodeId = hex.getRightUpNode().getId();
        this.rightDownNodeId = hex.getRightDownNode().getId();
        this.downNodeId = hex.getDownNode().getId();
        this.leftDownNodeId = hex.getLeftDownNode().getId();
        this.leftUpNodeId = hex.getLeftUpNode().getId();
    }

    public Integer getHexId() {
        return hexId;
    }

    public void setHexId(Integer hexId) {
        this.hexId = hexId;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDice() {
        return dice;
    }

    public void setDice(Integer dice) {
        this.dice = dice;
    }

    public boolean isRobbed() {
        return robbed;
    }

    public void setRobbed(boolean robbed) {
        this.robbed = robbed;
    }

    public Integer getUpNodeId() {
        return upNodeId;
    }

    public void setUpNodeId(Integer upNodeId) {
        this.upNodeId = upNodeId;
    }

    public Integer getRightUpNodeId() {
        return rightUpNodeId;
    }

    public void setRightUpNodeId(Integer rightUpNodeId) {
        this.rightUpNodeId = rightUpNodeId;
    }

    public Integer getRightDownNodeId() {
        return rightDownNodeId;
    }

    public void setRightDownNodeId(Integer rightDownNodeId) {
        this.rightDownNodeId = rightDownNodeId;
    }

    public Integer getDownNodeId() {
        return downNodeId;
    }

    public void setDownNodeId(Integer downNodeId) {
        this.downNodeId = downNodeId;
    }

    public Integer getLeftDownNodeId() {
        return leftDownNodeId;
    }

    public void setLeftDownNodeId(Integer leftDownNodeId) {
        this.leftDownNodeId = leftDownNodeId;
    }

    public Integer getLeftUpNodeId() {
        return leftUpNodeId;
    }

    public void setLeftUpNodeId(Integer leftUpNodeId) {
        this.leftUpNodeId = leftUpNodeId;
    }

    public Integer getRightUpEdgeId() {
        return rightUpEdgeId;
    }

    public void setRightUpEdgeId(Integer rightUpEdgeId) {
        this.rightUpEdgeId = rightUpEdgeId;
    }

    public Integer getRightEdgeId() {
        return rightEdgeId;
    }

    public void setRightEdgeId(Integer rightEdgeId) {
        this.rightEdgeId = rightEdgeId;
    }

    public Integer getRightDownEdgeId() {
        return rightDownEdgeId;
    }

    public void setRightDownEdgeId(Integer rightDownEdgeId) {
        this.rightDownEdgeId = rightDownEdgeId;
    }

    public Integer getLeftDownEdgeId() {
        return leftDownEdgeId;
    }

    public void setLeftDownEdgeId(Integer leftDownEdgeId) {
        this.leftDownEdgeId = leftDownEdgeId;
    }

    public Integer getLeftEdgeId() {
        return leftEdgeId;
    }

    public void setLeftEdgeId(Integer leftEdgeId) {
        this.leftEdgeId = leftEdgeId;
    }

    public Integer getLeftUpEdgeId() {
        return leftUpEdgeId;
    }

    public void setLeftUpEdgeId(Integer leftUpEdgeId) {
        this.leftUpEdgeId = leftUpEdgeId;
    }
}

