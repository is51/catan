package catan.services;

import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodePortType;
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

    public NodePortType generateRandomNodePortType() {
        int randomPortTypeId = (int) (1 + rvg.randomValue() * 6);
        switch (randomPortTypeId){
            case 1:
                return NodePortType.BRICK;
            case 2:
                return NodePortType.WOOD;
            case 3:
                return NodePortType.SHEEP;
            case 4:
                return NodePortType.WHEAT;
            case 5:
                return NodePortType.STONE;
            case 6:
                return NodePortType.ANY;
            default:
                return NodePortType.NONE;
        }
    }

    public void setRvg(RandomValueGenerator rvg) {
        this.rvg = rvg;
    }
}
