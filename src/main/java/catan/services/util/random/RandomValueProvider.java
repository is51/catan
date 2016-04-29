package catan.services.util.random;

import catan.domain.model.dashboard.Coordinates;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.Resources;
import catan.domain.model.game.types.DevelopmentCard;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class RandomValueProvider extends RandomUtil {

    private List<String> privateCodesToGenerate = new LinkedList<String>();
    private List<Integer> moveOrdersToGenerate = new LinkedList<Integer>();
    private List<Integer> diceNumbersToGenerate = new LinkedList<Integer>();
    private Map<Coordinates, HexType> hexTypesToGenerate = new HashMap<Coordinates, HexType>();
    private Map<Coordinates, Integer> hexDiceNumbersToGenerate = new HashMap<Coordinates, Integer>();
    private List<DevelopmentCard> developmentCardsToGenerate = new LinkedList<DevelopmentCard>();
    private List<HexType> stolenResourcesToGenerate = new LinkedList<HexType>();
    private List<Integer> offerIdsToGenerate = new LinkedList<Integer>();

    public void resetMock(){
        privateCodesToGenerate = new LinkedList<String>();
        moveOrdersToGenerate = new LinkedList<Integer>();
        diceNumbersToGenerate = new LinkedList<Integer>();
        hexTypesToGenerate = new HashMap<Coordinates, HexType>();
        hexDiceNumbersToGenerate = new HashMap<Coordinates, Integer>();
        developmentCardsToGenerate = new LinkedList<DevelopmentCard>();
        stolenResourcesToGenerate = new LinkedList<HexType>();
    }

    public void setNextPrivateCode(String privateCode){
        privateCodesToGenerate.add(privateCode);
    }

    public void setNextMoveOrder(Integer moveOrder) {
        moveOrdersToGenerate.add(moveOrder);
    }

    public void setNextHexType(Coordinates coordinates, HexType hexType) {
        hexTypesToGenerate.put(coordinates, hexType);
    }

    public void setNextHexDiceNumber(Coordinates coordinates, Integer diceNumber) {
        hexDiceNumbersToGenerate.put(coordinates, diceNumber);
    }

    public void setNextDiceNumber(Integer diceNumber) {
        diceNumbersToGenerate.add(diceNumber);
    }

    public void setNextDevelopmentCard(DevelopmentCard devCard) {
        developmentCardsToGenerate.add(devCard);
    }

    public void setNextStolenResource(HexType resource) {
        stolenResourcesToGenerate.add(resource);
    }

    public void setNextOfferId(int offerId) {
        offerIdsToGenerate.add(offerId);
    }

    @Override
    public String generateRandomPrivateCode(int numberOfDigits) {
        return privateCodesToGenerate.size() > 0 ? privateCodesToGenerate.remove(0) : super.generateRandomPrivateCode(numberOfDigits);
    }

    @Override
    public Integer pullRandomMoveOrder(List<Integer> moveOrderSequence) {
        return moveOrdersToGenerate.size() > 0 ? moveOrdersToGenerate.remove(0) : super.pullRandomMoveOrder(moveOrderSequence);
    }

    @Override
    public HexType pullRandomHexType(int x, int y, List<HexType> possibleHexTypes) {
        Coordinates coordinatesKey = new Coordinates(x, y);
        return hexTypesToGenerate.get(coordinatesKey) != null
                ? hexTypesToGenerate.remove(coordinatesKey)
                : super.pullRandomHexType(x, y, possibleHexTypes);
    }

    @Override
    public Integer pullRandomHexDiceNumber(int x, int y, List<Integer> possibleDiceNumbers) {
        Coordinates coordinatesKey = new Coordinates(x, y);
        return hexDiceNumbersToGenerate.get(coordinatesKey) != null
                ? hexDiceNumbersToGenerate.remove(coordinatesKey)
                : super.pullRandomHexDiceNumber(x, y, possibleDiceNumbers);
    }

    @Override
    public Integer getRandomDiceNumber() {
        return diceNumbersToGenerate.size() > 0 ? diceNumbersToGenerate.remove(0) : super.getRandomDiceNumber();
    }

    @Override
    public DevelopmentCard pullRandomDevelopmentCard(List<DevelopmentCard> availableDevCards) {
        return developmentCardsToGenerate.size() > 0 ? developmentCardsToGenerate.remove(0) : super.pullRandomDevelopmentCard(availableDevCards);

    }

    @Override
    public HexType getRandomUsersResource(Resources userResources) {
        return stolenResourcesToGenerate.size() > 0 ? stolenResourcesToGenerate.remove(0) : super.getRandomUsersResource(userResources);
    }

    @Override
    public Integer generateRandomOfferId(int limit) {
        return offerIdsToGenerate.size() > 0 ? offerIdsToGenerate.remove(0) : super.generateRandomOfferId(limit);
    }
}