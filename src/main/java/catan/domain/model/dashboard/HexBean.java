package catan.domain.model.dashboard;

import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.GameBean;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "HEX")
public class HexBean {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "HEXES_KEY", unique = true, nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GAME_ID", nullable = false)
    private GameBean game;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "COORDINATES_ID")
    private CoordinatesBean coordinates;

    @Column(name = "RESOURCE_TYPE", unique = false, nullable = false)
    private HexType resourceType;

    @Column(name = "DICE", unique = false, nullable = true)
    private int dice;

    @Column(name = "IS_ROBBED", unique = false, nullable = false)
    private boolean robbed;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "UP_NODE_ID")
    private NodeBean upNode;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "RIGHT_UP_NODE_ID")
    private NodeBean rightUpNode;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "RIGHT_DOWN_NODE_ID")
    private NodeBean rightDownNode;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "DOWN_NODE_ID")
    private NodeBean downNode;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "LEFT_DOWN_NODE_ID")
    private NodeBean leftDownNode;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "LEFT_UP_NODE_ID")
    private NodeBean leftUpNode;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "RIGHT_UP_EDGE_ID")
    private EdgeBean rightUpEdge;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "RIGHT_EDGE_ID")
    private EdgeBean rightEdge;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "RIGHT_DOWN_EDGE_ID")
    private EdgeBean rightDownEdge;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "LEFT_DOWN_EDGE_ID")
    private EdgeBean leftDownEdge;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "LEFT_EDGE_ID")
    private EdgeBean leftEdge;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "LEFT_UP_EDGE_ID")
    private EdgeBean leftUpEdge;

    public HexBean() {
    }

    public HexBean(GameBean game, CoordinatesBean coordinates, HexType resourceType, int dice, boolean robbed, NodeBean upNode, NodeBean rightUpNode, NodeBean rightDownNode, NodeBean downNode, NodeBean leftDownNode, NodeBean leftUpNode, EdgeBean rightUpEdge, EdgeBean rightEdge, EdgeBean rightDownEdge, EdgeBean leftDownEdge, EdgeBean leftEdge, EdgeBean leftUpEdge) {
        this.game = game;
        this.coordinates = coordinates;
        this.resourceType = resourceType;
        this.dice = dice;
        this.robbed = robbed;
        this.upNode = upNode;
        this.rightUpNode = rightUpNode;
        this.rightDownNode = rightDownNode;
        this.downNode = downNode;
        this.leftDownNode = leftDownNode;
        this.leftUpNode = leftUpNode;
        this.rightUpEdge = rightUpEdge;
        this.rightEdge = rightEdge;
        this.rightDownEdge = rightDownEdge;
        this.leftDownEdge = leftDownEdge;
        this.leftEdge = leftEdge;
        this.leftUpEdge = leftUpEdge;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GameBean getGame() {
        return game;
    }

    public void setGame(GameBean game) {
        this.game = game;
    }

    public CoordinatesBean getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesBean coordinates) {
        this.coordinates = coordinates;
    }

    public HexType getResourceType() {
        return resourceType;
    }

    public void setResourceType(HexType resourceType) {
        this.resourceType = resourceType;
    }

    public int getDice() {
        return dice;
    }

    public void setDice(int dice) {
        this.dice = dice;
    }

    public boolean isRobbed() {
        return robbed;
    }

    public void setRobbed(boolean robbed) {
        this.robbed = robbed;
    }

    public NodeBean getUpNode() {
        return upNode;
    }

    public void setUpNode(NodeBean upNode) {
        this.upNode = upNode;
    }

    public NodeBean getRightUpNode() {
        return rightUpNode;
    }

    public void setRightUpNode(NodeBean rightUpNode) {
        this.rightUpNode = rightUpNode;
    }

    public NodeBean getRightDownNode() {
        return rightDownNode;
    }

    public void setRightDownNode(NodeBean rightDownNode) {
        this.rightDownNode = rightDownNode;
    }

    public NodeBean getDownNode() {
        return downNode;
    }

    public void setDownNode(NodeBean downNode) {
        this.downNode = downNode;
    }

    public NodeBean getLeftDownNode() {
        return leftDownNode;
    }

    public void setLeftDownNode(NodeBean leftDownNode) {
        this.leftDownNode = leftDownNode;
    }

    public NodeBean getLeftUpNode() {
        return leftUpNode;
    }

    public void setLeftUpNode(NodeBean leftUpNode) {
        this.leftUpNode = leftUpNode;
    }

    public EdgeBean getRightUpEdge() {
        return rightUpEdge;
    }

    public void setRightUpEdge(EdgeBean rightUpEdge) {
        this.rightUpEdge = rightUpEdge;
    }

    public EdgeBean getRightEdge() {
        return rightEdge;
    }

    public void setRightEdge(EdgeBean rightEdge) {
        this.rightEdge = rightEdge;
    }

    public EdgeBean getRightDownEdge() {
        return rightDownEdge;
    }

    public void setRightDownEdge(EdgeBean rightDownEdge) {
        this.rightDownEdge = rightDownEdge;
    }

    public EdgeBean getLeftDownEdge() {
        return leftDownEdge;
    }

    public void setLeftDownEdge(EdgeBean leftDownEdge) {
        this.leftDownEdge = leftDownEdge;
    }

    public EdgeBean getLeftEdge() {
        return leftEdge;
    }

    public void setLeftEdge(EdgeBean leftEdge) {
        this.leftEdge = leftEdge;
    }

    public EdgeBean getLeftUpEdge() {
        return leftUpEdge;
    }

    public void setLeftUpEdge(EdgeBean leftUpEdge) {
        this.leftUpEdge = leftUpEdge;
    }
}
