package catan.domain.model.dashboard;

import catan.domain.model.dashboard.types.HexType;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "CT_HEX")
public class HexBean implements MapElement{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "HEX_ID", unique = true, nullable = false)
    private int id;

    @Column(name = "HEX_ABSOLUTE_ID", unique = false, nullable = false)
    private Integer absoluteId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GAME_ID", nullable = false)
    private GameBean game;

    @Embedded
    private Coordinates coordinates;

    @Column(name = "RESOURCE_TYPE", unique = false, nullable = false)
    private HexType resourceType;

    @Column(name = "DICE", unique = false, nullable = true)
    private Integer dice;

    @Column(name = "IS_ROBBED", unique = false, nullable = false)
    private boolean robbed;

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

    @Embedded
    @AssociationOverrides({
            @AssociationOverride(name = "topLeft", joinColumns = @JoinColumn(name = "EDGE_TOP_LEFT")),
            @AssociationOverride(name = "topRight", joinColumns = @JoinColumn(name = "EDGE_TOP_RIGHT")),
            @AssociationOverride(name = "right", joinColumns = @JoinColumn(name = "EDGE_RIGHT")),
            @AssociationOverride(name = "bottomRight", joinColumns = @JoinColumn(name = "EDGE_BOTTOM_RIGHT")),
            @AssociationOverride(name = "bottomLeft", joinColumns = @JoinColumn(name = "EDGE_BOTTOM_LEFT")),
            @AssociationOverride(name = "left", joinColumns = @JoinColumn(name = "EDGE_LEFT"))
    })
    private HorizontalLinks<EdgeBean> edges = new HorizontalLinks<EdgeBean>();

    public HexBean() {
    }

    public HexBean(int absoluteId, GameBean game, Coordinates coordinates, HexType resourceType, Integer dice, boolean robbed) {
        this.absoluteId = absoluteId;
        this.game = game;
        this.coordinates = coordinates;
        this.resourceType = resourceType;
        this.dice = dice;
        this.robbed = robbed;
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

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public HexType getResourceType() {
        return resourceType;
    }

    public void setResourceType(HexType resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getDice() {
        return dice;
    }

    public void setDice(Integer dice) {
        this.dice = dice;
    }

    public boolean isRobbed() {
        return robbed;
    }

    public void setRobbed(boolean robbed) {
        this.robbed = robbed;
    }

    public VerticalLinks<NodeBean> getNodes() {
        return nodes;
    }

    public void setNodes(VerticalLinks<NodeBean> nodes) {
        this.nodes = nodes;
    }

    public HorizontalLinks<EdgeBean> getEdges() {
        return edges;
    }

    public void setEdges(HorizontalLinks<EdgeBean> edges) {
        this.edges = edges;
    }

    public List<NodeBean> fetchNodesWithBuildings() {
        List<NodeBean> nodesWithBuildings = new ArrayList<NodeBean>();
        for (NodeBean node : this.nodes.listAllNotNullItems()) {
            if (node.getBuilding() != null) {
                nodesWithBuildings.add(node);
            }
        }

        return nodesWithBuildings;
    }

    public Set<GameUserBean> fetchGameUsersWithBuildingsAtNodes() {
        Set<GameUserBean> gameUsersWithBuildingsAtNodes = new HashSet<GameUserBean>();
        for (NodeBean node : this.nodes.listAllNotNullItems()) {
            if (node.getBuilding() != null) {
                gameUsersWithBuildingsAtNodes.add(node.getBuilding().getBuildingOwner());
            }
        }

        return gameUsersWithBuildingsAtNodes;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HexBean)) return false;

        HexBean hexBean = (HexBean) o;

        if (dice != null ? !dice.equals(hexBean.dice) : hexBean.dice != null) return false;
        if (!coordinates.equals(hexBean.coordinates)) return false;
        if (resourceType != hexBean.resourceType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = coordinates.hashCode();
        result = 31 * result + resourceType.hashCode();
        result = 31 * result + (dice != null ? dice.hashCode() : 0);
        
        return result;
    }

    @Override
    public String toString() {
        return "Hex{" +
                "id=" + id +
                ", absoluteId=" + absoluteId +
                ", " + (robbed ? "robbed" : "not robbed") +
                ", dice=" + dice +
                ", type=" + resourceType +
                ", x:y=(" + coordinates.getxCoordinate() + ":" + coordinates.getyCoordinate() + ")}";
    }
}
