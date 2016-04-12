package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import catan.domain.model.game.TradeProposal;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidationUtil {
    private Logger log = LoggerFactory.getLogger(ValidationUtil.class);

    private PlayUtil playUtil;

    public static final String ERROR_CODE_ERROR = "ERROR";
    public static final String OFFER_ALREADY_ACCEPTED_ERROR = "OFFER_ALREADY_ACCEPTED";
    public static final String OFFER_IS_NOT_ACTIVE_ERROR = "OFFER_IS_NOT_ACTIVE";

    public void validateActionIsAllowedForUser(GameUserBean gameUser, GameUserActionCode requiredAction) throws PlayException, GameException {
        if (requiredAction.equals(GameUserActionCode.TRADE_REPLY) && gameUser.isAvailableTradeReply()) {
            return;
        }

        String availableActionsJson = gameUser.getAvailableActions();
        AvailableActions availableActions = playUtil.toAvailableActionsFromJson(availableActionsJson);
        for (Action allowedActions : availableActions.getList()) {
            if (allowedActions.getCode().equals(requiredAction.name())) {
                return;
            }
        }

        log.debug("Required action {} is not allowed for {}", requiredAction.name(), gameUser);
        throw new PlayException(ERROR_CODE_ERROR);
    }

    public void validateGameUserCouldBeRobbed(GameUserBean gameUser) throws PlayException {
        if (gameUser.equals(gameUser.getGame().fetchActiveGameUser())) {
            log.error("Player couldn't rob himself");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        for (HexBean hex : gameUser.getGame().getHexes()) {
            if (!hex.isRobbed()) {
                continue;
            }
            if (!hex.fetchGameUsersWithBuildingsAtNodes().remove(gameUser)) {
                log.error("GameUser {} doesn't have any buildings at robbed hex {} and couldn't be robbed", gameUser.getGameUserId(), hex.getAbsoluteId());
                throw new PlayException(ERROR_CODE_ERROR);
            }
            break;
        }
    }

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

    public void validateSumOfResourcesToKickOffIsTheHalfOfTotalResources(int sumOfUsersResources, int sumOfResourcesKickingOff) throws PlayException {
        if (sumOfResourcesKickingOff != sumOfUsersResources / 2) {
            log.error("Wrong resources quantity: {}", sumOfResourcesKickingOff);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public int toValidResourceQuantityToKickOff(String resourceQuantityString, int usersResourceQuantity) throws PlayException {
        int resourceQuantity = toValidNumber(resourceQuantityString);

        if (resourceQuantity < 0) {
            log.error("Resource quantity could not be below 0");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        if (resourceQuantity > usersResourceQuantity) {
            log.error("User cannot kick of more resources than he has: {} / {}", resourceQuantity, usersResourceQuantity);
            throw new PlayException(ERROR_CODE_ERROR);
        }

        return resourceQuantity;
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
        int resourceQuantity = toValidNumber(resourceQuantityString);

        if (-resourceQuantity > usersResourceQuantity) {
            log.error("User cannot sell more resources than he has: {} / {}", -resourceQuantity, usersResourceQuantity);
            throw new PlayException(ERROR_CODE_ERROR);
        }

        return resourceQuantity;
    }

    public HexBean toValidHex(GameBean game, String hexAbsoluteIdString) throws PlayException {
        int hexAbsoluteId = toValidNumber(hexAbsoluteIdString);

        for (HexBean hex : game.getHexes()) {
            if (hex.getAbsoluteId() == hexAbsoluteId) {
                return hex;
            }
        }

        log.error("Hex {} does not belong to game {}", hexAbsoluteId, game.getGameId());
        throw new PlayException(ERROR_CODE_ERROR);
    }

    public int toValidNumber(String numberString) throws PlayException {
        try {
            return Integer.parseInt(numberString);
        } catch (Exception e) {
            log.error("Cannot convert number to integer value: {}", numberString);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public void validateHexCouldBeRobbed(HexBean hexToRob) throws PlayException {
        if (hexToRob.isRobbed() || hexToRob.getResourceType().equals(HexType.EMPTY)) {
            log.error("Hex {} cannot be robbed", hexToRob.getAbsoluteId());
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public HexType toValidResourceType(String resourceString) throws PlayException {
        try {
            return HexType.valueOf(resourceString);
        } catch (Exception e) {
            log.debug("Illegal resource type: {}", resourceString);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public void validateGameIdNotEmpty(String gameId) throws GameException {
        if (gameId == null || gameId.trim().length() == 0) {
            log.error("Cannot get game with empty gameId");
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    public void validateUserNotEmpty(UserBean user) throws PlayException {
        if (user == null) {
            log.debug("User should not be empty");
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public void validateGameStatusIsPlaying(GameBean game) throws GameException {
        if (game.getStatus() != GameStatus.PLAYING) {
            log.debug("User cannot do this action in current game status: {} instead of {}", game.getStatus(), GameStatus.PLAYING);
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    @Autowired
    public void setPlayUtil(PlayUtil playUtil) {
        this.playUtil = playUtil;
    }
}
