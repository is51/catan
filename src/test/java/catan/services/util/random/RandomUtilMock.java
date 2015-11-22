package catan.services.util.random;

import catan.domain.model.dashboard.Coordinates;
import catan.domain.model.dashboard.types.HexType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RandomUtilMock extends RandomUtil {

    private List<String> privateCodesToGenerate = new LinkedList<String>();
    private List<Integer> moveOrdersToGenerate = new LinkedList<Integer>();
    private List<Integer> diceNumbersToGenerate = new LinkedList<Integer>();
    private Map<Coordinates, HexType> hexTypesToGenerate = new HashMap<Coordinates, HexType>();
    private Map<Coordinates, Integer> hexDiceNumbersToGenerate = new HashMap<Coordinates, Integer>();

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

    public String generateRandomPrivateCode(int numberOfDigits) {
        return privateCodesToGenerate.size() > 0 ? privateCodesToGenerate.remove(0) : super.generateRandomPrivateCode(numberOfDigits);
    }

    public Integer pullRandomMoveOrder(List<Integer> moveOrderSequence) {
        return moveOrdersToGenerate.size() > 0 ? moveOrdersToGenerate.remove(0) : super.pullRandomMoveOrder(moveOrderSequence);
    }

    public HexType pullRandomHexType(int x, int y, List<HexType> possibleHexTypes) {
        Coordinates coordinatesKey = new Coordinates(x, y);
        return hexTypesToGenerate.get(coordinatesKey) != null
                ? hexTypesToGenerate.remove(coordinatesKey)
                : super.pullRandomHexType(x, y, possibleHexTypes);
    }

    public Integer pullRandomHexDiceNumber(int x, int y, List<Integer> possibleDiceNumbers) {
        Coordinates coordinatesKey = new Coordinates(x, y);
        return hexDiceNumbersToGenerate.get(coordinatesKey) != null
                ? hexDiceNumbersToGenerate.remove(coordinatesKey)
                : super.pullRandomHexDiceNumber(x, y, possibleDiceNumbers);
    }

    public Integer getRandomDiceNumber() {
        return diceNumbersToGenerate.size() > 0 ? diceNumbersToGenerate.remove(0) : super.getRandomDiceNumber();
    }

}