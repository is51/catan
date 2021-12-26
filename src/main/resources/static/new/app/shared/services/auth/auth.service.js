System.register(['angular2/core', 'app/shared/services/auth/auth-token.service', 'app/shared/services/remote/remote.service', 'app/shared/services/auth/auth-user.service'], function(exports_1, context_1) {
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
    var core_1, auth_token_service_1, remote_service_1, auth_user_service_1;
    var AuthService;
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
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            }],
        execute: function() {
            AuthService = (function () {
                function AuthService(_authToken, _remote, _authUser) {
                    this._authToken = _authToken;
                    this._remote = _remote;
                    this._authUser = _authUser;
                }
                AuthService.prototype.login = function (username, password) {
                    var _this = this;
                    return new Promise(function (resolve, reject) {
                        _this._remote.request('auth.login', {
                            username: username,
                            password: password
                        })
                            .then(function (data) {
                            if (data && data.token) {
                                _this._authToken.set(data.token);
                                _this._authUser.load();
                                resolve(data);
                            }
                            else {
                                reject(data);
                            }
                        })
                            .catch(function (data) { return reject(data); });
                    });
                };
                AuthService.prototype.logout = function () {
                    var _this = this;
                    return new Promise(function (resolve, reject) {
                        _this._remote.request('auth.logout')
                            .then(function (data) {
                            _this._authToken.delete();
                            _this._authUser.setToGuest();
                            resolve(data);
                        })
                            .catch(function (data) { return reject(data); });
                    });
                };
                AuthService.prototype.registerAndLoginGuest = function (username) {
                    var _this = this;
                    return new Promise(function (resolve, reject) {
                        _this._remote.request('auth.registerAndLoginGuest', { username: username })
                            .then(function (data) {
                            if (data.token) {
                                _this._authToken.set(data.token);
                                _this._authUser.load();
                                resolve(data);
                            }
                            else {
                                reject(data);
                            }
                        })
                            .catch(function (data) { return reject(data); });
                    });
                };
                AuthService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_token_service_1.AuthTokenService !== 'undefined' && auth_token_service_1.AuthTokenService) === 'function' && _a) || Object, (typeof (_b = typeof remote_service_1.RemoteService !== 'undefined' && remote_service_1.RemoteService) === 'function' && _b) || Object, (typeof (_c = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _c) || Object])
                ], AuthService);
                return AuthService;
                var _a, _b, _c;
            }());
            exports_1("AuthService", AuthService);
        }
    }
});
