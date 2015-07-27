package catan.domain.model.dashboard;

import catan.domain.model.dashboard.types.EdgeBuiltType;
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
@Table(name = "EDGE")
public class EdgeBean {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EDGE_ID", unique = true, nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "GAME_ID")
    private GameBean game;

    @Column(name = "BUILT", unique = false, nullable = false)
    private EdgeBuiltType built;

    @ManyToOne
    @JoinColumn(name = "BUILDING_OWNER_ID")
    private GameUserBean buildingOwner;

    //TODO: probably related hexes will be stored as Set<HexBean>
    @ManyToOne
    @JoinColumn(name = "UP_HEX_ID")
    private HexBean upHex;

    @ManyToOne
    @JoinColumn(name = "DOWN_HEX_ID")
    private HexBean downHex;

    //TODO: probably related nodes will be stored as Set<NodeBean>
    @ManyToOne
    @JoinColumn(name = "LEFT_NODE_ID")
    private NodeBean leftNode;

    @ManyToOne
    @JoinColumn(name = "RIGHT_NODE_ID")
    private NodeBean rightNode;

    public EdgeBean() {
    }

    public EdgeBean(GameBean game, EdgeBuiltType built, GameUserBean buildingOwner, HexBean upHex, HexBean downHex, NodeBean leftNode, NodeBean rightNode) {
        this.game = game;
        this.built = built;
        this.buildingOwner = buildingOwner;
        this.upHex = upHex;
        this.downHex = downHex;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
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

    public EdgeBuiltType getBuilt() {
        return built;
    }

    public void setBuilt(EdgeBuiltType built) {
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

    public HexBean getDownHex() {
        return downHex;
    }

    public void setDownHex(HexBean downHex) {
        this.downHex = downHex;
    }

    public NodeBean getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(NodeBean leftNode) {
        this.leftNode = leftNode;
    }

    public NodeBean getRightNode() {
        return rightNode;
    }

    public void setRightNode(NodeBean rightNode) {
        this.rightNode = rightNode;
    }
}
