package catan.domain.transfer.output.game.actions;

import java.util.List;

public class AvailableActionsDetails {
    private List<ActionDetails> list;
    private boolean isMandatory;

    public AvailableActionsDetails() {
    }

    public AvailableActionsDetails(List<ActionDetails> list, boolean isMandatory) {
        this.list = list;
        this.isMandatory = isMandatory;
    }

    public List<ActionDetails> getList() {
        return list;
    }

    public void setList(List<ActionDetails> list) {
        this.list = list;
    }

    public boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }
}
