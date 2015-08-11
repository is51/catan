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

    public void generateNewGameMap(GameBean game) {
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                if (x + y >= -2 && x + y <= 2) {
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
            createNode(game, hex, position);
        }

        game.getHexes().put(coordinates, hex);
    }

    private void createNode(GameBean game, HexBean hex, NodePosition nodePosition) {
        NodeBean node = getCurrentNodeOfLeftNeighbourHex(game, nodePosition);
        if (node == null) {
            node = getCurrentNodeOfRightNeighbourHex(game, nodePosition);
        }
        if (node == null) {
            node = new NodeBean(game, NodePortType.NONE);
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

    private NodeBean getCurrentNodeOfLeftNeighbourHex(GameBean game, NodePosition nodePosition) {
        CoordinatesBean coordinates = new CoordinatesBean(nodePosition.getLeftHexX(), nodePosition.getLeftHexY());
        HexBean leftNeighbour = game.getHexes().get(coordinates);
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

    public NodeBean getCurrentNodeOfRightNeighbourHex(GameBean game, NodePosition nodePosition) {
        CoordinatesBean coordinates = new CoordinatesBean(nodePosition.getRightHexX(), nodePosition.getRightHexY());
        HexBean rightNeighbour = game.getHexes().get(coordinates);
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
