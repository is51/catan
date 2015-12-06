package catan.domain.model.dashboard;

import catan.domain.model.dashboard.types.EdgeBuiltType;
import catan.domain.model.dashboard.types.EdgeOrientationType;
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

@Entity
@Table(name = "EDGE")
public class EdgeBean implements MapElement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EDGE_ID", unique = true, nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GAME_ID", nullable = false)
    private GameBean game;

    @Embedded
    private Building<EdgeBuiltType> building;

    @Column(name = "ORIENTATION", unique = false, nullable = false)
    private EdgeOrientationType orientation;

    @Embedded
    @AssociationOverrides({
            @AssociationOverride(name = "topLeft", joinColumns = @JoinColumn(name = "HEX_TOP_LEFT")),
            @AssociationOverride(name = "topRight", joinColumns = @JoinColumn(name = "HEX_TOP_RIGHT")),
            @AssociationOverride(name = "right", joinColumns = @JoinColumn(name = "HEX_RIGHT")),
            @AssociationOverride(name = "bottomRight", joinColumns = @JoinColumn(name = "HEX_BOTTOM_RIGHT")),
            @AssociationOverride(name = "bottomLeft", joinColumns = @JoinColumn(name = "HEX_BOTTOM_LEFT")),
            @AssociationOverride(name = "left", joinColumns = @JoinColumn(name = "HEX_LEFT"))
    })
    private HorizontalLinks<HexBean> hexes = new HorizontalLinks<HexBean>();

    @Embedded
    @AssociationOverrides({
            @AssociationOverride(name = "topLeft", joinColumns = @JoinColumn(name = "NODE_TOP_LEFT")),
            @AssociationOverride(name = "top", joinColumns = @JoinColumn(name = "NODE_TOP")),
            @AssociationOverride(name = "topRight", joinColumns = @JoinColumn(name = "NODE_TOP_RIGHT")),
            @AssociationOverride(name = "bottomRight", joinColumns = @JoinColumn(name = "NODE_BOTTOM_RIGHT")),
            @AssociationOverride(name = "bottom", joinColumns = @JoinColumn(name = "NODE_BOTTOM")),
            @AssociationOverride(name = "bottomLeft", joinColumns = @JoinColumn(name = "NODE_BOTTOM_LEFT"))
    })
    private VerticalLinks<NodeBean> nodes = new VerticalLinks<NodeBean>();

    public EdgeBean() {
    }

    public EdgeBean(GameBean game) {
        this.game = game;
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

    public Building<EdgeBuiltType> getBuilding() {
        return building;
    }

    public void setBuilding(Building<EdgeBuiltType> building) {
        this.building = building;
    }

    public EdgeOrientationType getOrientation() {
        return orientation;
    }

    public void setOrientation(EdgeOrientationType orientation) {
        this.orientation = orientation;
    }

    public HorizontalLinks<HexBean> getHexes() {
        return hexes;
    }

    public void setHexes(HorizontalLinks<HexBean> hexes) {
        this.hexes = hexes;
    }

    public VerticalLinks<NodeBean> getNodes() {
        return nodes;
    }

    public void setNodes(VerticalLinks<NodeBean> nodes) {
        this.nodes = nodes;
    }

    public Set<EdgeBean> fetchNeighborEdgesAccessibleForBuildingRoad(GameUserBean gameUser) {
        Set<EdgeBean> edges = new HashSet<EdgeBean>();
        for (NodeBean node : getNodes().listAllNotNullItems()) {
            if (node.getBuilding() != null && !node.getBuilding().getBuildingOwner().equals(gameUser)) {
                continue;
            }
            for (EdgeBean neighborEdge : node.getEdges().listAllNotNullItems()) {
                if (neighborEdge.getBuilding() == null && !this.equals(neighborEdge)) {
                    edges.add(neighborEdge);
                }
            }
        }

        return edges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EdgeBean)) return false;

        EdgeBean edgeBean = (EdgeBean) o;

        if (!hexes.equals(edgeBean.hexes)) return false;
        if (orientation != edgeBean.orientation) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = orientation.hashCode();
        result = 31 * result + hexes.hashCode();

        return result;
    }
}
