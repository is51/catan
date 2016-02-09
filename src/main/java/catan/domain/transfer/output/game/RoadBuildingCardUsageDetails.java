package catan.domain.transfer.output.game;

public class RoadBuildingCardUsageDetails {
    private Integer roadsCount;

    public RoadBuildingCardUsageDetails() {
    }

    public RoadBuildingCardUsageDetails(Integer roadsCount) {
        this.roadsCount = roadsCount;
    }

    public Integer getRoadsCount() {
        return roadsCount;
    }

    public void setRoadsCount(Integer card) {
        this.roadsCount = roadsCount;
    }
}
