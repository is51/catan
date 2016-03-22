package catan.domain.model.game.actions;

import catan.domain.model.game.types.GameUserActionCode;

public class Action {
    private String code;
    private ActionParams params;
    private boolean notify;
    private String notifyMessage;

    public Action() {
    }

    public Action(GameUserActionCode code) {
        this(code, null, false, null);
    }

    public Action(GameUserActionCode code, ActionParams params) {
        this(code, params, false, null);
    }

    public Action(GameUserActionCode code, boolean notify, String notifyMessage) {
        this(code, null, notify, notifyMessage);
    }

    public Action(GameUserActionCode code, ActionParams params, boolean notify, String notifyMessage) {
        this.code = code.name();
        this.params = params;
        this.notify = notify;
        this.notifyMessage = notifyMessage;
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
