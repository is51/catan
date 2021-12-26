System.register(['app/shared/domain/user', './available-actions', './resources'], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var user_1, available_actions_1, resources_1;
    var Player;
    return {
        setters:[
            function (user_1_1) {
                user_1 = user_1_1;
            },
            function (available_actions_1_1) {
                available_actions_1 = available_actions_1_1;
            },
            function (resources_1_1) {
                resources_1 = resources_1_1;
            }],
        execute: function() {
            Player = (function () {
                function Player(params) {
                    this.id = params.id;
                    this.colorId = params.colorId;
                    this.moveOrder = params.moveOrder;
                    this.ready = params.ready;
                    this.achievements = params.achievements;
                    this.developmentCards = params.developmentCards;
                    this.resources = new resources_1.Resources(params.resources);
                    this.user = new user_1.User(params.user);
                    if (params.availableActions) {
                        this.availableActions = new available_actions_1.AvailableActions(params.availableActions);
                    }
                    if (params.displayedMessage) {
                        this.displayedMessage = params.displayedMessage;
                        this.triggerDisplayedMessageShow(this.displayedMessage);
                    }
                    this.log = params.log;
                }
                Player.prototype.update = function (params) {
                    //TODO: revise this method
                    //this.id = params.id;
                    this.colorId = params.colorId;
                    this.moveOrder = params.moveOrder;
                    this.ready = params.ready;
                    this.achievements = params.achievements;
                    this.developmentCards = params.developmentCards;
                    this.resources.update(params.resources);
                    this.user.update(params.user);
                    if (params.availableActions) {
                        if (this.availableActions) {
                            this.availableActions.update(params.availableActions);
                        }
                        else {
                            this.availableActions = new available_actions_1.AvailableActions(params.availableActions);
                        }
                    }
                    else {
                        this.availableActions = undefined;
                    }
                    if (params.displayedMessage && (!this.displayedMessage || this.displayedMessage !== params.displayedMessage)) {
                        this.triggerDisplayedMessageShow(params.displayedMessage);
                    }
                    else if (this.displayedMessage && !params.displayedMessage) {
                        this.triggerDisplayedMessageHide();
                    }
                    this.displayedMessage = params.displayedMessage;
                    if (params.log) {
                        var lastLogItem = (this.log) ? this.log[0] : null;
                        this.log = params.log;
                        var newLogItems = this._getNewDisplayedLogItems(lastLogItem);
                        if (newLogItems) {
                            this.triggerDisplayedLogUpdate(newLogItems);
                        }
                    }
                };
                //TODO: try to replace with Subscribable (it's used in game-page.component)
                Player.prototype.onDisplayedMessageUpdate = function (onShowDisplayedMessage, onHideDisplayedMessage) {
                    this._onShowDisplayedMessage = onShowDisplayedMessage;
                    this._onHideDisplayedMessage = onHideDisplayedMessage;
                };
                Player.prototype.cancelOnDisplayedMessageUpdate = function () {
                    this._onShowDisplayedMessage = undefined;
                    this._onHideDisplayedMessage = undefined;
                };
                Player.prototype.triggerDisplayedMessageShow = function (text) {
                    if (this._onShowDisplayedMessage) {
                        this._onShowDisplayedMessage(text);
                    }
                };
                Player.prototype.triggerDisplayedMessageHide = function () {
                    if (this._onHideDisplayedMessage) {
                        this._onHideDisplayedMessage();
                    }
                };
                Player.prototype.onDisplayedLogUpdate = function (onLogUpdate) {
                    this._onDisplayedLogUpdate = onLogUpdate;
                };
                Player.prototype.cancelOnDisplayedLogUpdate = function () {
                    this._onDisplayedLogUpdate = undefined;
                };
                Player.prototype.triggerDisplayedLogUpdate = function (newLogItems) {
                    if (this._onDisplayedLogUpdate) {
                        this._onDisplayedLogUpdate(newLogItems);
                    }
                };
                Player.prototype._getNewDisplayedLogItems = function (lastLogItem) {
                    var newLogItems = [];
                    for (var _i = 0, _a = this.log; _i < _a.length; _i++) {
                        var logItem = _a[_i];
                        if (lastLogItem && lastLogItem.id !== logItem.id) {
                            if (logItem.displayedOnTop) {
                                newLogItems.push(logItem);
                            }
                        }
                        else {
                            break;
                        }
                    }
                    return (newLogItems.length) ? newLogItems.reverse() : null;
                };
                return Player;
            }());
            exports_1("Player", Player);
        }
    }
});
