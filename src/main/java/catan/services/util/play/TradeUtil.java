package catan.services.util.play;

import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import catan.domain.model.game.TradeProposal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TradeUtil {
    private Logger log = LoggerFactory.getLogger(TradeUtil.class);

    public static final String ERROR_CODE_ERROR = "ERROR";
    public static final String OFFER_ALREADY_ACCEPTED_ERROR = "OFFER_ALREADY_ACCEPTED";
    public static final String OFFER_IS_NOT_ACTIVE_ERROR = "OFFER_IS_NOT_ACTIVE";

    public void validateTradeIsNotEmpty(int brick, int wood, int sheep, int wheat, int stone) throws PlayException {
        if (brick == 0 && wood == 0 && sheep == 0 && wheat == 0 && stone == 0) {
            log.error("Trading request is empty. All requested resources has zero quantity");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public void validateThereIsNoNewOffers(Integer offerId, TradeProposal tradeProposal) throws PlayException {
        if (tradeProposal != null && tradeProposal.getOfferId() != null && !offerId.equals(tradeProposal.getOfferId())) {
            log.error("Trade proposal is not active already.");
            throw new PlayException(OFFER_IS_NOT_ACTIVE_ERROR);
        }
    }

    public void validateOfferIsNotAcceptedBefore(TradeProposal tradeProposal) throws PlayException {
        if (tradeProposal != null && tradeProposal.getOfferId() == null) {
            log.error("Trade proposal already accepted");
            throw new PlayException(OFFER_ALREADY_ACCEPTED_ERROR);
        }
    }

    public void validateUserHasRequestedResources(GameUserBean gameUser, int brick, int wood, int sheep, int wheat, int stone) throws PlayException {
        Resources userResources = gameUser.getResources();
        if (userResources.getBrick() < brick
                || userResources.getWood() < wood
                || userResources.getSheep() < sheep
                || userResources.getWheat() < wheat
                || userResources.getStone() < stone) {
            log.debug("User has not enough resources ({}) to accept trade proposal: [{}, {}, {}, {}, {}]", userResources, brick, wood, sheep, wheat, stone);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public void validateResourceQuantityToSellAndToBuyInTradePropose(int... resourcesQuantity) throws PlayException {
        int buyingResources = 0;
        int sellingResources = 0;
        for (int resourceQuantity : resourcesQuantity) {
            if (resourceQuantity > 0) {
                buyingResources += resourceQuantity;
            } else {
                sellingResources += resourceQuantity;
            }
        }

        if (buyingResources == 0) {
            log.error("User cannot propose trade without any resources to buy");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        if (sellingResources == 0) {
            log.error("User cannot propose trade without any resources to sell");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public void validateTradeBalanceIsZero(int tradingBalance) throws PlayException {
        if (tradingBalance != 0) {
            log.error("Trade balance is not zero: {}. Wrong resources quantity to trade", tradingBalance);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public int toValidResourceQuantityToTradeInPort(String resourceQuantityString, int usersResourceQuantity, int tradeCoefficient) throws PlayException {
        int resourceQuantity = toValidResourceQuantityToTrade(resourceQuantityString, usersResourceQuantity);

        if (resourceQuantity < 0 && resourceQuantity % tradeCoefficient != 0) {
            log.error("Invalid resource quantity to sell: {} should be divisible by {}", -resourceQuantity, tradeCoefficient);
            throw new PlayException(ERROR_CODE_ERROR);
        }

        return resourceQuantity;
    }

    public int toValidResourceQuantityToTrade(String resourceQuantityString, int usersResourceQuantity) throws PlayException {
        int resourceQuantity = ValidationUtil.toValidNumber(resourceQuantityString, log);

        if (-resourceQuantity > usersResourceQuantity) {
            log.error("User cannot sell more resources than he has: {} / {}", -resourceQuantity, usersResourceQuantity);
            throw new PlayException(ERROR_CODE_ERROR);
        }

        return resourceQuantity;
    }
}
