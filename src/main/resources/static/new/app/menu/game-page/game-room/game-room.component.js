System.register(['angular2/core', 'angular2/router', 'app/shared/services/auth/auth-user.service', 'app/menu/shared/players-list/players-list.component', './cancel-game-button/cancel-game-button.directive', './leave-game-button/leave-game-button.directive', './ready-button/ready-button.directive', './game-map-overview/game-map-overview.component'], function(exports_1, context_1) {
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
    var core_1, router_1, auth_user_service_1, players_list_component_1, cancel_game_button_directive_1, leave_game_button_directive_1, ready_button_directive_1, game_map_overview_component_1;
    var GameRoomComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            },
            function (players_list_component_1_1) {
                players_list_component_1 = players_list_component_1_1;
            },
            function (cancel_game_button_directive_1_1) {
                cancel_game_button_directive_1 = cancel_game_button_directive_1_1;
            },
            function (leave_game_button_directive_1_1) {
                leave_game_button_directive_1 = leave_game_button_directive_1_1;
            },
            function (ready_button_directive_1_1) {
                ready_button_directive_1 = ready_button_directive_1_1;
            },
            function (game_map_overview_component_1_1) {
                game_map_overview_component_1 = game_map_overview_component_1_1;
            }],
        execute: function() {
            GameRoomComponent = (function () {
                function GameRoomComponent(_authUser) {
                    this._authUser = _authUser;
                }
                GameRoomComponent.prototype.isCurrentUserCreator = function () {
                    return this._authUser.get().id === this.game.creatorId;
                };
                GameRoomComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-game-room',
                        templateUrl: 'app/menu/game-page/game-room/game-room.component.html',
                        directives: [
                            players_list_component_1.PlayersListComponent,
                            cancel_game_button_directive_1.CancelGameButtonDirective,
                            leave_game_button_directive_1.LeaveGameButtonDirective,
                            ready_button_directive_1.ReadyButtonDirective,
                            game_map_overview_component_1.GameMapOverviewComponent,
                            router_1.RouterLink
                        ],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object])
                ], GameRoomComponent);
                return GameRoomComponent;
                var _a;
            }());
            exports_1("GameRoomComponent", GameRoomComponent);
        }
    }
});
