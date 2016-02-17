package catan.domain.model.game.actions;

import java.util.List;

public class BuildOnNodeParams extends ActionParams {

    private List<Integer> nodeIds;

    public BuildOnNodeParams() {
    }

    public BuildOnNodeParams(List<Integer> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public List<Integer> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(List<Integer> nodeIds) {
        this.nodeIds = nodeIds;
    }
}
