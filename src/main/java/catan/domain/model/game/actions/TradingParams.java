package catan.domain.model.game.actions;

import catan.domain.model.game.TradeProposal;

public class TradingParams extends ActionParams {

    private ResourcesParams resources;
    private Integer offerId;

    public TradingParams() {
    }

    public TradingParams(TradeProposal tradeProposal) {
        this.resources = new ResourcesParams(tradeProposal.getBrick(),
                                             tradeProposal.getWood(),
                                             tradeProposal.getSheep(),
                                             tradeProposal.getWheat(),
                                             tradeProposal.getStone());
        this.offerId = tradeProposal.getOfferId();
    }

    public ResourcesParams getResources() {
        return resources;
    }

    public void setResources(ResourcesParams resources) {
        this.resources = resources;
    }

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }
}
