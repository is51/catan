package catan.domain.model.game.actions;

import java.util.List;

public class ActionOnHexParams extends ActionParams {

    private List<Integer> hexIds;

    public ActionOnHexParams() {
    }

    public ActionOnHexParams(List<Integer> hexIds) {
        this.hexIds = hexIds;
    }

    public List<Integer> getHexIds() {
        return hexIds;
    }

    public void setHexIds(List<Integer> hexIds) {
        this.hexIds = hexIds;
    }
}
