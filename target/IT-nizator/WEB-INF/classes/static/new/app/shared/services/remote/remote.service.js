System.register(['angular2/core', 'angular2/http', 'app/shared/services/auth/auth-token.service'], function(exports_1, context_1) {
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
    var core_1, http_1, auth_token_service_1;
    var RemoteService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (auth_token_service_1_1) {
                auth_token_service_1 = auth_token_service_1_1;
            }],
        execute: function() {
            RemoteService = (function () {
                function RemoteService(_http, _authToken) {
                    this._http = _http;
                    this._authToken = _authToken;
                    this._requestsOptions = new Map();
                    this._setConfig();
                }
                RemoteService.prototype._setConfig = function () {
                    //TODO: create and use @RemoteConfig in app.component
                    this._setDefaultOptions(new http_1.RequestOptions({
                        headers: new http_1.Headers({
                            'Content-Type': 'application/x-www-form-urlencoded',
                            'Accept': 'application/json'
                        }),
                        method: 'post'
                    }));
                    this._setRequestOptionsUrlOnly('auth.register', '/api/user/register');
                    this._setRequestOptionsUrlOnly('auth.registerAndLoginGuest', '/api/user/register/guest');
                    this._setRequestOptionsUrlOnly('auth.login', '/api/user/login');
                    this._setRequestOptionsUrlOnly('auth.logout', '/api/user/logout');
                    this._setRequestOptionsUrlOnly('auth.details', '/api/user/details');
                    this._setRequestOptionsUrlOnly('game.create', '/api/game/create');
                    this._setRequestOptionsUrlOnly('game.listCurrent', '/api/game/list/current');
                    this._setRequestOptionsUrlOnly('game.listPublic', '/api/game/list/public');
                    this._setRequestOptionsUrlOnly('game.joinPublic', '/api/game/join/public');
                    this._setRequestOptionsUrlOnly('game.joinPrivate', '/api/game/join/private');
                    this._setRequestOptionsUrlOnly('game.details', '/api/game/details');
                    this._setRequestOptionsUrlOnly('game.leave', '/api/game/leave');
                    this._setRequestOptionsUrlOnly('game.cancel', '/api/game/cancel');
                    this._setRequestOptionsUrlOnly('game.ready', '/api/game/ready');
                    this._setRequestOptionsUrlOnly('game.notReady', '/api/game/not-ready');
                    this._setRequestOptionsUrlOnly('play.endTurn', '/api/play/end-turn');
                    this._setRequestOptionsUrlOnly('play.buildSettlement', '/api/play/build/settlement');
                    this._setRequestOptionsUrlOnly('play.buildCity', '/api/play/build/city');
                    this._setRequestOptionsUrlOnly('play.buildRoad', '/api/play/build/road');
                    this._setRequestOptionsUrlOnly('play.buyCard', '/api/play/buy/card');
                    this._setRequestOptionsUrlOnly('play.throwDice', '/api/play/throw-dice');
                    this._setRequestOptionsUrlOnly('play.useCardYearOfPlenty', '/api/play/use-card/year-of-plenty');
                    this._setRequestOptionsUrlOnly('play.useCardRoadBuilding', '/api/play/use-card/road-building');
                    this._setRequestOptionsUrlOnly('play.useCardMonopoly', '/api/play/use-card/monopoly');
                    this._setRequestOptionsUrlOnly('play.useCardKnight', '/api/play/use-card/knight');
                    this._setRequestOptionsUrlOnly('play.moveRobber', '/api/play/robbery/move-robber');
                    this._setRequestOptionsUrlOnly('play.choosePlayerToRob', '/api/play/robbery/choose-player-to-rob');
                    this._setRequestOptionsUrlOnly('play.kickOffResources', '/api/play/robbery/kick-off-resources');
                    this._setRequestOptionsUrlOnly('play.tradePort', '/api/play/trade/port');
                    this._setRequestOptionsUrlOnly('play.tradePropose', '/api/play/trade/propose');
                    this._setRequestOptionsUrlOnly('play.tradeAccept', '/api/play/trade/reply/accept');
                    this._setRequestOptionsUrlOnly('play.tradeDecline', '/api/play/trade/reply/decline');
                    ;
                };
                RemoteService.prototype._setDefaultOptions = function (options) {
                    this._defaultOptions = options;
                };
                RemoteService.prototype._setRequestOptionsUrlOnly = function (requestName, url) {
                    this._setRequestOptions(requestName, new http_1.RequestOptions({ url: url }));
                };
                RemoteService.prototype._setRequestOptions = function (requestName, options) {
                    this._requestsOptions.set(requestName, options);
                };
                RemoteService.prototype._getRequestOptionsMerged = function (requestName) {
                    return this._defaultOptions.merge(this._requestsOptions.get(requestName));
                };
                RemoteService.prototype.request = function (requestName, params) {
                    var _this = this;
                    if (params === void 0) { params = null; }
                    var token = this._authToken.get();
                    var fullParams = this._mergeParamsWithToken(params, token);
                    var body = this._convertToBody(fullParams);
                    var options = this._getRequestOptionsMerged(requestName)
                        .merge(new http_1.RequestOptions({ body: body }));
                    return new Promise(function (resolve, reject) {
                        _this._http.request('', options).subscribe(function (response) {
                            var data = (response.text() === '') ? {} : response.json();
                            resolve(data);
                        }, function (response) {
                            var data = (response.text() === '') ? {} : response.json();
                            reject(data);
                        });
                    });
                };
                RemoteService.prototype._convertToBody = function (params) {
                    var body = '';
                    for (var key in params) {
                        if (body !== '') {
                            body += '&';
                        }
                        body += key + '=' + params[key];
                    }
                    return body;
                };
                RemoteService.prototype._mergeParamsWithToken = function (params, token) {
                    var merged = {};
                    if (params) {
                        for (var i in params) {
                            merged[i] = params[i];
                        }
                    }
                    if (token) {
                        merged['token'] = token;
                    }
                    return merged;
                };
                RemoteService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [http_1.Http, (typeof (_a = typeof auth_token_service_1.AuthTokenService !== 'undefined' && auth_token_service_1.AuthTokenService) === 'function' && _a) || Object])
                ], RemoteService);
                return RemoteService;
                var _a;
            }());
            exports_1("RemoteService", RemoteService);
        }
    }
});
