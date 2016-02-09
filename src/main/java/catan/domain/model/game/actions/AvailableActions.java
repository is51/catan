package catan.domain.model.game.actions;

import java.util.List;

//TODO: thing about refactoring and abolishing this class
public class AvailableActions {
    private List<Action> list;
    private boolean isMandatory;

    public AvailableActions() {
    }

    public AvailableActions(List<Action> list, boolean isMandatory) {
        this.list = list;
        this.isMandatory = isMandatory;
    }

    public List<Action> getList() {
        return list;
    }

    public void setList(List<Action> list) {
        this.list = list;
    }

    public boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }
}
