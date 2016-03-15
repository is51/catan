package catan.domain.model.game.actions;

import java.util.List;

public class BuildOnEdgeParams extends ActionParams {

    private List<Integer> edgeIds;

    public BuildOnEdgeParams() {
    }

    public BuildOnEdgeParams(List<Integer> edgeIds) {
        this.edgeIds = edgeIds;
    }

    public List<Integer> getEdgeIds() {
        return edgeIds;
    }

    public void setEdgeIds(List<Integer> edgeIds) {
        this.edgeIds = edgeIds;
    }
}
