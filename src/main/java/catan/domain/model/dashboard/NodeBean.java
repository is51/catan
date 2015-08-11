package catan.domain.model.dashboard;

import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;

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

    @OneToOne
    @JoinColumn(name = "BUILDING_ID")
    private BuildingBean building;

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
    private EdgeBean rightUpEdge;

    @ManyToOne
    @JoinColumn(name = "DOWN_EDGE_ID")
    private EdgeBean dowEdge;

    @ManyToOne
    @JoinColumn(name = "LEFT_UP_EDGE_ID")
    private EdgeBean leftUpEdge;

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

    public BuildingBean getBuilding() {
        return building;
    }

    public void setBuilding(BuildingBean building) {
        this.building = building;
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

    public EdgeBean getDowEdge() {
        return dowEdge;
    }

    public void setDowEdge(EdgeBean dowEdge) {
        this.dowEdge = dowEdge;
    }

    public EdgeBean getLeftUpEdge() {
        return leftUpEdge;
    }

    public void setLeftUpEdge(EdgeBean leftUpEdge) {
        this.leftUpEdge = leftUpEdge;
    }
}
