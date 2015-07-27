package catan.domain.transfer.output.dashboard;

import java.util.List;

public class DashboardDetails {
    private List<EdgeDetails> edges;
    private List<HexDetails> hexes;
    private List<NodeDetails> nodes;

    public List<EdgeDetails> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeDetails> edges) {
        this.edges = edges;
    }

    public List<HexDetails> getHexes() {
        return hexes;
    }

    public void setHexes(List<HexDetails> hexes) {
        this.hexes = hexes;
    }

    public List<NodeDetails> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeDetails> nodes) {
        this.nodes = nodes;
    }
}
