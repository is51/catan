const GROUPS = {
    "BUILD": ["BUILD_SETTLEMENT", "BUILD_CITY", "BUILD_ROAD", "BUY_CARD"],
    "TRADE": ["TRADE_PORT", "TRADE_PROPOSE"]
};

export class AvailableActions {
    isImmediate: boolean;
    list: AvailableAction[];

    private _onUpdate: Function;

    constructor(params) {
        this.isImmediate = params.isMandatory;
        this.list = <AvailableAction[]>params.list;
    }

    update(params) {
        let newActions = params.list.filter(action => {
            return !this.list.some(act => act.code === action.code);
        });

        this.isImmediate = params.isMandatory;
        this.list = <AvailableAction[]>params.list;

        if (newActions.length) {
            this.triggerUpdate(newActions);
        }
    }

    isEnabled(code: string) {
        return this.list.some(item => item.code === code);
    }

    isEnabledGroup(code: string) {
        let relatedActions = this._getRelatedActions(code);

        if (relatedActions.length === 0) {
            return true;
        }

        return this.list.some(item => relatedActions.indexOf(item.code) !== -1);
    }

    private _getRelatedActions(code: string) {
        let relatedActions = [];
        var current;

        if (GROUPS[code]) {
            for (var i in GROUPS[code]) {
                current = GROUPS[code][i];
                relatedActions.push(current);
                relatedActions = relatedActions.concat(this._getRelatedActions(current));
            }
        }

        return relatedActions;
    }

    getParams(code: string) {
        return this._get(code).params;
    }

    private _get(code: string) {
        return this.list.filter(item => item.code === code)[0];
    }

    //TODO: try to replace with Subscribable (it's used in game-page.component)
    onUpdate(onUpdate: Function) {
        this._onUpdate = onUpdate;
    }
    cancelOnUpdate() {
        this._onUpdate = undefined;
    }
    triggerUpdate(newActions: AvailableAction[]) {
        if (this._onUpdate) {
            this._onUpdate(newActions);
        }
    }
}

interface AvailableAction {
    code: string;
    params;
    notify: boolean;
    notifyMessage: string;
}