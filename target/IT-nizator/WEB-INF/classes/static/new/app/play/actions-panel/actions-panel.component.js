System.register(['angular2/core', 'app/play/shared/services/execute-actions.service', 'app/shared/services/auth/auth-user.service', 'app/shared/modal-window/modal-window.service'], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, execute_actions_service_1, auth_user_service_1, modal_window_service_1;
    var ActionsPanelComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (execute_actions_service_1_1) {
                execute_actions_service_1 = execute_actions_service_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            },
            function (modal_window_service_1_1) {
                modal_window_service_1 = modal_window_service_1_1;
            }],
        execute: function() {
            ActionsPanelComponent = (function () {
                function ActionsPanelComponent(_authUser, _modalWindow, _actions) {
                    this._authUser = _authUser;
                    this._modalWindow = _modalWindow;
                    this._actions = _actions;
                }
                ActionsPanelComponent.prototype.isActionEnabled = function (actionCode) {
                    return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabled(actionCode);
                };
                ActionsPanelComponent.prototype.isActionGroupEnabled = function (groupCode) {
                    return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabledGroup(groupCode);
                };
                ActionsPanelComponent.prototype.isTradeReplyPanelVisible = function () {
                    return this._modalWindow.isVisible('TRADE_REPLY_PANEL');
                };
                ActionsPanelComponent.prototype.isGameResultsVisible = function () {
                    return this._modalWindow.isVisible('GAME_RESULTS');
                };
                ActionsPanelComponent.prototype.isExecuting = function (actionCode) {
                    return this._actions.isExecuting(actionCode);
                };
                ActionsPanelComponent.prototype.endTurn = function () {
                    this._actions.execute('END_TURN', this.game);
                };
                ActionsPanelComponent.prototype.throwDice = function () {
                    this._actions.execute('THROW_DICE', this.game);
                };
                ActionsPanelComponent.prototype.kickOffResources = function () {
                    this._actions.execute('KICK_OFF_RESOURCES', this.game);
                };
                ActionsPanelComponent.prototype.build = function () {
                    this._modalWindow.show("BUY_PANEL");
                };
                ActionsPanelComponent.prototype.showCards = function () {
                    this._modalWindow.show("CARDS_PANEL");
                };
                ActionsPanelComponent.prototype.showTradePanel = function () {
                    this._modalWindow.show("TRADE_PANEL");
                };
                ActionsPanelComponent.prototype.showTradeReplyPanel = function () {
                    this._actions.execute('TRADE_REPLY');
                };
                ActionsPanelComponent.prototype.showGameResults = function () {
                    this._modalWindow.show("GAME_RESULTS");
                };
                ActionsPanelComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-actions-panel',
                        templateUrl: 'app/play/actions-panel/actions-panel.component.html',
                        styleUrls: ['app/play/actions-panel/actions-panel.component.css'],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object, (typeof (_b = typeof modal_window_service_1.ModalWindowService !== 'undefined' && modal_window_service_1.ModalWindowService) === 'function' && _b) || Object, (typeof (_c = typeof execute_actions_service_1.ExecuteActionsService !== 'undefined' && execute_actions_service_1.ExecuteActionsService) === 'function' && _c) || Object])
                ], ActionsPanelComponent);
                return ActionsPanelComponent;
                var _a, _b, _c;
            }());
            exports_1("ActionsPanelComponent", ActionsPanelComponent);
        }
    }
});
