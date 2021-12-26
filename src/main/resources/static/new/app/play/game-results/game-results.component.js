System.register(['angular2/core', 'angular2/router', 'app/shared/modal-window/modal-window.service', 'app/shared/modal-window/modal-window.directive', 'app/shared/modal-window/modal-window-close.directive'], function(exports_1, context_1) {
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
    var core_1, router_1, modal_window_service_1, modal_window_directive_1, modal_window_close_directive_1;
    var GameResultsComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (modal_window_service_1_1) {
                modal_window_service_1 = modal_window_service_1_1;
            },
            function (modal_window_directive_1_1) {
                modal_window_directive_1 = modal_window_directive_1_1;
            },
            function (modal_window_close_directive_1_1) {
                modal_window_close_directive_1 = modal_window_close_directive_1_1;
            }],
        execute: function() {
            GameResultsComponent = (function () {
                function GameResultsComponent(_modalWindow) {
                    this._modalWindow = _modalWindow;
                }
                GameResultsComponent.prototype.ngOnInit = function () {
                    this.playersSorted = this._getPlayersSortedByVictoryPoints();
                    this.winnerName = this.playersSorted[0].user.getDisplayedName();
                    this._modalWindow.show('GAME_RESULTS');
                };
                GameResultsComponent.prototype._getPlayersSortedByVictoryPoints = function () {
                    return this.game.players
                        .slice()
                        .sort(function (a, b) { return b.achievements.realVictoryPoints - a.achievements.realVictoryPoints; });
                };
                GameResultsComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-game-results',
                        templateUrl: 'app/play/game-results/game-results.component.html',
                        styleUrls: [
                            'app/play/game-results/game-results.component.css',
                            'app/shared/modal-window/modal-window.directive.css'
                        ],
                        directives: [
                            router_1.RouterLink,
                            modal_window_directive_1.ModalWindowDirective,
                            modal_window_close_directive_1.ModalWindowCloseDirective
                        ],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof modal_window_service_1.ModalWindowService !== 'undefined' && modal_window_service_1.ModalWindowService) === 'function' && _a) || Object])
                ], GameResultsComponent);
                return GameResultsComponent;
                var _a;
            }());
            exports_1("GameResultsComponent", GameResultsComponent);
        }
    }
});
