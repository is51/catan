package catan.domain.model.dashboard;

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
@Table(name = "EDGE")
public class EdgeBean {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EDGE_ID", unique = true, nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GAME_ID", nullable = false)
    private GameBean game;

    @OneToOne
    @JoinColumn(name = "BUILDING_ID")
    private BuildingBean building;

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

    public EdgeBean(GameBean game, BuildingBean building, HexBean upHex, HexBean downHex, NodeBean leftNode, NodeBean rightNode) {
        this.game = game;
        this.building = building;
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
