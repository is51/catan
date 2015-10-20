package catan.domain.transfer.output.game;

import java.util.List;

public class AllAvailableActionsDetails {
    private List<ActionDetails> list;
    private boolean isMandatory;

    public AllAvailableActionsDetails() {
    }

    public AllAvailableActionsDetails(List<ActionDetails> list, boolean isMandatory) {
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
