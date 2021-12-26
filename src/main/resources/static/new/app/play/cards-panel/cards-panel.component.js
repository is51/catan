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
    var GRADIENT_COUNT_COLORS, CardsPanelComponent;
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
            //TODO: use global config for colors
            GRADIENT_COUNT_COLORS = {
                1: ['#d26953', '#e19583'],
                2: ['#5c98d5', '#81b0e3'],
                3: ['#d5c65d', '#e9de88'],
                4: ['#5dd582', '#85e6a4']
            };
            CardsPanelComponent = (function () {
                function CardsPanelComponent(_authUser, _modalWindow, _actions) {
                    this._authUser = _authUser;
                    this._modalWindow = _modalWindow;
                    this._actions = _actions;
                }
                CardsPanelComponent.prototype.ngOnInit = function () {
                    var colorId = this.game.getCurrentPlayer(this._authUser.get()).colorId;
                    this.gradientCountColor1 = GRADIENT_COUNT_COLORS[colorId][0];
                    this.gradientCountColor2 = GRADIENT_COUNT_COLORS[colorId][1];
                };
                CardsPanelComponent.prototype.isActionEnabled = function (actionCode) {
                    return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabled(actionCode);
                };
                CardsPanelComponent.prototype.cards = function () {
                    return this.game.getCurrentPlayer(this._authUser.get()).developmentCards;
                    ;
                };
                CardsPanelComponent.prototype.useCardYearOfPlenty = function () {
                    if (this.isActionEnabled('USE_CARD_YEAR_OF_PLENTY')) {
                        this._modalWindow.hide("CARDS_PANEL");
                        this._actions.execute('USE_CARD_YEAR_OF_PLENTY', this.game);
                    }
                };
                CardsPanelComponent.prototype.useCardMonopoly = function () {
                    if (this.isActionEnabled('USE_CARD_MONOPOLY')) {
                        this._modalWindow.hide("CARDS_PANEL");
                        this._actions.execute('USE_CARD_MONOPOLY', this.game);
                    }
                };
                CardsPanelComponent.prototype.useCardRoadBuilding = function () {
                    if (this.isActionEnabled('USE_CARD_ROAD_BUILDING')) {
                        this._modalWindow.hide("CARDS_PANEL");
                        this._actions.execute('USE_CARD_ROAD_BUILDING', this.game);
                    }
                };
                CardsPanelComponent.prototype.useCardKnight = function () {
                    if (this.isActionEnabled('USE_CARD_KNIGHT')) {
                        this._modalWindow.hide("CARDS_PANEL");
                        this._actions.execute('USE_CARD_KNIGHT', this.game);
                    }
                };
                CardsPanelComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-cards-panel',
                        templateUrl: 'app/play/cards-panel/cards-panel.component.html',
                        styleUrls: [
                            'app/play/cards-panel/cards-panel.component.css',
                            'app/shared/modal-window/modal-window.directive.css'
                        ],
                        directives: [
                            modal_window_directive_1.ModalWindowDirective,
                            modal_window_close_directive_1.ModalWindowCloseDirective,
                        ],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object, (typeof (_b = typeof modal_window_service_1.ModalWindowService !== 'undefined' && modal_window_service_1.ModalWindowService) === 'function' && _b) || Object, (typeof (_c = typeof execute_actions_service_1.ExecuteActionsService !== 'undefined' && execute_actions_service_1.ExecuteActionsService) === 'function' && _c) || Object])
                ], CardsPanelComponent);
                return CardsPanelComponent;
                var _a, _b, _c;
            }());
            exports_1("CardsPanelComponent", CardsPanelComponent);
        }
    }
});
