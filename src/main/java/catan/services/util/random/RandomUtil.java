package catan.services.util.random;

import catan.domain.model.dashboard.types.HexType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public int pullRandomHexDiceNumber(List<Integer> possibleDiceNumbers) {
        int randomDiceNumber = (int) (rvg.randomValue() * possibleDiceNumbers.size());

        return possibleDiceNumbers.remove(randomDiceNumber);
    }

    public int getRandomDiceNumber() {
        int diceNumber = (int) Math.ceil(rvg.randomValue() * 6);
        return diceNumber == 0 ? 1 : diceNumber;
    }

    public void setRvg(RandomValueGenerator rvg) {
        this.rvg = rvg;
    }
}
