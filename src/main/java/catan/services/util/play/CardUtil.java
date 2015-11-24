package catan.services.util.play;

import catan.domain.exception.PlayException;
import catan.domain.model.game.DevelopmentCards;
import catan.domain.model.game.types.DevelopmentCard;
import catan.services.util.random.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardUtil {
    private Logger log = LoggerFactory.getLogger(CardUtil.class);

    public static final String CARDS_ARE_OVER_ERROR = "CARDS_ARE_OVER";

    private RandomUtil randomUtil;

    public DevelopmentCard chooseDevelopmentCard(DevelopmentCards availableDevelopmentCards) throws PlayException {
        List<DevelopmentCard> availableDevelopmentCardsList = listAvailableDevCards(availableDevelopmentCards);
        validateThereAreAvailableCards(availableDevelopmentCardsList);

        return randomUtil.pullRandomDevelopmentCard(availableDevelopmentCardsList);
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
