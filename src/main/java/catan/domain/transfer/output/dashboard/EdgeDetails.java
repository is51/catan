package catan.domain.transfer.output.dashboard;

import java.util.List;

public class EdgeDetails {
    private Integer id;
    private String built;
    private Integer buildingOwnerId;
    private List<Integer> hexIds;
    private List<Integer> nodeIds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBuilt() {
        return built;
    }

    public void setBuilt(String built) {
        this.built = built;
    }

    public Integer getBuildingOwnerId() {
        return buildingOwnerId;
    }

    public void setBuildingOwnerId(Integer buildingOwnerId) {
        this.buildingOwnerId = buildingOwnerId;
    }

    public List<Integer> getHexIds() {
        return hexIds;
    }

    public void setHexIds(List<Integer> hexIds) {
        this.hexIds = hexIds;
    }

    public List<Integer> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(List<Integer> nodeIds) {
        this.nodeIds = nodeIds;
    }
}
