package catan.domain.model.dashboard;

import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.dashboard.types.NodeOrientationType;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "CT_NODE")
public class NodeBean implements MapElement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "NODE_ID", unique = true, nullable = false)
    private int id;

    @Column(name = "NODE_ABSOLUTE_ID", unique = false, nullable = false)
    private Integer absoluteId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GAME_ID", nullable = false)
    private GameBean game;

    @Column(name = "PORT", unique = false, nullable = false)
    private NodePortType port;

    @Embedded
    private Building<NodeBuiltType> building;

    @Column(name = "ORIENTATION", unique = false, nullable = false)
    private NodeOrientationType orientation;

    @Transient
    private VerticalLinks<HexBean> hexes = new VerticalLinks<HexBean>();

    @Transient
    private VerticalLinks<EdgeBean> edges = new VerticalLinks<EdgeBean>();


    public NodeBean() {
    }

    public NodeBean(int absoluteId, GameBean game, NodePortType port) {
        this.absoluteId = absoluteId;
        this.game = game;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Integer getAbsoluteId() {
        return absoluteId;
    }

    public void setAbsoluteId(Integer absoluteId) {
        this.absoluteId = absoluteId;
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

    public Building<NodeBuiltType> getBuilding() {
        return building;
    }

    public void setBuilding(Building<NodeBuiltType> building) {
        this.building = building;
    }

    public NodeOrientationType getOrientation() {
        return orientation;
    }

    public void setOrientation(NodeOrientationType orientation) {
        this.orientation = orientation;
    }

    public VerticalLinks<HexBean> getHexes() {
        return hexes;
    }

    public void setHexes(VerticalLinks<HexBean> hexes) {
        this.hexes = hexes;
    }

    public VerticalLinks<EdgeBean> getEdges() {
        return edges;
    }

    public void setEdges(VerticalLinks<EdgeBean> edges) {
        this.edges = edges;
    }

    public boolean hasBuildingBelongsToUser(GameUserBean gameUser) {
        return this.getBuilding() != null && this.getBuilding().getBuildingOwner().equals(gameUser);
    }

    public boolean hasAllNeighbourNodesEmpty() {
        for (EdgeBean edge : getEdges().listAllNotNullItems()) {
            for (NodeBean node : edge.getNodes().listAllNotNullItems()) {
                if (!node.equals(this) && node.getBuilding() != null) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean hasNeighbourRoadBelongsToGameUser(GameUserBean gameUser) {
        for (EdgeBean edge : getEdges().listAllNotNullItems()) {
            if (edge.getBuilding() != null && edge.getBuilding().getBuildingOwner().equals(gameUser)) {
                return true;
            }
        }

        return false;
    }

    public boolean couldBeUsedForBuildingSettlementByGameUserInMainStage(GameUserBean gameUser) {
        if (this.getBuilding() != null) {
            return false;
        }

        boolean nodeHasGameUsersNeighbourRoad = false;
        for (EdgeBean edge : getEdges().listAllNotNullItems()) {
            if (edge.getBuilding() != null && edge.getBuilding().getBuildingOwner().equals(gameUser)) {
                nodeHasGameUsersNeighbourRoad = true;
            }

            for (NodeBean node : edge.getNodes().listAllNotNullItems()) {
                if (!node.equals(this) && node.getBuilding() != null) {
                    return false;
                }
            }
        }

        return nodeHasGameUsersNeighbourRoad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeBean nodeBean = (NodeBean) o;

        if (absoluteId != null ? !absoluteId.equals(nodeBean.absoluteId) : nodeBean.absoluteId != null) return false;
        if (port != nodeBean.port) return false;
        return orientation == nodeBean.orientation;

    }

    @Override
    public int hashCode() {
        int result = absoluteId != null ? absoluteId.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (orientation != null ? orientation.hashCode() : 0);

        return result;
    }
}
