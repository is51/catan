package catan.domain.model.dashboard;

import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.GameBean;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
@Table(name = "HEX")
public class HexBean implements MapElement{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "HEX_ID", unique = true, nullable = false)
    private int id;

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "NODE_TOP_LEFT")
    private NodeBean nodeTopLeft;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "NODE_TOP_RIGHT")
    private NodeBean nodeTopRight;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "NODE_BOTTOM_RIGHT")
    private NodeBean nodeBottomRight;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "NODE_BOTTOM_LEFT")
    private NodeBean nodeBottomLeft;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "NODE_TOP")
    private NodeBean nodeTop;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "NODE_BOTTOM")
    private NodeBean nodeBottom;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EDGE_TOP_LEFT")
    private EdgeBean edgeTopLeft;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EDGE_TOP_RIGHT")
    private EdgeBean edgeTopRight;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EDGE_BOTTOM_RIGHT")
    private EdgeBean edgeBottomRight;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EDGE_BOTTOM_LEFT")
    private EdgeBean edgeBottomLeft;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EDGE_RIGHT")
    private EdgeBean edgeRight;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EDGE_LEFT")
    private EdgeBean edgeLeft;

    public HexBean() {
    }

    public HexBean(GameBean game, Coordinates coordinates, HexType resourceType, Integer dice, boolean robbed) {
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
        VerticalLinks<NodeBean> nodeBeanVerticalLinks = new VerticalLinks<NodeBean>();
        nodeBeanVerticalLinks.setBottom(nodeBottom);
        nodeBeanVerticalLinks.setTop(nodeTop);
        nodeBeanVerticalLinks.setTopLeft(nodeTopLeft);
        nodeBeanVerticalLinks.setTopRight(nodeTopRight);
        nodeBeanVerticalLinks.setBottomLeft(nodeBottomLeft);
        nodeBeanVerticalLinks.setBottomRight(nodeBottomRight);
        
        return nodeBeanVerticalLinks;
    }

    public HorizontalLinks<EdgeBean> getEdges() {
        HorizontalLinks<EdgeBean> edgeBeanHorizontalLinks = new HorizontalLinks<EdgeBean>();
        edgeBeanHorizontalLinks.setLeft(edgeLeft);
        edgeBeanHorizontalLinks.setRight(edgeRight);
        edgeBeanHorizontalLinks.setBottomLeft(edgeBottomLeft);
        edgeBeanHorizontalLinks.setBottomRight(edgeBottomRight);
        edgeBeanHorizontalLinks.setTopLeft(edgeTopLeft);
        edgeBeanHorizontalLinks.setTopRight(edgeTopRight);

        return edgeBeanHorizontalLinks;
    }


    public NodeBean getNodeTopLeft() {
        return nodeTopLeft;
    }

    public void setNodeTopLeft(NodeBean nodeTopLeft) {
        this.nodeTopLeft = nodeTopLeft;
    }

    public NodeBean getNodeTopRight() {
        return nodeTopRight;
    }

    public void setNodeTopRight(NodeBean nodeTopRight) {
        this.nodeTopRight = nodeTopRight;
    }

    public NodeBean getNodeBottomRight() {
        return nodeBottomRight;
    }

    public void setNodeBottomRight(NodeBean nodeBottomRight) {
        this.nodeBottomRight = nodeBottomRight;
    }

    public NodeBean getNodeBottomLeft() {
        return nodeBottomLeft;
    }

    public void setNodeBottomLeft(NodeBean nodeBottomLeft) {
        this.nodeBottomLeft = nodeBottomLeft;
    }

    public NodeBean getNodeTop() {
        return nodeTop;
    }

    public void setNodeTop(NodeBean nodeTop) {
        this.nodeTop = nodeTop;
    }

    public NodeBean getNodeBottom() {
        return nodeBottom;
    }

    public void setNodeBottom(NodeBean nodeBottom) {
        this.nodeBottom = nodeBottom;
    }

    public EdgeBean getEdgeTopLeft() {
        return edgeTopLeft;
    }

    public void setEdgeTopLeft(EdgeBean edgeTopLeft) {
        this.edgeTopLeft = edgeTopLeft;
    }

    public EdgeBean getEdgeTopRight() {
        return edgeTopRight;
    }

    public void setEdgeTopRight(EdgeBean edgeTopRight) {
        this.edgeTopRight = edgeTopRight;
    }

    public EdgeBean getEdgeBottomRight() {
        return edgeBottomRight;
    }

    public void setEdgeBottomRight(EdgeBean edgeBottomRight) {
        this.edgeBottomRight = edgeBottomRight;
    }

    public EdgeBean getEdgeBottomLeft() {
        return edgeBottomLeft;
    }

    public void setEdgeBottomLeft(EdgeBean edgeBottomLeft) {
        this.edgeBottomLeft = edgeBottomLeft;
    }

    public EdgeBean getEdgeRight() {
        return edgeRight;
    }

    public void setEdgeRight(EdgeBean edgeRight) {
        this.edgeRight = edgeRight;
    }

    public EdgeBean getEdgeLeft() {
        return edgeLeft;
    }

    public void setEdgeLeft(EdgeBean edgeLeft) {
        this.edgeLeft = edgeLeft;
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
}
