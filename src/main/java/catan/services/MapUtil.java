package catan.services;

import catan.domain.model.dashboard.CoordinatesBean;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapUtil {
    private RandomUtil randomUtil;

    public void generateNewRoundGameMap(GameBean game, int size) {
        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                if (x + y >= -size && x + y <= size) {
                    createHex(game, x, y);
                }
            }
        }
    }

    private void createHex(GameBean game, int x, int y) {
        CoordinatesBean coordinates = new CoordinatesBean(x, y);
        HexType randomHexType = randomUtil.generateRandomHexType();

        HexBean hex = new HexBean();
        hex.setGame(game);
        hex.setCoordinates(coordinates);
        hex.setResourceType(randomHexType);
        hex.setDice(x + y + 4);
        hex.setRobbed(false);

        for (NodePosition position : NodePosition.values()) {
            createNode(hex, position);
        }

        game.getHexes().put(coordinates, hex);
    }

    protected void createNode(HexBean hex, NodePosition nodePosition) {
        NodeBean node = getCurrentNodeOfLeftNeighbourHex(hex.getGame(), nodePosition, hex.getCoordinates());
        if (node == null) {
            node = getCurrentNodeOfRightNeighbourHex(hex.getGame(), nodePosition, hex.getCoordinates());
        }
        if (node == null) {
            node = new NodeBean(hex.getGame(), NodePortType.NONE); //TODO: create ports randomly
        }

        switch (nodePosition) {
            case UP:
                node.populateDownHex(hex);
                hex.setUpNode(node);
                break;
            case RIGHT_UP:
                node.setLeftDownHex(hex);
                hex.setRightUpNode(node);
                break;
            case RIGHT_DOWN:
                node.populateLeftUpHex(hex);
                hex.setRightDownNode(node);
                break;
            case DOWN:
                node.setUpHex(hex);
                hex.setDownNode(node);
                break;
            case LEFT_DOWN:
                node.populateRightUpHex(hex);
                hex.setLeftDownNode(node);
                break;
            case LEFT_UP:
                node.setRightDownHex(hex);
                hex.setLeftUpNode(node);
                break;
        }
    }

    protected NodeBean getCurrentNodeOfLeftNeighbourHex(GameBean game, NodePosition nodePosition, CoordinatesBean currentHexCoordinates) {
        int xCoordinate = currentHexCoordinates.getxCoordinate() + nodePosition.getLeftNeighborHexXShift();
        int yCoordinate = currentHexCoordinates.getyCoordinate() + nodePosition.getLeftNeighborHexYShift();

        CoordinatesBean leftNeighbourCoordinates = new CoordinatesBean(xCoordinate, yCoordinate);
        HexBean leftNeighbour = game.getHexes().get(leftNeighbourCoordinates);
        if (leftNeighbour == null) {
            return null;
        }

        switch (nodePosition) {
            case UP:
                return leftNeighbour.getRightDownNode();
            case RIGHT_UP:
                return leftNeighbour.getDownNode();
            case RIGHT_DOWN:
                return leftNeighbour.getLeftDownNode();
            case DOWN:
                return leftNeighbour.getLeftUpNode();
            case LEFT_DOWN:
                return leftNeighbour.getUpNode();
            case LEFT_UP:
                return leftNeighbour.getRightUpNode();
            default:
                return null;
        }
    }

    protected NodeBean getCurrentNodeOfRightNeighbourHex(GameBean game, NodePosition nodePosition, CoordinatesBean currentHexCoordinates) {
        int xCoordinate = currentHexCoordinates.getxCoordinate() + nodePosition.getRightNeighborHexXShift();
        int yCoordinate = currentHexCoordinates.getyCoordinate() + nodePosition.getRightNeighborHexYShift();

        CoordinatesBean rightNeighbourCoordinates = new CoordinatesBean(xCoordinate, yCoordinate);
        HexBean rightNeighbour = game.getHexes().get(rightNeighbourCoordinates);
        if (rightNeighbour == null) {
            return null;
        }

        switch (nodePosition) {
            case UP:
                return rightNeighbour.getLeftDownNode();
            case RIGHT_UP:
                return rightNeighbour.getLeftUpNode();
            case RIGHT_DOWN:
                return rightNeighbour.getUpNode();
            case DOWN:
                return rightNeighbour.getRightUpNode();
            case LEFT_DOWN:
                return rightNeighbour.getRightDownNode();
            case LEFT_UP:
                return rightNeighbour.getDownNode();
            default:
                return null;
        }
    }

    @Autowired
    public void setRandomUtil(RandomUtil randomUtil) {
        this.randomUtil = randomUtil;
    }
}
