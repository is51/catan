package catan.services;

import catan.domain.model.dashboard.types.HexType;
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

    public HexType pullRandomHexType(List<HexType> possibleHexTypes) {
        int randomHexTypeId = (int) (rvg.randomValue() * possibleHexTypes.size());

        return possibleHexTypes.remove(randomHexTypeId);
    }

    //TODO: add rule to avoid placing 6 and 8 dice numbers close to each other
    public int pullRandomDiceNumber(List<Integer> possibleDiceNumbers) {
        int randomDiceNumber = (int) (rvg.randomValue() * possibleDiceNumbers.size());

        return possibleDiceNumbers.remove(randomDiceNumber);
    }

    public void setRvg(RandomValueGenerator rvg) {
        this.rvg = rvg;
    }
}
