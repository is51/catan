const GROUPS = {
    "BUILD": ["BUILD_SETTLEMENT", "BUILD_CITY", "BUILD_ROAD", "BUY_CARD"],
    "TRADE": ["TRADE_PORT", "TRADE_PROPOSE"]
};

export class AvailableActions {
    isImmediate: boolean;
    list: AvailableAction[];

    constructor(params) {
        this.isImmediate = params.isMandatory;
        this.list = params.list;
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
}

interface AvailableAction {
    code: string;
    params;
}