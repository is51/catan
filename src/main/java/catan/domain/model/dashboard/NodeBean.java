package catan.domain.model.dashboard;

import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.dashboard.types.NodeOrientationType;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
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
import java.util.HashSet;
import java.util.Set;

import static catan.services.impl.PlayServiceImpl.ERROR_CODE_ERROR;

@Entity
@Table(name = "NODE")
public class NodeBean implements MapElement{
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
    private Building<NodeBuiltType> building;

    @Column(name = "ORIENTATION", unique = false, nullable = false)
    private NodeOrientationType orientation;

    @Embedded
    @AssociationOverrides({
            @AssociationOverride(name = "topLeft", joinColumns = @JoinColumn(name = "HEX_TOP_LEFT")),
            @AssociationOverride(name = "top", joinColumns = @JoinColumn(name = "HEX_TOP")),
            @AssociationOverride(name = "topRight", joinColumns = @JoinColumn(name = "HEX_TOP_RIGHT")),
            @AssociationOverride(name = "bottomRight", joinColumns = @JoinColumn(name = "HEX_BOTTOM_RIGHT")),
            @AssociationOverride(name = "bottom", joinColumns = @JoinColumn(name = "HEX_BOTTOM")),
            @AssociationOverride(name = "bottomLeft", joinColumns = @JoinColumn(name = "HEX_BOTTOM_LEFT"))
    })
    private VerticalLinks<HexBean> hexes = new VerticalLinks<HexBean>();

    @Embedded
    @AssociationOverrides({
            @AssociationOverride(name = "topLeft", joinColumns = @JoinColumn(name = "EDGE_TOP_LEFT")),
            @AssociationOverride(name = "top", joinColumns = @JoinColumn(name = "EDGE_TOP")),
            @AssociationOverride(name = "topRight", joinColumns = @JoinColumn(name = "EDGE_TOP_RIGHT")),
            @AssociationOverride(name = "bottomRight", joinColumns = @JoinColumn(name = "EDGE_BOTTOM_RIGHT")),
            @AssociationOverride(name = "bottom", joinColumns = @JoinColumn(name = "EDGE_BOTTOM")),
            @AssociationOverride(name = "bottomLeft", joinColumns = @JoinColumn(name = "EDGE_BOTTOM_LEFT"))
    })
    private VerticalLinks<EdgeBean> edges = new VerticalLinks<EdgeBean>();


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
        if (!(o instanceof NodeBean)) return false;

        NodeBean nodeBean = (NodeBean) o;

        if (!hexes.equals(nodeBean.hexes)) return false;
        if (orientation != nodeBean.orientation) return false;
        if (port != nodeBean.port) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = port.hashCode();
        result = 31 * result + orientation.hashCode();
        result = 31 * result + hexes.hashCode();

        return result;
    }
}
