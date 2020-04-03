System.register(['angular2/core', 'app/shared/services/auth/auth-user.service', 'app/shared/modal-window/modal-window.service', 'app/play/shared/services/execute-actions.service', 'app/shared/modal-window/modal-window.directive', 'app/shared/modal-window/modal-window-close.directive'], function(exports_1, context_1) {
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
    var core_1, auth_user_service_1, modal_window_service_1, execute_actions_service_1, modal_window_directive_1, modal_window_close_directive_1;
    var BuyPanelComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            },
            function (modal_window_service_1_1) {
                modal_window_service_1 = modal_window_service_1_1;
            },
            function (execute_actions_service_1_1) {
                execute_actions_service_1 = execute_actions_service_1_1;
            },
            function (modal_window_directive_1_1) {
                modal_window_directive_1 = modal_window_directive_1_1;
            },
            function (modal_window_close_directive_1_1) {
                modal_window_close_directive_1 = modal_window_close_directive_1_1;
            }],
        execute: function() {
            BuyPanelComponent = (function () {
                function BuyPanelComponent(_authUser, _modalWindow, _actions) {
                    this._authUser = _authUser;
                    this._modalWindow = _modalWindow;
                    this._actions = _actions;
                }
                BuyPanelComponent.prototype.isActionEnabled = function (actionCode) {
                    return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabled(actionCode);
                };
                BuyPanelComponent.prototype.buildSettlement = function () {
                    this._modalWindow.hide("BUY_PANEL");
                    this._actions.execute('BUILD_SETTLEMENT', this.game);
                };
                BuyPanelComponent.prototype.buildCity = function () {
                    this._modalWindow.hide("BUY_PANEL");
                    this._actions.execute('BUILD_CITY', this.game);
                };
                BuyPanelComponent.prototype.buildRoad = function () {
                    this._modalWindow.hide("BUY_PANEL");
                    this._actions.execute('BUILD_ROAD', this.game);
                };
                BuyPanelComponent.prototype.buyCard = function () {
                    this._modalWindow.hide("BUY_PANEL");
                    this._actions.execute('BUY_CARD', this.game);
                };
                BuyPanelComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-buy-panel',
                        templateUrl: 'app/play/buy-panel/buy-panel.component.html',
                        styleUrls: [
                            'app/play/buy-panel/buy-panel.component.css',
                            'app/shared/modal-window/modal-window.directive.css'
                        ],
                        directives: [
                            modal_window_directive_1.ModalWindowDirective,
                            modal_window_close_directive_1.ModalWindowCloseDirective,
                        ],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object, (typeof (_b = typeof modal_window_service_1.ModalWindowService !== 'undefined' && modal_window_service_1.ModalWindowService) === 'function' && _b) || Object, (typeof (_c = typeof execute_actions_service_1.ExecuteActionsService !== 'undefined' && execute_actions_service_1.ExecuteActionsService) === 'function' && _c) || Object])
                ], BuyPanelComponent);
                return BuyPanelComponent;
                var _a, _b, _c;
            }());
            exports_1("BuyPanelComponent", BuyPanelComponent);
        }
    }
});
