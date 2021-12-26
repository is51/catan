System.register(['angular2/core', 'app/play/shared/services/execute-actions.service', 'app/shared/modal-window/modal-window.service', 'app/shared/modal-window/modal-window.directive', 'app/shared/modal-window/modal-window-close.directive', './trade-players-panel/trade-players-panel.component', './trade-port-panel/trade-port-panel.component', 'app/play/shared/choose-resources/choose-resources-cancel.directive'], function(exports_1, context_1) {
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
    var core_1, execute_actions_service_1, modal_window_service_1, modal_window_directive_1, modal_window_close_directive_1, trade_players_panel_component_1, trade_port_panel_component_1, choose_resources_cancel_directive_1;
    var PANEL_ID, TradePanelComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (execute_actions_service_1_1) {
                execute_actions_service_1 = execute_actions_service_1_1;
            },
            function (modal_window_service_1_1) {
                modal_window_service_1 = modal_window_service_1_1;
            },
            function (modal_window_directive_1_1) {
                modal_window_directive_1 = modal_window_directive_1_1;
            },
            function (modal_window_close_directive_1_1) {
                modal_window_close_directive_1 = modal_window_close_directive_1_1;
            },
            function (trade_players_panel_component_1_1) {
                trade_players_panel_component_1 = trade_players_panel_component_1_1;
            },
            function (trade_port_panel_component_1_1) {
                trade_port_panel_component_1 = trade_port_panel_component_1_1;
            },
            function (choose_resources_cancel_directive_1_1) {
                choose_resources_cancel_directive_1 = choose_resources_cancel_directive_1_1;
            }],
        execute: function() {
            PANEL_ID = 'TRADE_PANEL';
            TradePanelComponent = (function () {
                function TradePanelComponent(_actions, _modalWindow) {
                    var _this = this;
                    this._actions = _actions;
                    this._modalWindow = _modalWindow;
                    this.isVisibleTradePortPanel = false;
                    this.isVisibleTradePlayersPanel = false;
                    this.onShow = function () { return _this.showTradePort(); };
                    this.onHide = function () {
                        _this.isVisibleTradePortPanel = false;
                        _this.isVisibleTradePlayersPanel = false;
                    };
                }
                TradePanelComponent.prototype.showTradePort = function () {
                    var _this = this;
                    this.isVisibleTradePortPanel = true;
                    this.isVisibleTradePlayersPanel = false;
                    this._actions.execute('TRADE_PORT', this.game)
                        .then(function () {
                        _this._modalWindow.hide(PANEL_ID);
                    })
                        .catch(function () {
                        if (!_this.isVisibleTradePlayersPanel) {
                            _this._modalWindow.hide(PANEL_ID);
                        }
                    });
                };
                TradePanelComponent.prototype.showTradePlayers = function () {
                    var _this = this;
                    this.isVisibleTradePortPanel = false;
                    this.isVisibleTradePlayersPanel = true;
                    this._actions.execute('TRADE_PLAYERS', this.game)
                        .then(function () {
                        _this._modalWindow.hide(PANEL_ID);
                    })
                        .catch(function () {
                        if (!_this.isVisibleTradePortPanel) {
                            _this._modalWindow.hide(PANEL_ID);
                        }
                    });
                };
                TradePanelComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-trade-panel',
                        templateUrl: 'app/play/trade-panel/trade-panel.component.html',
                        styleUrls: [
                            'app/play/trade-panel/trade-panel.component.css',
                            'app/shared/modal-window/modal-window.directive.css'
                        ],
                        directives: [
                            modal_window_directive_1.ModalWindowDirective,
                            modal_window_close_directive_1.ModalWindowCloseDirective,
                            trade_players_panel_component_1.TradePlayersPanelComponent,
                            trade_port_panel_component_1.TradePortPanelComponent,
                            choose_resources_cancel_directive_1.ChooseResourcesCancelDirective
                        ],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof execute_actions_service_1.ExecuteActionsService !== 'undefined' && execute_actions_service_1.ExecuteActionsService) === 'function' && _a) || Object, (typeof (_b = typeof modal_window_service_1.ModalWindowService !== 'undefined' && modal_window_service_1.ModalWindowService) === 'function' && _b) || Object])
                ], TradePanelComponent);
                return TradePanelComponent;
                var _a, _b;
            }());
            exports_1("TradePanelComponent", TradePanelComponent);
        }
    }
});
