System.register(['angular2/core', 'app/shared/services/remote/remote.service', 'app/shared/domain/game'], function(exports_1, context_1) {
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
    var core_1, remote_service_1, game_1;
    var GameService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (remote_service_1_1) {
                remote_service_1 = remote_service_1_1;
            },
            function (game_1_1) {
                game_1 = game_1_1;
            }],
        execute: function() {
            GameService = (function () {
                function GameService(_remote) {
                    this._remote = _remote;
                }
                GameService.prototype.findAllByType = function (type) {
                    // type can be "PUBLIC" or "CURRENT"
                    var remoteRequestName = (type === 'CURRENT') ? 'game.listCurrent' : 'game.listPublic';
                    return this.findAllByRemoteService(remoteRequestName);
                };
                GameService.prototype.findAllByRemoteService = function (requestName) {
                    var _this = this;
                    return new Promise(function (resolve, reject) {
                        _this._remote.request(requestName)
                            .then(function (data) {
                            var items = data.map(function (item) { return new game_1.Game(item); });
                            resolve(items);
                        }, function (error) {
                            reject(error);
                        });
                    });
                };
                GameService.prototype.findById = function (id) {
                    var _this = this;
                    return new Promise(function (resolve, reject) {
                        _this._remote.request('game.details', { gameId: id }).then(function (data) {
                            var game = new game_1.Game(data);
                            resolve(game);
                        }, function (error) {
                            reject(error);
                        });
                    });
                };
                GameService.prototype.startRefreshing = function (game, delay, onEverySuccess, onEveryError) {
                    var _this = this;
                    this.stopRefreshing();
                    this._refreshingTimeout = setTimeout(function () {
                        _this.refresh(game).then(function (data) {
                            var continueUpdating = true;
                            if (onEverySuccess) {
                                continueUpdating = onEverySuccess(data) !== false;
                            }
                            if (continueUpdating) {
                                _this.startRefreshing(game, delay, onEverySuccess, onEveryError);
                            }
                        }, function (error) {
                            var continueUpdating = true;
                            if (onEveryError) {
                                continueUpdating = onEveryError(error) !== false;
                            }
                            if (continueUpdating) {
                                _this.startRefreshing(game, delay, onEverySuccess, onEveryError);
                            }
                        });
                    }, delay);
                };
                GameService.prototype.refresh = function (game) {
                    var _this = this;
                    return new Promise(function (resolve, reject) {
                        _this._remote.request('game.details', { gameId: game.getId() }).then(function (data) {
                            game.update(data);
                            resolve(data);
                        }, function (error) {
                            reject(error);
                        });
                    });
                };
                GameService.prototype.stopRefreshing = function () {
                    clearTimeout(this._refreshingTimeout);
                };
                GameService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof remote_service_1.RemoteService !== 'undefined' && remote_service_1.RemoteService) === 'function' && _a) || Object])
                ], GameService);
                return GameService;
                var _a;
            }());
            exports_1("GameService", GameService);
        }
    }
});
