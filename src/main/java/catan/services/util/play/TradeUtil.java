package catan.services.util.play;

import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.Resources;
import catan.domain.model.game.TradeProposal;
import catan.domain.model.game.actions.ResourcesParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TradeUtil {
    private Logger log = LoggerFactory.getLogger(TradeUtil.class);

    public static final String ERROR_CODE_ERROR = "ERROR";
    public static final String OFFER_ALREADY_ACCEPTED_ERROR = "OFFER_ALREADY_ACCEPTED";
    public static final String OFFER_IS_NOT_ACTIVE_ERROR = "OFFER_IS_NOT_ACTIVE";

    public void validateTradeIsNotEmpty(Resources resourcesToTrade) throws PlayException {
        for (int resQuantity : resourcesToTrade.resourcesToMap().values()) {
            if (resQuantity != 0) {
                return;
            }
        }
        log.error("Trading request is empty. All requested resources has zero quantity");
        throw new PlayException(ERROR_CODE_ERROR);
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

    public void validateUserHasRequestedResources(Resources userResources, Resources resourcesToBuy) throws PlayException {
        Map<HexType, Integer> userResourcesMap = userResources.resourcesToMap();
        Map<HexType, Integer> resourcesToBuyMap = resourcesToBuy.resourcesToMap();
        for (HexType hexType : userResourcesMap.keySet()) {
            if (userResourcesMap.get(hexType) < resourcesToBuyMap.get(hexType)) {
                log.debug("User has not enough resources ({}) to accept trade proposal. Required resources: {}]", userResources, resourcesToBuy);
                throw new PlayException(ERROR_CODE_ERROR);
            }
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

    public void validateTradeBalanceIsZero(Resources resourcesToTrade, ResourcesParams tradePortParams) throws PlayException {
        int tradingBalance = 0;
        Map<HexType, Integer> resourcesToTradeMap = resourcesToTrade.resourcesToMap();
        for (HexType hexType : resourcesToTradeMap.keySet()) {
            int resQuantity = resourcesToTradeMap.get(hexType);
            tradingBalance += resQuantity < 0 ? resQuantity / tradePortParams.paramOf(hexType) : resQuantity;
        }

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
