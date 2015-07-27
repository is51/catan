package catan.domain.transfer.output.dashboard;

import java.util.List;

public class NodeDetails {
    private Integer id;
    private String port;
    private String built;
    private Integer buildingOwnerId;
    private List<Integer> hexIds;
    private List<Integer> edgeIds;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getBuilt() {
        return built;
    }

    public void setBuilt(String built) {
        this.built = built;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<Integer> getEdgeIds() {
        return edgeIds;
    }

    public void setEdgeIds(List<Integer> edgeIds) {
        this.edgeIds = edgeIds;
    }
}
