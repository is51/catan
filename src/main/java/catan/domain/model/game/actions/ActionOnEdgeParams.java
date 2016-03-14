package catan.domain.model.game.actions;

import java.util.List;

public class ActionOnEdgeParams extends ActionParams {

    private List<Integer> edgeIds;

    public ActionOnEdgeParams() {
    }

    public ActionOnEdgeParams(List<Integer> edgeIds) {
        this.edgeIds = edgeIds;
    }

    public List<Integer> getEdgeIds() {
        return edgeIds;
    }

    public void setEdgeIds(List<Integer> edgeIds) {
        this.edgeIds = edgeIds;
    }
}
