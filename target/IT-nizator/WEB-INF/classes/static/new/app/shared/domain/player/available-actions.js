System.register([], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var GROUPS, AvailableActions;
    return {
        setters:[],
        execute: function() {
            GROUPS = {
                "BUILD": ["BUILD_SETTLEMENT", "BUILD_CITY", "BUILD_ROAD", "BUY_CARD"],
                "TRADE": ["TRADE_PORT", "TRADE_PROPOSE"]
            };
            AvailableActions = (function () {
                function AvailableActions(params) {
                    this.isImmediate = params.isMandatory;
                    this.list = params.list;
                }
                AvailableActions.prototype.update = function (params) {
                    var _this = this;
                    var newActions = params.list.filter(function (action) {
                        return !_this.list.some(function (act) { return act.code === action.code; });
                    });
                    this.isImmediate = params.isMandatory;
                    this.list = params.list;
                    if (newActions.length) {
                        this.triggerUpdate(newActions);
                    }
                };
                AvailableActions.prototype.isEnabled = function (code) {
                    return this.list.some(function (item) { return item.code === code; });
                };
                AvailableActions.prototype.isEnabledGroup = function (code) {
                    var relatedActions = this._getRelatedActions(code);
                    if (relatedActions.length === 0) {
                        return true;
                    }
                    return this.list.some(function (item) { return relatedActions.indexOf(item.code) !== -1; });
                };
                AvailableActions.prototype._getRelatedActions = function (code) {
                    var relatedActions = [];
                    var current;
                    if (GROUPS[code]) {
                        for (var i in GROUPS[code]) {
                            current = GROUPS[code][i];
                            relatedActions.push(current);
                            relatedActions = relatedActions.concat(this._getRelatedActions(current));
                        }
                    }
                    return relatedActions;
                };
                AvailableActions.prototype.getParams = function (code) {
                    return this._get(code).params;
                };
                AvailableActions.prototype._get = function (code) {
                    return this.list.filter(function (item) { return item.code === code; })[0];
                };
                //TODO: try to replace with Subscribable (it's used in game-page.component)
                AvailableActions.prototype.onUpdate = function (onUpdate) {
                    this._onUpdate = onUpdate;
                };
                AvailableActions.prototype.cancelOnUpdate = function () {
                    this._onUpdate = undefined;
                };
                AvailableActions.prototype.triggerUpdate = function (newActions) {
                    if (this._onUpdate) {
                        this._onUpdate(newActions);
                    }
                };
                return AvailableActions;
            }());
            exports_1("AvailableActions", AvailableActions);
        }
    }
});
