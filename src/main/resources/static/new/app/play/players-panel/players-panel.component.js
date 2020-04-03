System.register(['angular2/core', 'app/shared/services/auth/auth-user.service'], function(exports_1, context_1) {
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
    var core_1, auth_user_service_1;
    var AVATARS_PATH, AVATARS_COUNT, PlayersPanelComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            }],
        execute: function() {
            AVATARS_PATH = '/new/resources/avatars/'; // Depends on #rootpath
            AVATARS_COUNT = 4;
            PlayersPanelComponent = (function () {
                function PlayersPanelComponent(_authUser) {
                    this._authUser = _authUser;
                    this.displayCompact = false;
                    this.PLAYER_BLOCK_HEIGHT = 111;
                    this.ACTIVE_PLAYER_BLOCK_SCALE = 1.12;
                }
                PlayersPanelComponent.prototype.ngOnInit = function () {
                    this._setPlayersSortedByMoveOrderCurrentUserFirst();
                };
                PlayersPanelComponent.prototype._setPlayersSortedByMoveOrderCurrentUserFirst = function () {
                    var currentPlayer = this.game.getCurrentPlayer(this._authUser.get());
                    var players = this.game.players.slice();
                    var playersCount = players.length;
                    this.players = players.sort(function (a, b) {
                        var aMoveOrder = a.moveOrder + ((a !== currentPlayer && a.moveOrder < currentPlayer.moveOrder) ? playersCount : 0);
                        var bMoveOrder = b.moveOrder + ((b !== currentPlayer && b.moveOrder < currentPlayer.moveOrder) ? playersCount : 0);
                        return aMoveOrder - bMoveOrder;
                    });
                };
                PlayersPanelComponent.prototype.isActive = function (player) {
                    return this.game.currentMove === player.moveOrder;
                };
                PlayersPanelComponent.prototype.isActivePrevious = function (index) {
                    return index > 0 && this.isActive(this.players[index - 1]);
                };
                PlayersPanelComponent.prototype.isCurrentUser = function (player) {
                    return this._authUser.get().id === player.user.id;
                };
                PlayersPanelComponent.prototype.isBiggestArmy = function (player) {
                    return this.game.biggestArmyOwnerId === player.id;
                };
                PlayersPanelComponent.prototype.isLongestWay = function (player) {
                    return this.game.longestWayOwnerId === player.id;
                };
                PlayersPanelComponent.prototype.isResourcesCountCritical = function (player) {
                    return player.achievements.totalResources > 7; //TODO: put to config? "7" is used in kick-resources panel as well
                };
                PlayersPanelComponent.prototype.getPlayerBlockY = function (index) {
                    var _this = this;
                    var isActivePlayerBefore = this.players
                        .slice(0, index)
                        .some(function (player, pIndex) { return _this.isActive(player); });
                    return index * this.PLAYER_BLOCK_HEIGHT
                        + ((isActivePlayerBefore) ? (this.ACTIVE_PLAYER_BLOCK_SCALE - 1) * this.PLAYER_BLOCK_HEIGHT : 0);
                };
                PlayersPanelComponent.prototype.getAvatarUrl = function (player) {
                    var avatarId = player.user.id % AVATARS_COUNT + 1;
                    return AVATARS_PATH + 'a' + avatarId + '.svg';
                };
                //TODO: use global config for colors
                PlayersPanelComponent.prototype.getColor = function (player) {
                    var colors = {
                        1: '#ab4242',
                        2: '#3e77ae',
                        3: '#B58B3C',
                        4: '#42ab73'
                    };
                    return colors[player.colorId];
                };
                PlayersPanelComponent.prototype.toggleDisplayCompact = function () {
                    this.displayCompact = !this.displayCompact;
                };
                PlayersPanelComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-players-panel',
                        templateUrl: 'app/play/players-panel/players-panel.component.html',
                        styleUrls: ['app/play/players-panel/players-panel.component.css'],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object])
                ], PlayersPanelComponent);
                return PlayersPanelComponent;
                var _a;
            }());
            exports_1("PlayersPanelComponent", PlayersPanelComponent);
        }
    }
});
