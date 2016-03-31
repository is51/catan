package catan.domain.model.dashboard;

import catan.domain.model.dashboard.types.EdgeBuiltType;
import catan.domain.model.dashboard.types.EdgeOrientationType;
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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CT_EDGE")
public class EdgeBean implements MapElement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EDGE_ID", unique = true, nullable = false)
    private int id;

    @Column(name = "EDGE_ABSOLUTE_ID", unique = false, nullable = false)
    private Integer absoluteId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GAME_ID", nullable = false)
    private GameBean game;

    @Embedded
    private Building<EdgeBuiltType> building;

    @Column(name = "ORIENTATION", unique = false, nullable = false)
    private EdgeOrientationType orientation;

    @Transient
    private HorizontalLinks<HexBean> hexes = new HorizontalLinks<HexBean>();

    @Transient
    private VerticalLinks<NodeBean> nodes = new VerticalLinks<NodeBean>();

    public EdgeBean() {
    }

    public EdgeBean(int absoluteId, GameBean game) {
        this.absoluteId = absoluteId;
        this.game = game;
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
        if (o == null || getClass() != o.getClass()) return false;

        EdgeBean edgeBean = (EdgeBean) o;

        if (!absoluteId.equals(edgeBean.absoluteId)) return false;
        return orientation == edgeBean.orientation;

    }

    @Override
    public int hashCode() {
        int result = absoluteId.hashCode();
        result = 31 * result + orientation.hashCode();

        return result;
    }
}
