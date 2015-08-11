package catan.services;

import catan.domain.model.dashboard.types.HexType;
import org.springframework.stereotype.Component;

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

    public HexType generateRandomHexType() {
        int randomHexTypeId = (int) (rvg.randomValue() * 6);
        switch (randomHexTypeId){
            case 0:
                return HexType.BRICK;
            case 1:
                return HexType.WOOD;
            case 2:
                return HexType.SHEEP;
            case 3:
                return HexType.WHEAT;
            case 4:
                return HexType.STONE;
            default:
                return HexType.EMPTY;
        }
    }

    public void setRvg(RandomValueGenerator rvg) {
        this.rvg = rvg;
    }
}
