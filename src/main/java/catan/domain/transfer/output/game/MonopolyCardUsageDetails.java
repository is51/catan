package catan.domain.transfer.output.game;

public class MonopolyCardUsageDetails {
    private Integer resourcesCount;

    public MonopolyCardUsageDetails() {
    }

    public MonopolyCardUsageDetails(Integer resourcesCount) {
        this.resourcesCount = resourcesCount;
    }

    public Integer getResourcesCount() {
        return resourcesCount;
    }

    public void setResourcesCount(Integer resourcesCount) {
        this.resourcesCount = resourcesCount;
    }
}
