package catan.services;

import catan.domain.model.dashboard.Coordinates;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static catan.domain.model.dashboard.types.EdgeOrientationType.BOTTOM_LEFT;
import static catan.domain.model.dashboard.types.EdgeOrientationType.BOTTOM_RIGHT;
import static catan.domain.model.dashboard.types.EdgeOrientationType.VERTICAL;
import static catan.domain.model.dashboard.types.NodeOrientationType.SINGLE_DOWN;
import static catan.domain.model.dashboard.types.NodeOrientationType.SINGLE_UP;

@Component
public class MapUtil {
    private RandomUtil randomUtil;

    public void generateNewRoundGameMap(GameBean game, int size) {
        Map<Coordinates, HexBean> tempCoordinatesToHexMap = new HashMap<Coordinates, HexBean>();

        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                if (x + y >= -size && x + y <= size) {
                    continue;
                }

                NodePortType nodePort = NodePortType.NONE;
                if(x + y == -size || x + y == size){
                    nodePort = randomUtil.generateRandomNodePortType();
                }

                createHex(tempCoordinatesToHexMap, game, x, y, nodePort);
            }
        }
    }

    private void createHex(Map<Coordinates, HexBean> tempCoordinatesToHexMap, GameBean game, int x, int y, NodePortType nodePort) {
        Coordinates coordinates = new Coordinates(x, y);
        HexType randomHexType = randomUtil.generateRandomHexType();

        HexBean hex = new HexBean(game, coordinates, randomHexType, x + y + 4, false);

        for (NodePosition position : NodePosition.values()) {
            //TODO: put port only at one position, depending on border and other ports
            createNodeAtPosition(tempCoordinatesToHexMap, hex, position, nodePort);
        }

        //TODO: maybe make it in one (previous) cycle
        for (NodePosition position : NodePosition.values()) {
            createEdgeRightToNodePosition(tempCoordinatesToHexMap, hex, position);
        }

        game.getHexes().add(hex);
        tempCoordinatesToHexMap.put(coordinates, hex);
    }

    protected void createNodeAtPosition(Map<Coordinates, HexBean> tempCoordinatesToHexMap, HexBean hex, NodePosition nodePosition, NodePortType port) {
        //Check if left or right neighbour hex of this node already defined and stored current node
        NodeBean node = getCurrentNodeOfLeftNeighbourHex(tempCoordinatesToHexMap, nodePosition, hex.getCoordinates());
        if (node == null) {
            node = getCurrentNodeOfClockwiseNeighbourHex(tempCoordinatesToHexMap, nodePosition, hex.getCoordinates());
        }

        //If neighbour hex is not defined yet, or doesn't exists in map, create a new node
        if (node == null) {
            node = new NodeBean(hex.getGame(), port);
        }

        //Populate relationship between node and hex and set orientation of node
        switch (nodePosition) {
            case TOP:
                node.setOrientation(SINGLE_DOWN);  //TODO: move orientation set to hex setter
                node.getHexes().setBottom(hex);
                hex.getNodes().setTop(node);
                break;
            case TOP_RIGHT:
                node.setOrientation(SINGLE_UP);
                node.getHexes().setBottomLeft(hex);
                hex.getNodes().setTopRight(node);
                break;
            case BOTTOM_RIGHT:
                node.setOrientation(SINGLE_DOWN);
                node.getHexes().setTopLeft(hex);
                hex.getNodes().setBottomRight(node);
                break;
            case BOTTOM:
                node.setOrientation(SINGLE_UP);
                node.getHexes().setTop(hex);
                hex.getNodes().setBottom(node);
                break;
            case BOTTOM_LEFT:
                node.setOrientation(SINGLE_DOWN);
                node.getHexes().setTopRight(hex);
                hex.getNodes().setBottomLeft(node);
                break;
            case TOP_LEFT:
                node.setOrientation(SINGLE_UP);
                node.getHexes().setBottomRight(hex);
                hex.getNodes().setTopLeft(node);
                break;
        }
    }

    protected void createEdgeRightToNodePosition(Map<Coordinates, HexBean> tempCoordinatesToHexMap, HexBean innerHex, NodePosition nodePosition) {
        //Check if RIGHT neighbour hex of this node already defined and stored current edge
        EdgeBean edge = getCurrentEdgeOfOuterHex(tempCoordinatesToHexMap, nodePosition, innerHex.getCoordinates());

        //If RIGHT neighbour hex of node is not defined yet, or doesn't exists in map, create a new edge
        if (edge == null) {
            edge = new EdgeBean(innerHex.getGame());
        }

        //Populate relationship between edge and hex and set orientation of edge
        switch (nodePosition) {
            case TOP:
                edge.setOrientation(BOTTOM_RIGHT);

                edge.getNodes().setTopLeft(innerHex.getNodes().getTop());
                edge.getNodes().setBottomRight(innerHex.getNodes().getTopRight());

                edge.getHexes().setBottomLeft(innerHex);

                innerHex.getNodes().getTop().getEdges().setBottomRight(edge);
                innerHex.getEdges().setTopRight(edge);
                break;
            case TOP_RIGHT:
                edge.setOrientation(VERTICAL);

                edge.getNodes().setTop(innerHex.getNodes().getTopRight());
                edge.getNodes().setBottom(innerHex.getNodes().getBottomRight());

                edge.getHexes().setLeft(innerHex);

                innerHex.getNodes().getTopRight().getEdges().setBottom(edge);
                innerHex.getEdges().setRight(edge);
                break;
            case BOTTOM_RIGHT:
                edge.setOrientation(BOTTOM_LEFT);

                edge.getNodes().setTopRight(innerHex.getNodes().getBottomRight());
                edge.getNodes().setBottomLeft(innerHex.getNodes().getBottom());

                edge.getHexes().setTopLeft(innerHex);

                innerHex.getNodes().getBottomRight().getEdges().setBottomLeft(edge);
                innerHex.getEdges().setBottomRight(edge);
                break;
            case BOTTOM:
                edge.setOrientation(BOTTOM_RIGHT);

                edge.getNodes().setBottomRight(innerHex.getNodes().getBottom());
                edge.getNodes().setTopLeft(innerHex.getNodes().getBottomLeft());

                edge.getHexes().setTopRight(innerHex);

                innerHex.getNodes().getBottom().getEdges().setTopLeft(edge);
                innerHex.getEdges().setBottomLeft(edge);
                break;
            case BOTTOM_LEFT:
                edge.setOrientation(VERTICAL);

                edge.getNodes().setBottom(innerHex.getNodes().getBottomLeft());
                edge.getNodes().setTop(innerHex.getNodes().getTopLeft());

                edge.getHexes().setRight(innerHex);

                innerHex.getNodes().getBottomLeft().getEdges().setTop(edge);
                innerHex.getEdges().setLeft(edge);
                break;
            case TOP_LEFT:
                edge.setOrientation(BOTTOM_LEFT);

                edge.getNodes().setBottomLeft(innerHex.getNodes().getTopLeft());
                edge.getNodes().setTopRight(innerHex.getNodes().getTop());

                edge.getHexes().setBottomRight(innerHex);

                innerHex.getNodes().getTopLeft().getEdges().setTopRight(edge);
                innerHex.getEdges().setTopLeft(edge);
                break;
        }

    }

    // Counter Clockwise Neighbour
    protected NodeBean getCurrentNodeOfLeftNeighbourHex(Map<Coordinates, HexBean> tempCoordinatesToHexMap, NodePosition nodePosition, Coordinates currentHexCoordinates) {
        int xCoordinate = currentHexCoordinates.getxCoordinate() + nodePosition.getLeftNeighborHexXShift();
        int yCoordinate = currentHexCoordinates.getyCoordinate() + nodePosition.getLeftNeighborHexYShift();

        Coordinates leftNeighbourCoordinates = new Coordinates(xCoordinate, yCoordinate);
        HexBean leftNeighbour = tempCoordinatesToHexMap.get(leftNeighbourCoordinates);
        if (leftNeighbour == null) {
            return null;
        }

        switch (nodePosition) {
            case TOP:
                return leftNeighbour.getNodes().getBottomRight();
            case TOP_RIGHT:
                return leftNeighbour.getNodes().getBottom();
            case BOTTOM_RIGHT:
                return leftNeighbour.getNodes().getBottomLeft();
            case BOTTOM:
                return leftNeighbour.getNodes().getTopLeft();
            case BOTTOM_LEFT:
                return leftNeighbour.getNodes().getTop();
            case TOP_LEFT:
                return leftNeighbour.getNodes().getTopRight();
            default:
                return null;
        }
    }

    // Clockwise Neighbour
    protected NodeBean getCurrentNodeOfClockwiseNeighbourHex(Map<Coordinates, HexBean> tempCoordinatesToHexMap, NodePosition nodePosition, Coordinates currentHexCoordinates) {
        int xCoordinate = currentHexCoordinates.getxCoordinate() + nodePosition.getRightNeighborHexXShift();
        int yCoordinate = currentHexCoordinates.getyCoordinate() + nodePosition.getRightNeighborHexYShift();

        Coordinates rightNeighbourCoordinates = new Coordinates(xCoordinate, yCoordinate);
        HexBean rightNeighbour = tempCoordinatesToHexMap.get(rightNeighbourCoordinates);
        if (rightNeighbour == null) {
            return null;
        }

        switch (nodePosition) {
            case TOP:
                return rightNeighbour.getNodes().getBottomLeft();
            case TOP_RIGHT:
                return rightNeighbour.getNodes().getTopLeft();
            case BOTTOM_RIGHT:
                return rightNeighbour.getNodes().getTop();
            case BOTTOM:
                return rightNeighbour.getNodes().getTopRight();
            case BOTTOM_LEFT:
                return rightNeighbour.getNodes().getBottomRight();
            case TOP_LEFT:
                return rightNeighbour.getNodes().getBottom();
            default:
                return null;
        }
    }

    protected EdgeBean getCurrentEdgeOfOuterHex(Map<Coordinates, HexBean> tempCoordinatesToHexMap, NodePosition nodePosition, Coordinates currentHexCoordinates) {
        int xCoordinate = currentHexCoordinates.getxCoordinate() + nodePosition.getRightNeighborHexXShift();
        int yCoordinate = currentHexCoordinates.getyCoordinate() + nodePosition.getRightNeighborHexYShift();

        Coordinates outerHexCoordinates = new Coordinates(xCoordinate, yCoordinate);
        HexBean outerHex = tempCoordinatesToHexMap.get(outerHexCoordinates);
        if (outerHex == null) {
            return null;
        }
        //TODO: finish at home
        /*
        switch (nodePosition) {
            case TOP:
                return outerHex.getEdges().getLeftDownEdge();
            case TOP_RIGHT:
                return outerHex.getEdges().getLeftEdge();
            case BOTTOM_RIGHT:
                return outerHex.getEdges().getLeftUpEdge();
            case BOTTOM:
                return outerHex.getEdges().getRightUpEdge();
            case BOTTOM_LEFT:
                return outerHex.getEdges().getRightEdge();
            case TOP_LEFT:
                return outerHex.getEdges().getRightDownEdge();
            default:
                return null;
        }
        */
        return null;
    }

    @Autowired
    public void setRandomUtil(RandomUtil randomUtil) {
        this.randomUtil = randomUtil;
    }
}
