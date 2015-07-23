package catan.domain.model.dashboard;

import catan.domain.model.game.GameBean;

public class HexBean {
    private int id;
    private GameBean game;
    private int xCoordinate;
    private int yCoordinate;
    private HexType resourseType;
    private int dice;
    private boolean robbed;

    private NodeBean upNode;
    private NodeBean rightUpNode;
    private NodeBean rightDownNode;
    private NodeBean downNode;
    private NodeBean leftDownNode;
    private NodeBean leftUpNode;

    private EdgeBean rightUpEdge;
    private EdgeBean rightEdge;
    private EdgeBean rightDownEdge;
    private EdgeBean leftdownEdge;
    private EdgeBean leftEdge;
    private EdgeBean leftUpEdge;

}
