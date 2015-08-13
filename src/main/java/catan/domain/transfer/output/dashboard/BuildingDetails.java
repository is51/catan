package catan.domain.transfer.output.dashboard;

import catan.domain.model.dashboard.BuildingBean;

public class BuildingDetails {
    private Integer ownerGameUserId;
    private String built;

    public BuildingDetails() {
    }

    public BuildingDetails(BuildingBean building) {
        this.ownerGameUserId = building.getBuildingOwner().getGameUserId();
        this.built = building.getBuilt().name();
    }

    public Integer getOwnerGameUserId() {
        return ownerGameUserId;
    }

    public void setOwnerGameUserId(Integer ownerGameUserId) {
        this.ownerGameUserId = ownerGameUserId;
    }

    public String getBuilt() {
        return built;
    }

    public void setBuilt(String built) {
        this.built = built;
    }
}
