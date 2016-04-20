package catan.domain.transfer.output.game;

public class BuildSettlementDetails {
    private boolean isLimitReached;

    public BuildSettlementDetails() {
    }

    public BuildSettlementDetails(String isLimitReached) {
        this.isLimitReached = Boolean.valueOf(isLimitReached);
    }

    public BuildSettlementDetails(boolean isLimitReached) {
        this.isLimitReached = isLimitReached;
    }

    public boolean isLimitReached() {
        return isLimitReached;
    }

    public void setLimitReached(boolean limitReached) {
        isLimitReached = limitReached;
    }
}
