package catan.domain.model.dashboard;

import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.dashboard.types.NodeOrientationType;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "NODE")
public class NodeBean {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "NODE_ID", unique = true, nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GAME_ID", nullable = false)
    private GameBean game;

    @Column(name = "PORT", unique = false, nullable = false)
    private NodePortType port;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "BUILT", column = @Column(name = "BUILT")) })
    @Enumerated(EnumType.STRING)
    private Building<NodeBuiltType> building;

    @Column(name = "ORIENTATION", unique = false, nullable = false)
    private NodeOrientationType orientation;

    @ManyToOne
    @JoinColumn(name = "UP_HEX_ID")
    private HexBean upHex;          // inverted downHex

    @ManyToOne
    @JoinColumn(name = "RIGHT_DOWN_HEX_ID")
    private HexBean rightDownHex;   // inverted leftUpHex

    @ManyToOne
    @JoinColumn(name = "LEFT_DOWN_HEX_ID")
    private HexBean leftDownHex;    // inverted rightUpHex

    @ManyToOne
    @JoinColumn(name = "RIGHT_UP_EDGE_ID")
    private EdgeBean rightUpEdge;   // inverted leftDownEdge

    @ManyToOne
    @JoinColumn(name = "DOWN_EDGE_ID")
    private EdgeBean downEdge;       // inverted upEdge

    @ManyToOne
    @JoinColumn(name = "LEFT_UP_EDGE_ID")
    private EdgeBean leftUpEdge;   // inverted rightDownEdge

    public NodeBean() {
    }

    public NodeBean(GameBean game, NodePortType port) {
        this.game = game;
        this.port = port;
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

    public NodePortType getPort() {
        return port;
    }

    public void setPort(NodePortType port) {
        this.port = port;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public NodeOrientationType getOrientation() {
        return orientation;
    }

    public void setOrientation(NodeOrientationType orientation) {
        this.orientation = orientation;
    }

    public HexBean getUpHex() {
        return upHex;
    }

    public void setUpHex(HexBean upHex) {
        this.upHex = upHex;
    }

    public void populateDownHex(HexBean downHex) {
        this.upHex = downHex;
    }

    public HexBean getRightDownHex() {
        return rightDownHex;
    }

    public void setRightDownHex(HexBean rightDownHex) {
        this.rightDownHex = rightDownHex;
    }

    public void populateLeftUpHex(HexBean leftUpHex) {
        this.rightDownHex = leftUpHex;
    }

    public HexBean getLeftDownHex() {
        return leftDownHex;
    }

    public void setLeftDownHex(HexBean leftDownHex) {
        this.leftDownHex = leftDownHex;
    }

    public void populateRightUpHex(HexBean rightUpHex) {
        this.leftDownHex = rightUpHex;
    }

    public EdgeBean getRightUpEdge() {
        return rightUpEdge;
    }

    public void setRightUpEdge(EdgeBean rightUpEdge) {
        this.rightUpEdge = rightUpEdge;
    }

    public void populateLeftDownEdge(EdgeBean leftDownEdge) {
        this.rightUpEdge = leftDownEdge;
    }

    public EdgeBean getDownEdge() {
        return downEdge;
    }

    public void setDownEdge(EdgeBean downEdge) {
        this.downEdge = downEdge;
    }

    public void populateUpEdge(EdgeBean upEdge) {
        this.downEdge = upEdge;
    }

    public EdgeBean getLeftUpEdge() {
        return leftUpEdge;
    }

    public void setLeftUpEdge(EdgeBean leftUpEdge) {
        this.leftUpEdge = leftUpEdge;
    }

    public void populateRightDownEdge(EdgeBean rightDownEdge) {
        this.leftUpEdge = rightDownEdge;
    }
}
