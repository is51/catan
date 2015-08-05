package catan.domain.transfer.output.dashboard;

public class BuildingDetails {
    private Integer ownerGameUserId;
    private String built;

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
