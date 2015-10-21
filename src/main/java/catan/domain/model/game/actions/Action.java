package catan.domain.model.game.actions;

import catan.domain.model.game.types.GameUserActionCode;

public class Action {
    private String code;
    private ActionParams params;

    public Action() {
    }

    public Action(GameUserActionCode code) {
        this.code = code.name();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ActionParams getParams() {
        return params;
    }

    public void setParams(ActionParams params) {
        this.params = params;
    }
}
