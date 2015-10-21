package catan.domain.transfer.output.game.actions;

import catan.domain.model.game.types.GameUserActionCode;

public class ActionDetails {
    private String code;
    private ActionParamsDetails params;

    public ActionDetails() {
    }

    public ActionDetails(GameUserActionCode code) {
        this.code = code.name();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ActionParamsDetails getParams() {
        return params;
    }

    public void setParams(ActionParamsDetails params) {
        this.params = params;
    }
}
