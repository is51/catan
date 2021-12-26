System.register(['angular2/core', 'angular2/router', 'app/shared/services/game/game.service', 'app/shared/alert/alert.service', 'app/menu/shared/players-list/players-list.component', 'app/menu/shared/join-public-game-button/join-public-game-button.directive'], function(exports_1, context_1) {
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
    var core_1, router_1, game_service_1, alert_service_1, players_list_component_1, join_public_game_button_directive_1;
    var GamesListComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (game_service_1_1) {
                game_service_1 = game_service_1_1;
            },
            function (alert_service_1_1) {
                alert_service_1 = alert_service_1_1;
            },
            function (players_list_component_1_1) {
                players_list_component_1 = players_list_component_1_1;
            },
            function (join_public_game_button_directive_1_1) {
                join_public_game_button_directive_1 = join_public_game_button_directive_1_1;
            }],
        execute: function() {
            GamesListComponent = (function () {
                function GamesListComponent(_gameService, _alert) {
                    this._gameService = _gameService;
                    this._alert = _alert;
                    this.games = null;
                }
                GamesListComponent.prototype.ngOnInit = function () {
                    this.update();
                };
                GamesListComponent.prototype.update = function () {
                    var _this = this;
                    this._gameService.findAllByType(this.typeOfGames)
                        .then(function (games) { return _this.games = games; })
                        .catch(function (data) { return _this._alert.message('Getting Games List Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')); });
                };
                GamesListComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-games-list',
                        templateUrl: 'app/menu/shared/games-list/games-list.component.html',
                        styleUrls: ['app/menu/shared/games-list/games-list.component.css'],
                        directives: [
                            router_1.RouterLink,
                            players_list_component_1.PlayersListComponent,
                            join_public_game_button_directive_1.JoinPublicGameButtonDirective
                        ],
                        inputs: ['typeOfGames']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof game_service_1.GameService !== 'undefined' && game_service_1.GameService) === 'function' && _a) || Object, (typeof (_b = typeof alert_service_1.AlertService !== 'undefined' && alert_service_1.AlertService) === 'function' && _b) || Object])
                ], GamesListComponent);
                return GamesListComponent;
                var _a, _b;
            }());
            exports_1("GamesListComponent", GamesListComponent);
        }
    }
});
