package catan.services.util.map;

import catan.domain.model.dashboard.Coordinates;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;
import catan.services.util.random.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static catan.domain.model.dashboard.types.EdgeOrientationType.BOTTOM_LEFT;
import static catan.domain.model.dashboard.types.EdgeOrientationType.BOTTOM_RIGHT;
import static catan.domain.model.dashboard.types.EdgeOrientationType.VERTICAL;
import static catan.domain.model.dashboard.types.HexType.BRICK;
import static catan.domain.model.dashboard.types.HexType.SHEEP;
import static catan.domain.model.dashboard.types.HexType.STONE;
import static catan.domain.model.dashboard.types.HexType.WHEAT;
import static catan.domain.model.dashboard.types.HexType.WOOD;
import static catan.domain.model.dashboard.types.NodeOrientationType.SINGLE_BOTTOM;
import static catan.domain.model.dashboard.types.NodeOrientationType.SINGLE_TOP;
import static java.util.Arrays.asList;

@Component
public class MapUtil {
    private RandomUtil randomUtil;
    private int hexAbsoluteIdSequence;
    private int nodeAbsoluteIdSequence;
    private int edgeAbsoluteIdSequence;

    public void generateNewRoundGameMap(GameBean game, int size) {
        Map<Coordinates, HexBean> tempCoordinatesToHexMap = new HashMap<Coordinates, HexBean>();
        List<Integer> possibleDiceNumbers = new ArrayList<Integer>(asList(
                2,
                3, 3,
                4, 4,
                5, 5,
                6, 6,
                8, 8,
                9, 9,
                10, 10,
                11, 11,
                12));
        List<HexType> possibleHexTypes = new ArrayList<HexType>(asList(
                WOOD, WOOD, WOOD, WOOD,
                SHEEP, SHEEP, SHEEP, SHEEP,
                WHEAT, WHEAT, WHEAT, WHEAT,
                BRICK, BRICK, BRICK,
                STONE, STONE, STONE));

        hexAbsoluteIdSequence = 1;
        nodeAbsoluteIdSequence = 1;
        edgeAbsoluteIdSequence = 1;

        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                if (x + y < -size || x + y > size) {
                    continue;
                }

                NodePortType hexPort = null;
                EdgePosition portLocation = null;

                if (x == 0 && y == -size) {
                    portLocation = EdgePosition.TOP_LEFT;
                    hexPort = NodePortType.ANY;
                } else if (x == 1 && y == -size) {
                    portLocation = EdgePosition.TOP_RIGHT;
                    hexPort = NodePortType.SHEEP;
                } else if (x == size && y == -1) {
                    portLocation = EdgePosition.TOP_RIGHT;
                    hexPort = NodePortType.ANY;
                } else if (x == size && y == 0) {
                    portLocation = EdgePosition.RIGHT;
                    hexPort = NodePortType.ANY;
                } else if (x == 1 && y == size - 1) {
                    portLocation = EdgePosition.BOTTOM_RIGHT;
                    hexPort = NodePortType.BRICK;
                } else if (x == -1 && y == size) {
                    portLocation = EdgePosition.BOTTOM_RIGHT;
                    hexPort = NodePortType.WOOD;
                } else if (x == -size && y == size) {
                    portLocation = EdgePosition.BOTTOM_LEFT;
                    hexPort = NodePortType.ANY;
                } else if (x == -size && y == 1) {
                    portLocation = EdgePosition.LEFT;
                    hexPort = NodePortType.WHEAT;
                } else if (x == -size + 1 && y == -1) {
                    portLocation = EdgePosition.LEFT;
                    hexPort = NodePortType.STONE;
                }

                boolean robbed = (x == 0 && y == 0);
                Integer diceNumber = (x == 0 && y == 0)
                        ? null
                        : randomUtil.pullRandomHexDiceNumber(x, y, possibleDiceNumbers);
                HexType hexType = (x == 0 && y == 0)
                        ? HexType.EMPTY
                        : randomUtil.pullRandomHexType(x, y, possibleHexTypes);

                createHex(tempCoordinatesToHexMap, game, x, y, hexPort, portLocation, hexType, diceNumber, robbed);
            }
        }
    }

    private void createHex(Map<Coordinates, HexBean> tempCoordinatesToHexMap,
                           GameBean game,
                           int x,
                           int y,
                           NodePortType hexPort,
                           EdgePosition portLocation,
                           HexType hexType,
                           Integer diceNumber,
                           boolean robbed) {
        Coordinates coordinates = new Coordinates(x, y);
        HexBean hex = new HexBean(hexAbsoluteIdSequence++, game, coordinates, hexType, diceNumber, robbed);

        for (NodePosition position : NodePosition.values()) {
            createNodeAtPosition(tempCoordinatesToHexMap, hex, position);
        }

        for (EdgePosition edgePosition : EdgePosition.values()) {
            NodePortType nodePort = edgePosition == portLocation ? hexPort : NodePortType.NONE;
            createEdgeAtPosition(tempCoordinatesToHexMap, hex, edgePosition, nodePort);
        }

        game.getHexes().add(hex);
        tempCoordinatesToHexMap.put(coordinates, hex);
    }

    protected void createNodeAtPosition(Map<Coordinates, HexBean> tempCoordinatesToHexMap,
                                        HexBean hex,
                                        NodePosition nodePosition) {
        //Check if left or right neighbour hex of this node already defined and stored current node
        NodeBean node = getCurrentNodeOfLeftNeighbourHex(tempCoordinatesToHexMap, nodePosition, hex.getCoordinates());
        if (node == null) {
            node = getCurrentNodeOfRightNeighbourHex(tempCoordinatesToHexMap, nodePosition, hex.getCoordinates());
        }

        //If neighbour hex is not defined yet, or doesn't exists in map, create a new node
        if (node == null) {
            node = new NodeBean(nodeAbsoluteIdSequence++, hex.getGame(), NodePortType.NONE);
        }

        //Populate relationship between node and hex and set orientation of node
        switch (nodePosition) {
            case TOP:
                node.setOrientation(SINGLE_BOTTOM);
                node.getHexes().setBottom(hex);
                hex.getNodes().setTop(node);
                break;
            case TOP_RIGHT:
                node.setOrientation(SINGLE_TOP);
                node.getHexes().setBottomLeft(hex);
                hex.getNodes().setTopRight(node);
                break;
            case BOTTOM_RIGHT:
                node.setOrientation(SINGLE_BOTTOM);
                node.getHexes().setTopLeft(hex);
                hex.getNodes().setBottomRight(node);
                break;
            case BOTTOM:
                node.setOrientation(SINGLE_TOP);
                node.getHexes().setTop(hex);
                hex.getNodes().setBottom(node);
                break;
            case BOTTOM_LEFT:
                node.setOrientation(SINGLE_BOTTOM);
                node.getHexes().setTopRight(hex);
                hex.getNodes().setBottomLeft(node);
                break;
            case TOP_LEFT:
                node.setOrientation(SINGLE_TOP);
                node.getHexes().setBottomRight(hex);
                hex.getNodes().setTopLeft(node);
                break;
        }

        hex.getGame().getNodes().add(node);
    }

    protected void createEdgeAtPosition(Map<Coordinates, HexBean> tempCoordinatesToHexMap,
                                        HexBean innerHex,
                                        EdgePosition edgePosition,
                                        NodePortType nodePort) {
        //Check if RIGHT neighbour hex of this node already defined and stored current edge
        EdgeBean edge = getCurrentEdgeOfOuterHex(tempCoordinatesToHexMap, edgePosition, innerHex.getCoordinates());

        //If RIGHT neighbour hex of node is not defined yet, or doesn't exists in map, create a new edge
        if (edge == null) {
            edge = new EdgeBean(edgeAbsoluteIdSequence++, innerHex.getGame());
        }

        //Populate relationship between edge, node and hex and set orientation of edge and port to appropriate node
        switch (edgePosition) {
            case TOP_LEFT:
                edge.setOrientation(BOTTOM_LEFT);

                edge.getNodes().setBottomLeft(innerHex.getNodes().getTopLeft());
                edge.getNodes().setTopRight(innerHex.getNodes().getTop());

                edge.getHexes().setBottomRight(innerHex);

                innerHex.getNodes().getTopLeft().getEdges().setTopRight(edge);
                innerHex.getNodes().getTop().getEdges().setBottomLeft(edge);

                if(nodePort != NodePortType.NONE){
                    innerHex.getNodes().getTopLeft().setPort(nodePort);
                    innerHex.getNodes().getTop().setPort(nodePort);
                }

                innerHex.getEdges().setTopLeft(edge);
                break;
            case TOP_RIGHT:
                edge.setOrientation(BOTTOM_RIGHT);

                edge.getNodes().setTopLeft(innerHex.getNodes().getTop());
                edge.getNodes().setBottomRight(innerHex.getNodes().getTopRight());

                edge.getHexes().setBottomLeft(innerHex);

                innerHex.getNodes().getTop().getEdges().setBottomRight(edge);
                innerHex.getNodes().getTopRight().getEdges().setTopLeft(edge);

                if(nodePort != NodePortType.NONE){
                    innerHex.getNodes().getTop().setPort(nodePort);
                    innerHex.getNodes().getTopRight().setPort(nodePort);
                }

                innerHex.getEdges().setTopRight(edge);
                break;
            case RIGHT:
                edge.setOrientation(VERTICAL);

                edge.getNodes().setTop(innerHex.getNodes().getTopRight());
                edge.getNodes().setBottom(innerHex.getNodes().getBottomRight());

                edge.getHexes().setLeft(innerHex);

                innerHex.getNodes().getTopRight().getEdges().setBottom(edge);
                innerHex.getNodes().getBottomRight().getEdges().setTop(edge);

                if(nodePort != NodePortType.NONE){
                    innerHex.getNodes().getTopRight().setPort(nodePort);
                    innerHex.getNodes().getBottomRight().setPort(nodePort);
                }

                innerHex.getEdges().setRight(edge);
                break;
            case BOTTOM_RIGHT:
                edge.setOrientation(BOTTOM_LEFT);

                edge.getNodes().setTopRight(innerHex.getNodes().getBottomRight());
                edge.getNodes().setBottomLeft(innerHex.getNodes().getBottom());

                edge.getHexes().setTopLeft(innerHex);

                innerHex.getNodes().getBottomRight().getEdges().setBottomLeft(edge);
                innerHex.getNodes().getBottom().getEdges().setTopRight(edge);

                if(nodePort != NodePortType.NONE){
                    innerHex.getNodes().getBottomRight().setPort(nodePort);
                    innerHex.getNodes().getBottom().setPort(nodePort);
                }

                innerHex.getEdges().setBottomRight(edge);
                break;
            case BOTTOM_LEFT:
                edge.setOrientation(BOTTOM_RIGHT);

                edge.getNodes().setBottomRight(innerHex.getNodes().getBottom());
                edge.getNodes().setTopLeft(innerHex.getNodes().getBottomLeft());

                edge.getHexes().setTopRight(innerHex);

                innerHex.getNodes().getBottom().getEdges().setTopLeft(edge);
                innerHex.getNodes().getBottomLeft().getEdges().setBottomRight(edge);

                if(nodePort != NodePortType.NONE){
                    innerHex.getNodes().getBottom().setPort(nodePort);
                    innerHex.getNodes().getBottomLeft().setPort(nodePort);
                }

                innerHex.getEdges().setBottomLeft(edge);
                break;
            case LEFT:
                edge.setOrientation(VERTICAL);

                edge.getNodes().setBottom(innerHex.getNodes().getBottomLeft());
                edge.getNodes().setTop(innerHex.getNodes().getTopLeft());

                edge.getHexes().setRight(innerHex);

                innerHex.getNodes().getBottomLeft().getEdges().setTop(edge);
                innerHex.getNodes().getTopLeft().getEdges().setBottom(edge);

                if(nodePort != NodePortType.NONE){
                    innerHex.getNodes().getBottomLeft().setPort(nodePort);
                    innerHex.getNodes().getTopLeft().setPort(nodePort);
                }

                innerHex.getEdges().setLeft(edge);
                break;
        }

        innerHex.getGame().getEdges().add(edge);
    }

    // Counter Clockwise Neighbour
    protected NodeBean getCurrentNodeOfLeftNeighbourHex(Map<Coordinates, HexBean> tempCoordinatesToHexMap,
                                                        NodePosition nodePosition,
                                                        Coordinates currentHexCoordinates) {
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
    protected NodeBean getCurrentNodeOfRightNeighbourHex(Map<Coordinates, HexBean> tempCoordinatesToHexMap,
                                                         NodePosition nodePosition,
                                                         Coordinates currentHexCoordinates) {
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

    protected EdgeBean getCurrentEdgeOfOuterHex(Map<Coordinates, HexBean> tempCoordinatesToHexMap,
                                                EdgePosition edgePosition,
                                                Coordinates currentHexCoordinates) {
        int xCoordinate = currentHexCoordinates.getxCoordinate() + edgePosition.getOuterNeighborHexXShift();
        int yCoordinate = currentHexCoordinates.getyCoordinate() + edgePosition.getOuterNeighborHexYShift();

        Coordinates outerHexCoordinates = new Coordinates(xCoordinate, yCoordinate);
        HexBean outerHex = tempCoordinatesToHexMap.get(outerHexCoordinates);
        if (outerHex == null) {
            return null;
        }

        switch (edgePosition) {
            case TOP_LEFT:
                return outerHex.getEdges().getBottomRight();
            case TOP_RIGHT:
                return outerHex.getEdges().getBottomLeft();
            case RIGHT:
                return outerHex.getEdges().getLeft();
            case BOTTOM_RIGHT:
                return outerHex.getEdges().getTopLeft();
            case BOTTOM_LEFT:
                return outerHex.getEdges().getTopRight();
            case LEFT:
                return outerHex.getEdges().getRight();
            default:
                return null;
        }
    }

    @Autowired
    public void setRandomUtil(RandomUtil randomUtil) {
        this.randomUtil = randomUtil;
    }
}
