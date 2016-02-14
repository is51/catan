package catan.controllers.ctf;

import catan.controllers.util.PlayTestUtil;

public class TradeReplyAction {
    private final ReplyType replyType;
    protected String userToken;
    protected Scenario scenario;

    public TradeReplyAction(String userToken, Scenario scenario, ReplyType replyType) {
        this.userToken = userToken;
        this.scenario = scenario;
        this.replyType = replyType;
    }

    public Scenario withOfferId(int offerId) {
        switch(replyType){
            case ACCEPT:
                scenario.lastApiResponse = PlayTestUtil.tradeAccept(userToken, scenario.gameId, offerId);
                break;
            case DECLINE:
                scenario.lastApiResponse = PlayTestUtil.tradeDecline(userToken, scenario.gameId, offerId);
                break;
        }

        return scenario;
    }

    protected enum ReplyType {
        ACCEPT, DECLINE
    }
}
