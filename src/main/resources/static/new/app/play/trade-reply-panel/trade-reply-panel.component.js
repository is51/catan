System.register(['angular2/core', 'app/play/shared/services/play.service', 'app/shared/services/game/game.service', 'app/shared/services/auth/auth-user.service', 'app/shared/modal-window/modal-window.service', 'app/shared/alert/alert.service', 'app/shared/domain/player/resources', 'app/shared/modal-window/modal-window.directive', 'app/shared/modal-window/modal-window-close.directive'], function(exports_1, context_1) {
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
    var core_1, play_service_1, game_service_1, auth_user_service_1, modal_window_service_1, alert_service_1, resources_1, modal_window_directive_1, modal_window_close_directive_1;
    var PANEL_ID, TradeReplyPanelComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (play_service_1_1) {
                play_service_1 = play_service_1_1;
            },
            function (game_service_1_1) {
                game_service_1 = game_service_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            },
            function (modal_window_service_1_1) {
                modal_window_service_1 = modal_window_service_1_1;
            },
            function (alert_service_1_1) {
                alert_service_1 = alert_service_1_1;
            },
            function (resources_1_1) {
                resources_1 = resources_1_1;
            },
            function (modal_window_directive_1_1) {
                modal_window_directive_1 = modal_window_directive_1_1;
            },
            function (modal_window_close_directive_1_1) {
                modal_window_close_directive_1 = modal_window_close_directive_1_1;
            }],
        execute: function() {
            PANEL_ID = 'TRADE_REPLY_PANEL';
            TradeReplyPanelComponent = (function () {
                function TradeReplyPanelComponent(_authUser, _play, _gameService, _modalWindow, _alert) {
                    var _this = this;
                    this._authUser = _authUser;
                    this._play = _play;
                    this._gameService = _gameService;
                    this._modalWindow = _modalWindow;
                    this._alert = _alert;
                    this.onShow = function () { return _this._init(); };
                }
                TradeReplyPanelComponent.prototype._init = function () {
                    this.currentPlayer = this.game.getCurrentPlayer(this._authUser.get());
                    var actionParams = this.currentPlayer.availableActions.getParams('TRADE_REPLY');
                    var proposition = actionParams.resources;
                    this.propositionGive = new resources_1.Resources();
                    this.propositionGet = new resources_1.Resources();
                    for (var i in proposition) {
                        if (proposition[i] > 0) {
                            this.propositionGive[i] = proposition[i];
                            this.propositionGet[i] = 0;
                        }
                        else {
                            this.propositionGive[i] = 0;
                            this.propositionGet[i] = -proposition[i];
                        }
                    }
                    this.offerId = actionParams.offerId;
                    this.offerIsActive = true;
                    this.proposerName = this.game.getMovingPlayer().user.getDisplayedName();
                };
                TradeReplyPanelComponent.prototype.accept = function () {
                    var _this = this;
                    this._play.tradeAccept(this.game, this.offerId)
                        .then(function () {
                        _this._modalWindow.hide(PANEL_ID);
                        _this._gameService.refresh(_this.game);
                    })
                        .catch(function (data) {
                        _this._alert.message('Trade Propose Accept error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                        if (data.errorCode === "OFFER_ALREADY_ACCEPTED") {
                            _this._modalWindow.hide(PANEL_ID);
                            _this._gameService.refresh(_this.game);
                        }
                    });
                };
                TradeReplyPanelComponent.prototype.decline = function () {
                    var _this = this;
                    this._play.tradeDecline(this.game, this.offerId)
                        .then(function () {
                        _this._modalWindow.hide(PANEL_ID);
                        _this._gameService.refresh(_this.game);
                    })
                        .catch(function (data) {
                        if (data.errorCode === "OFFER_ALREADY_ACCEPTED") {
                            _this._modalWindow.hide(PANEL_ID);
                            _this._gameService.refresh(_this.game);
                        }
                        else {
                            _this._alert.message('Trade Propose Decline error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                        }
                    });
                };
                TradeReplyPanelComponent.prototype.acceptDisabled = function () {
                    var currentPlayerResources = this.currentPlayer.resources;
                    for (var i in this.propositionGive) {
                        if (currentPlayerResources[i] < this.propositionGive[i]) {
                            return true;
                        }
                    }
                    return false;
                };
                TradeReplyPanelComponent.prototype.ngDoCheck = function () {
                    // TODO: it should be done only on changes. Currently - many useless updates
                    this._checkIfOfferIsActive();
                };
                TradeReplyPanelComponent.prototype._checkIfOfferIsActive = function () {
                    if (this._modalWindow.isVisible(PANEL_ID) && this.currentPlayer) {
                        var offerId = (this.currentPlayer.availableActions.isEnabled('TRADE_REPLY'))
                            ? this.currentPlayer.availableActions.getParams('TRADE_REPLY').offerId
                            : null;
                        this.offerIsActive = this.offerId === offerId;
                    }
                };
                TradeReplyPanelComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-trade-reply-panel',
                        templateUrl: 'app/play/trade-reply-panel/trade-reply-panel.component.html',
                        styleUrls: [
                            'app/play/trade-reply-panel/trade-reply-panel.component.css',
                            'app/shared/modal-window/modal-window.directive.css'
                        ],
                        directives: [
                            modal_window_directive_1.ModalWindowDirective,
                            modal_window_close_directive_1.ModalWindowCloseDirective
                        ],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object, (typeof (_b = typeof play_service_1.PlayService !== 'undefined' && play_service_1.PlayService) === 'function' && _b) || Object, (typeof (_c = typeof game_service_1.GameService !== 'undefined' && game_service_1.GameService) === 'function' && _c) || Object, (typeof (_d = typeof modal_window_service_1.ModalWindowService !== 'undefined' && modal_window_service_1.ModalWindowService) === 'function' && _d) || Object, (typeof (_e = typeof alert_service_1.AlertService !== 'undefined' && alert_service_1.AlertService) === 'function' && _e) || Object])
                ], TradeReplyPanelComponent);
                return TradeReplyPanelComponent;
                var _a, _b, _c, _d, _e;
            }());
            exports_1("TradeReplyPanelComponent", TradeReplyPanelComponent);
        }
    }
});
