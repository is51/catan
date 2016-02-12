package catan.services.util.random;

import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.Resources;
import catan.domain.model.game.types.DevelopmentCard;

import java.util.List;

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

    public HexType pullRandomHexType(int x, int y, List<HexType> possibleHexTypes) {
        int randomHexTypeId = (int) (rvg.randomValue() * possibleHexTypes.size());

        return possibleHexTypes.remove(randomHexTypeId);
    }

    //TODO: add rule to avoid placing 6 and 8 dice numbers close to each other
    public Integer pullRandomHexDiceNumber(int x, int y, List<Integer> possibleDiceNumbers) {
        int randomDiceNumberId = (int) (rvg.randomValue() * possibleDiceNumbers.size());

        return possibleDiceNumbers.remove(randomDiceNumberId);
    }

    public Integer getRandomDiceNumber() {
        return (int) (rvg.randomValue() * 6) + 1;
    }

    public Integer generateRandomOfferId(int limit) {
        return (int) (rvg.randomValue() * limit) + 1;
    }

    public DevelopmentCard pullRandomDevelopmentCard(List<DevelopmentCard> availableDevCards) {
        int randomAvailableDevCardIndex = (int) (rvg.randomValue() * availableDevCards.size());
        return availableDevCards.remove(randomAvailableDevCardIndex);
    }

    public HexType getRandomUsersResource(Resources userResources) {
        int brick = userResources.getBrick();
        int wood = userResources.getWood();
        int sheep = userResources.getSheep();
        int wheat = userResources.getWheat();
        int stone = userResources.getStone();
        int totalResourcesInSequence = brick + wood + sheep + wheat + stone;

        //TODO: it should never happen since we check the same condition above
        if (totalResourcesInSequence == 0) {
            return null;
        }

        int randomResourceNumber = (int) (rvg.randomValue() * totalResourcesInSequence);

        int sequenceIndex = brick;
        if (randomResourceNumber < sequenceIndex) {
            return HexType.BRICK;
        }

        sequenceIndex += wood;
        if (randomResourceNumber < sequenceIndex) {
            return HexType.WOOD;
        }

        sequenceIndex += sheep;
        if (randomResourceNumber < sequenceIndex) {
            return HexType.SHEEP;
        }

        sequenceIndex += wheat;
        if (randomResourceNumber < sequenceIndex) {
            return HexType.WHEAT;
        }

        return HexType.STONE;
    }

    public void setRvg(RandomValueGenerator rvg) {
        this.rvg = rvg;
    }
}
