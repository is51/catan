package catan.domain.transfer.output.game;

import catan.domain.model.game.types.GameUserActions;

public class ActionDetails {
    private GameUserActions code;
    private ActionParamsDetails actionParams;

    public ActionDetails() {
    }

    public ActionDetails(GameUserActions code) {
        this.code = code;
        this.actionParams = new ActionParamsDetails();
    }

    public GameUserActions getCode() {
        return code;
    }

    public void setCode(GameUserActions code) {
        this.code = code;
    }

    public ActionParamsDetails getParams() {
        return actionParams;
    }

    public void setParams(ActionParamsDetails actionParams) {
        this.actionParams = actionParams;
    }
}
