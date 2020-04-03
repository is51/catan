System.register(['angular2/core', 'app/shared/services/auth/auth-token.service', 'app/shared/services/remote/remote.service', 'app/shared/domain/user'], function(exports_1, context_1) {
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
    var core_1, auth_token_service_1, remote_service_1, user_1;
    var Status, AuthUserService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (auth_token_service_1_1) {
                auth_token_service_1 = auth_token_service_1_1;
            },
            function (remote_service_1_1) {
                remote_service_1 = remote_service_1_1;
            },
            function (user_1_1) {
                user_1 = user_1_1;
            }],
        execute: function() {
            (function (Status) {
                Status[Status["PENDING"] = 0] = "PENDING";
                Status[Status["NOT_AUTHORIZED"] = 1] = "NOT_AUTHORIZED";
                Status[Status["AUTHORIZED"] = 2] = "AUTHORIZED";
            })(Status || (Status = {}));
            AuthUserService = (function () {
                function AuthUserService(_authToken, _remote) {
                    this._authToken = _authToken;
                    this._remote = _remote;
                    this._status = Status.PENDING;
                }
                AuthUserService.prototype.isAuthorized = function () {
                    return this._status === Status.AUTHORIZED;
                };
                AuthUserService.prototype.isNotAuthorized = function () {
                    return this._status === Status.NOT_AUTHORIZED;
                };
                AuthUserService.prototype.isPending = function () {
                    return this._status === Status.PENDING;
                };
                AuthUserService.prototype.isTypeGuest = function () {
                    return this._status === Status.AUTHORIZED && this._details.guest;
                };
                AuthUserService.prototype.get = function () {
                    return this._details;
                };
                AuthUserService.prototype.load = function () {
                    var _this = this;
                    return new Promise(function (resolve, reject) {
                        if (_this._authToken.get()) {
                            _this._remote.request('auth.details').then(function (data) {
                                _this._details = new user_1.User(data);
                                _this._status = Status.AUTHORIZED;
                                resolve();
                            }, function () {
                                _this._authToken.delete();
                                _this._status = Status.NOT_AUTHORIZED;
                                reject();
                            });
                        }
                        else {
                            _this._status = Status.NOT_AUTHORIZED;
                            reject();
                        }
                    });
                };
                AuthUserService.prototype.setToGuest = function () {
                    this._status = Status.NOT_AUTHORIZED;
                    this._details = null;
                };
                AuthUserService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_token_service_1.AuthTokenService !== 'undefined' && auth_token_service_1.AuthTokenService) === 'function' && _a) || Object, (typeof (_b = typeof remote_service_1.RemoteService !== 'undefined' && remote_service_1.RemoteService) === 'function' && _b) || Object])
                ], AuthUserService);
                return AuthUserService;
                var _a, _b;
            }());
            exports_1("AuthUserService", AuthUserService);
        }
    }
});
