package catan.services.util.play;

import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.DevelopmentCards;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import catan.domain.model.game.types.DevelopmentCard;
import catan.services.util.random.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class CardUtil {
    private Logger log = LoggerFactory.getLogger(CardUtil.class);

    public static final String CARDS_ARE_OVER_ERROR = "CARDS_ARE_OVER";
    public static final String CARD_ALREADY_USED_IN_CURRENT_TURN_ERROR = "CARD_ALREADY_USED_IN_CURRENT_TURN";
    public static final String CARD_BOUGHT_IN_CURRENT_TURN_ERROR = "CARD_BOUGHT_IN_CURRENT_TURN";
    public static final String ROAD_CANNOT_BE_BUILT_ERROR = "ROAD_CANNOT_BE_BUILT";

    private RandomUtil randomUtil;

    public DevelopmentCard chooseDevelopmentCard(DevelopmentCards availableDevelopmentCards) throws PlayException {
        List<DevelopmentCard> availableDevelopmentCardsList = listAvailableDevCards(availableDevelopmentCards);
        validateThereAreAvailableCards(availableDevelopmentCardsList);

        return randomUtil.pullRandomDevelopmentCard(availableDevelopmentCardsList);
    }

    public void giveDevelopmentCardToUser(GameUserBean gameUser, DevelopmentCards availableDevelopmentCards, DevelopmentCard chosenDevelopmentCard) {
        DevelopmentCards usersDevelopmentCards = gameUser.getDevelopmentCards();
        usersDevelopmentCards.increaseQuantityByOne(chosenDevelopmentCard);
        availableDevelopmentCards.decreaseQuantityByOne(chosenDevelopmentCard);
    }

    public void takeDevelopmentCardFromPlayer(GameUserBean gameUser, DevelopmentCard developmentCard) {
        gameUser.getDevelopmentCards().decreaseQuantityByOne(developmentCard);
        gameUser.getDevelopmentCardsReadyForUsing().decreaseQuantityByOne(developmentCard);
    }

    public void giveTwoResourcesToPlayer(Resources userResources, HexType firstResource, HexType secondResource) {
        int currentFirstResourceQuantity = userResources.quantityOf(firstResource);
        userResources.updateResourceQuantity(firstResource, currentFirstResourceQuantity + 1);
        int currentSecondResourceQuantity = userResources.quantityOf(secondResource);
        userResources.updateResourceQuantity(secondResource, currentSecondResourceQuantity + 1);
    }

    public Integer takeResourceFromRivalsAndGiveItAwayToPlayer(GameUserBean currentGameUser, GameBean game, HexType resource) {
        Integer takenResourcesCount = 0;
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (!gameUser.equals(currentGameUser)) {
                Integer usersResourceQuantity = gameUser.getResources().quantityOf(resource);
                gameUser.getResources().updateResourceQuantity(resource, 0);
                takenResourcesCount += usersResourceQuantity;
            }
        }

        if (takenResourcesCount > 0) {
            Integer currentUsersResourceQuantity = currentGameUser.getResources().quantityOf(resource);
            currentGameUser.getResources().updateResourceQuantity(resource, currentUsersResourceQuantity + takenResourcesCount);
        }
        return takenResourcesCount;
    }

    public Integer defineRoadsQuantityToBuild(GameUserBean gameUser, GameBean game) throws PlayException {
        List<EdgeBean> availableEdges = new ArrayList<EdgeBean>(game.fetchEdgesAccessibleForBuildingRoad(gameUser));
        switch (availableEdges.size()) {
            case 0:
                log.debug("There are no available edges build road for current player");
                throw new PlayException(ROAD_CANNOT_BE_BUILT_ERROR);
            case 1:
                Set<EdgeBean> edgesNextToAvailable = availableEdges.get(0).fetchNeighborEdgesAccessibleForBuildingRoad(gameUser);
                if (edgesNextToAvailable.size() > 0) {
                    return 2;
                } else {
                    return 1;
                }
            default:
                return 2;
        }
    }

    public void validateUserDidNotBoughtCardInCurrentTurn(GameUserBean gameUser, DevelopmentCard developmentCard) throws PlayException {
        if (gameUser.getDevelopmentCardsReadyForUsing().quantityOf(developmentCard) == 0) {
            log.debug("Cannot use card. It was bought in current turn: {}", developmentCard);
            throw new PlayException(CARD_BOUGHT_IN_CURRENT_TURN_ERROR);
        }
    }

    public void validateUserDidNotUsedCardsInCurrentTurn(GameBean game) throws PlayException {
        if (game.isDevelopmentCardUsed()) {
            log.debug("Cannot use card. It was already used in current turn");
            throw new PlayException(CARD_ALREADY_USED_IN_CURRENT_TURN_ERROR);
        }
    }

    private void validateThereAreAvailableCards(List<DevelopmentCard> availableDevelopmentCardsQuantity) throws PlayException {
        if (availableDevelopmentCardsQuantity.size() == 0) {
            log.debug("No available cards");
            throw new PlayException(CARDS_ARE_OVER_ERROR);
        }
    }

    private List<DevelopmentCard> listAvailableDevCards(DevelopmentCards availableDevelopmentCards) {
        List<DevelopmentCard> availableDevelopmentCardsList = new ArrayList<DevelopmentCard>();
        for (DevelopmentCard developmentCard : DevelopmentCard.values()) {
            for (int i = 0; i < availableDevelopmentCards.quantityOf(developmentCard); i++) {
                availableDevelopmentCardsList.add(developmentCard);
            }
        }

        return availableDevelopmentCardsList;
    }

    @Autowired
    public void setRandomUtil(RandomUtil randomUtil) {
        this.randomUtil = randomUtil;
    }
}
