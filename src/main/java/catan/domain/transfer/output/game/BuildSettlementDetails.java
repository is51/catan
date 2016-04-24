package catan.domain.transfer.output.game;

public class BuildSettlementDetails {
    private boolean limitReached;

    public BuildSettlementDetails() {
    }

    public BuildSettlementDetails(String limitReached) {
        this.limitReached = Boolean.valueOf(limitReached);
    }

    public BuildSettlementDetails(boolean limitReached) {
        this.limitReached = limitReached;
    }

    public boolean isLimitReached() {
        return limitReached;
    }

    public void setLimitReached(boolean limitReached) {
        this.limitReached = limitReached;
    }
}
