package catan.domain.model.game.actions;

import java.util.List;

public class ActionOnNodeParams extends ActionParams {

    private List<Integer> nodeIds;

    public ActionOnNodeParams() {
    }

    public ActionOnNodeParams(List<Integer> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public List<Integer> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(List<Integer> nodeIds) {
        this.nodeIds = nodeIds;
    }
}
