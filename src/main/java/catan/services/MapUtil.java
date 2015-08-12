package catan.services;

import catan.domain.model.dashboard.CoordinatesBean;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static catan.domain.model.dashboard.types.EdgeOrientationType.LEFT_DOWN;
import static catan.domain.model.dashboard.types.EdgeOrientationType.RIGHT_DOWN;
import static catan.domain.model.dashboard.types.EdgeOrientationType.UP;
import static catan.domain.model.dashboard.types.NodeOrientationType.SINGLE_DOWN;
import static catan.domain.model.dashboard.types.NodeOrientationType.SINGLE_UP;

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
            createNodeAtPosition(hex, position);
        }

        //TODO: maybe make it in one (previous) cycle
        for (NodePosition position : NodePosition.values()) {
            createEdgeRightToNodePosition(hex, position);
        }

        game.getHexes().put(coordinates, hex);
    }

    protected void createNodeAtPosition(HexBean hex, NodePosition nodePosition) {
        //Check if left or right neighbour hex of this node already defined and stored current node
        NodeBean node = getCurrentNodeOfLeftNeighbourHex(hex.getGame(), nodePosition, hex.getCoordinates());
        if (node == null) {
            node = getCurrentNodeOfRightNeighbourHex(hex.getGame(), nodePosition, hex.getCoordinates());
        }

        //If neighbour hex is not defined yet, or doesn't exists in map, create a new node
        if (node == null) {
            node = new NodeBean(hex.getGame(), NodePortType.NONE); //TODO: create ports randomly
        }

        //Populate relationship between node and hex and set orientation of node
        switch (nodePosition) {
            case UP:
                node.setOrientation(SINGLE_DOWN);  //TODO: move orientation set to hex setter
                node.populateDownHex(hex);
                hex.setUpNode(node);
                break;
            case RIGHT_UP:
                node.setOrientation(SINGLE_UP);
                node.setLeftDownHex(hex);
                hex.setRightUpNode(node);
                break;
            case RIGHT_DOWN:
                node.setOrientation(SINGLE_DOWN);
                node.populateLeftUpHex(hex);
                hex.setRightDownNode(node);
                break;
            case DOWN:
                node.setOrientation(SINGLE_UP);
                node.setUpHex(hex);
                hex.setDownNode(node);
                break;
            case LEFT_DOWN:
                node.setOrientation(SINGLE_DOWN);
                node.populateRightUpHex(hex);
                hex.setLeftDownNode(node);
                break;
            case LEFT_UP:
                node.setOrientation(SINGLE_UP);
                node.setRightDownHex(hex);
                hex.setLeftUpNode(node);
                break;
        }
    }

    protected void createEdgeRightToNodePosition(HexBean innerHex, NodePosition nodePosition) {
        //Check if RIGHT neighbour hex of this node already defined and stored current edge
        EdgeBean edge = getCurrentEdgeOfOuterHex(innerHex.getGame(), nodePosition, innerHex.getCoordinates());

        //If RIGHT neighbour hex of node is not defined yet, or doesn't exists in map, create a new edge
        if (edge == null) {
            edge = new EdgeBean(innerHex.getGame());
        }

        //Populate relationship between edge and hex and set orientation of edge
        switch (nodePosition) {
            case UP:
                edge.setOrientation(RIGHT_DOWN);       //TODO: move orientation set to hex setter
                edge.setLeftNode(innerHex.getUpNode());
                edge.setRightNode(innerHex.getRightUpNode());
                edge.setDownHex(innerHex);
                innerHex.setRightUpEdge(edge);
                break;
            case RIGHT_UP:
                edge.setOrientation(UP);
                edge.populateUpNode(innerHex.getRightUpNode());
                edge.populateDownNode(innerHex.getRightDownNode());
                edge.populateLeftHex(innerHex);
                innerHex.setRightEdge(edge);
                break;
            case RIGHT_DOWN:
                edge.setOrientation(LEFT_DOWN);
                edge.setRightNode(innerHex.getRightDownNode());
                edge.setLeftNode(innerHex.getDownNode());
                edge.setUpHex(innerHex);
                innerHex.setRightDownEdge(edge);
                break;
            case DOWN:
                edge.setOrientation(RIGHT_DOWN);
                edge.setRightNode(innerHex.getDownNode());
                edge.setLeftNode(innerHex.getLeftDownNode());
                edge.setUpHex(innerHex);
                innerHex.setLeftDownEdge(edge);
                break;
            case LEFT_DOWN:
                edge.setOrientation(UP);
                edge.populateDownNode(innerHex.getLeftDownNode());
                edge.populateUpNode(innerHex.getLeftUpNode());
                edge.populateRightHex(innerHex);
                innerHex.setLeftEdge(edge);
                break;
            case LEFT_UP:
                edge.setOrientation(LEFT_DOWN);
                edge.setLeftNode(innerHex.getLeftUpNode());
                edge.setRightNode(innerHex.getUpNode());
                edge.setDownHex(innerHex);
                innerHex.setLeftUpEdge(edge);
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

    protected EdgeBean getCurrentEdgeOfOuterHex(GameBean game, NodePosition nodePosition, CoordinatesBean currentHexCoordinates) {
        int xCoordinate = currentHexCoordinates.getxCoordinate() + nodePosition.getRightNeighborHexXShift();
        int yCoordinate = currentHexCoordinates.getyCoordinate() + nodePosition.getRightNeighborHexYShift();

        CoordinatesBean outerHexCoordinates = new CoordinatesBean(xCoordinate, yCoordinate);
        HexBean outerHex = game.getHexes().get(outerHexCoordinates);
        if (outerHex == null) {
            return null;
        }

        switch (nodePosition) {
            case UP:
                return outerHex.getLeftDownEdge();
            case RIGHT_UP:
                return outerHex.getLeftEdge();
            case RIGHT_DOWN:
                return outerHex.getLeftUpEdge();
            case DOWN:
                return outerHex.getRightUpEdge();
            case LEFT_DOWN:
                return outerHex.getRightEdge();
            case LEFT_UP:
                return outerHex.getRightDownEdge();
            default:
                return null;
        }
    }

    @Autowired
    public void setRandomUtil(RandomUtil randomUtil) {
        this.randomUtil = randomUtil;
    }
}
