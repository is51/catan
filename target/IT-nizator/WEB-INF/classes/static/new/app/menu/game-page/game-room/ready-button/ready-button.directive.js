System.register(['angular2/core', 'app/shared/services/remote/remote.service', 'app/shared/services/auth/auth-user.service', 'app/shared/services/game/game.service', 'app/shared/alert/alert.service'], function(exports_1, context_1) {
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
    var core_1, remote_service_1, auth_user_service_1, game_service_1, alert_service_1;
    var ReadyButtonDirective;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (remote_service_1_1) {
                remote_service_1 = remote_service_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            },
            function (game_service_1_1) {
                game_service_1 = game_service_1_1;
            },
            function (alert_service_1_1) {
                alert_service_1 = alert_service_1_1;
            }],
        execute: function() {
            ReadyButtonDirective = (function () {
                function ReadyButtonDirective(_remote, _authUser, _gameService, _alert) {
                    this._remote = _remote;
                    this._authUser = _authUser;
                    this._gameService = _gameService;
                    this._alert = _alert;
                }
                ReadyButtonDirective.prototype.onClick = function () {
                    var _this = this;
                    var requestName = (this.isCurrentPlayerReady()) ? "game.notReady" : "game.ready";
                    this._remote.request(requestName, { gameId: this.game.getId() })
                        .then(function () { return _this._gameService.refresh(_this.game); })
                        .catch(function (data) { return _this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')); });
                };
                ReadyButtonDirective.prototype.isCurrentPlayerReady = function () {
                    return this.game.getCurrentPlayer(this._authUser.get()).ready;
                };
                ReadyButtonDirective = __decorate([
                    core_1.Directive({
                        selector: '[ct-ready-button]',
                        host: {
                            '(click)': 'onClick($event)',
                            '[class.btn-success]': 'isCurrentPlayerReady()'
                        },
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof remote_service_1.RemoteService !== 'undefined' && remote_service_1.RemoteService) === 'function' && _a) || Object, (typeof (_b = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _b) || Object, (typeof (_c = typeof game_service_1.GameService !== 'undefined' && game_service_1.GameService) === 'function' && _c) || Object, (typeof (_d = typeof alert_service_1.AlertService !== 'undefined' && alert_service_1.AlertService) === 'function' && _d) || Object])
                ], ReadyButtonDirective);
                return ReadyButtonDirective;
                var _a, _b, _c, _d;
            }());
            exports_1("ReadyButtonDirective", ReadyButtonDirective);
        }
    }
});
