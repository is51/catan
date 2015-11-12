package catan.services.util.random;

import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.DevelopmentCard;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class RandomUtil {

    private RandomValueGenerator rvg = new RandomValueGenerator();

    public String generateRandomPrivateCode(int numberOfDigits) {
        int digits = 1;
        for (int i = 1; i < numberOfDigits; i++) {
            digits *= 10;
        }

        char firstTwoDigits = (char) (65 + rvg.randomValue() * 26);
        char secondTwoDigits = (char) (65 + rvg.randomValue() * 26);
        int remainingDigits = (int) (digits + rvg.randomValue() * digits * 9);


        return "" + firstTwoDigits + secondTwoDigits + remainingDigits;
    }

    public Integer pullRandomMoveOrder(List<Integer> moveOrderSequence) {
        int randomMoveOrderId = (int) (rvg.randomValue() * moveOrderSequence.size());
        return moveOrderSequence.remove(randomMoveOrderId);
    }

    public HexType pullRandomHexType(List<HexType> possibleHexTypes) {
        int randomHexTypeId = (int) (rvg.randomValue() * possibleHexTypes.size());

        return possibleHexTypes.remove(randomHexTypeId);
    }

    //TODO: add rule to avoid placing 6 and 8 dice numbers close to each other
    public int pullRandomDiceNumber(List<Integer> possibleDiceNumbers) {
        int randomDiceNumber = (int) (rvg.randomValue() * possibleDiceNumbers.size());

        return possibleDiceNumbers.remove(randomDiceNumber);
    }

    public DevelopmentCard pullRandomDevelopmentCard(List<DevelopmentCard> availableDevCards) {
        int randomAvailableDevCardIndex = (int) (rvg.randomValue() * availableDevCards.size());
        return availableDevCards.remove(randomAvailableDevCardIndex);
    }

    public void setRvg(RandomValueGenerator rvg) {
        this.rvg = rvg;
    }
}
