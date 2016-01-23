package catan.domain.transfer.output.game.actions;

public class ActionDetails {
    private String code;
    private ActionParamsDetails params;

    public ActionDetails() {
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
