package catan.domain.transfer.output.game.actions;

public class ActionDetails {
    private String code;
    private ActionParamsDetails params;
    private boolean notify;
    private String notifyMessage;

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

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public String getNotifyMessage() {
        return notifyMessage;
    }

    public void setNotifyMessage(String notifyMessage) {
        this.notifyMessage = notifyMessage;
    }
}
