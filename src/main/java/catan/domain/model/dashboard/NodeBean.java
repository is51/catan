package catan.domain.model.dashboard;

import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;

import javax.persistence.Column;
import javax.persistence.Entity;
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

    @ManyToOne
    @JoinColumn(name = "GAME_ID")
    private GameBean game;

    @Column(name = "PORT", unique = false, nullable = false)
    private NodePortType port;

    @Column(name = "BUILT", unique = false, nullable = false)
    private NodeBuiltType built;

    @ManyToOne
    @JoinColumn(name = "BUILDING_OWNER_ID")
    private GameUserBean buildingOwner;

    @ManyToOne
    @JoinColumn(name = "UP_HEX_ID")
    private HexBean upHex;

    @ManyToOne
    @JoinColumn(name = "RIGHT_DOWN_HEX_ID")
    private HexBean rightDownHex;

    @ManyToOne
    @JoinColumn(name = "LEFT_DOWN_HEX_ID")
    private HexBean leftDownHex;

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

    public NodeBean(GameBean game, NodePortType port, NodeBuiltType built, GameUserBean buildingOwner, HexBean upHex, HexBean rightDownHex, HexBean leftDownHex, EdgeBean rightUpEdge, EdgeBean dowEdge, EdgeBean leftUpEdge) {
        this.game = game;
        this.port = port;
        this.built = built;
        this.buildingOwner = buildingOwner;
        this.upHex = upHex;
        this.rightDownHex = rightDownHex;
        this.leftDownHex = leftDownHex;
        this.rightUpEdge = rightUpEdge;
        this.dowEdge = dowEdge;
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

    public NodePortType getPort() {
        return port;
    }

    public void setPort(NodePortType port) {
        this.port = port;
    }

    public NodeBuiltType getBuilt() {
        return built;
    }

    public void setBuilt(NodeBuiltType built) {
        this.built = built;
    }

    public GameUserBean getBuildingOwner() {
        return buildingOwner;
    }

    public void setBuildingOwner(GameUserBean buildingOwner) {
        this.buildingOwner = buildingOwner;
    }

    public HexBean getUpHex() {
        return upHex;
    }

    public void setUpHex(HexBean upHex) {
        this.upHex = upHex;
    }

    public HexBean getRightDownHex() {
        return rightDownHex;
    }

    public void setRightDownHex(HexBean rightDownHex) {
        this.rightDownHex = rightDownHex;
    }

    public HexBean getLeftDownHex() {
        return leftDownHex;
    }

    public void setLeftDownHex(HexBean leftDownHex) {
        this.leftDownHex = leftDownHex;
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
