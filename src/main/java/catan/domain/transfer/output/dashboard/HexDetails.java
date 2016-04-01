package catan.domain.transfer.output.dashboard;

import catan.domain.model.dashboard.HexBean;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HexDetails {
    private Integer hexId;
    private Integer x;
    private Integer y;
    private String type;
    private Integer dice;
    private boolean robbed;
    private LinkIdsDetails edgesIds;
    private LinkIdsDetails nodesIds;

    public HexDetails() {
    }

    public HexDetails(HexBean hex) {
        this.hexId = hex.getAbsoluteId();
        this.x = hex.getCoordinates().getxCoordinate();
        this.y = hex.getCoordinates().getyCoordinate();
        this.type = hex.getResourceType().name();
        this.dice = hex.getDice();
        this.robbed = hex.isRobbed();
        this.edgesIds = new LinkIdsDetails(hex.getEdges());
        this.nodesIds = new LinkIdsDetails(hex.getNodes());
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

    public LinkIdsDetails getEdgesIds() {
        return edgesIds;
    }

    public void setEdgesIds(LinkIdsDetails edgesIds) {
        this.edgesIds = edgesIds;
    }

    public LinkIdsDetails getNodesIds() {
        return nodesIds;
    }

    public void setNodesIds(LinkIdsDetails nodesIds) {
        this.nodesIds = nodesIds;
    }
}

