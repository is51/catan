System.register(['angular2/core', 'angular2/router', 'app/shared/services/auth/auth-user.service', 'app/shared/services/remote/remote.service', 'app/shared/services/route-data/route-data.service'], function(exports_1, context_1) {
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
    var core_1, router_1, auth_user_service_1, remote_service_1, route_data_service_1;
    var CreateGameFormComponent;
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
            function (remote_service_1_1) {
                remote_service_1 = remote_service_1_1;
            },
            function (route_data_service_1_1) {
                route_data_service_1 = route_data_service_1_1;
            }],
        execute: function() {
            CreateGameFormComponent = (function () {
                function CreateGameFormComponent(_authUser, _remote, _router, _routeData) {
                    this._authUser = _authUser;
                    this._remote = _remote;
                    this._router = _router;
                    this._routeData = _routeData;
                    this.initialBuildingsSetIdValues = [
                        { value: 1, name: "2 settlements + 2 roads" },
                        { value: 2, name: "1 city + 2 settlements + 3 roads" }
                    ];
                }
                CreateGameFormComponent.prototype.ngOnInit = function () {
                    if (!this.data) {
                        this._setDefaultData();
                    }
                };
                CreateGameFormComponent.prototype._setDefaultData = function () {
                    this.data = {
                        privateGame: true,
                        targetVictoryPoints: 12,
                        initialBuildingsSetId: 1,
                    };
                };
                CreateGameFormComponent.prototype.submit = function () {
                    var _this = this;
                    if (this._authUser.isAuthorized()) {
                        if (!this.data.privateGame && this._authUser.isTypeGuest()) {
                            alert("Guest can't create public game. You should register. Registration from guest to regular user is NOT IMPLEMENTED");
                        }
                        else {
                            this._createGame();
                        }
                    }
                    if (this._authUser.isNotAuthorized()) {
                        if (this.data.privateGame) {
                            this._routeData.put({
                                onRegister: function () {
                                    _this._goCreateGamePage();
                                    _this._createGame();
                                },
                                onBack: function () { return _this._goCreateGamePage(); }
                            });
                            this._router.navigate(['RegisterGuestPage']);
                        }
                        else {
                            this._routeData.put({
                                onLogin: function () {
                                    _this._goCreateGamePage();
                                    _this._createGame();
                                },
                                onBack: function () { return _this._goCreateGamePage(); }
                            });
                            this._router.navigate(['LoginPage']);
                        }
                    }
                };
                CreateGameFormComponent.prototype._goCreateGamePage = function () {
                    this._routeData.put({ formData: this.data });
                    this._router.navigate(['CreateGamePage']);
                };
                CreateGameFormComponent.prototype._createGame = function () {
                    var _this = this;
                    this._remote.request('game.create', {
                        privateGame: this.data.privateGame,
                        targetVictoryPoints: this.data.targetVictoryPoints,
                        initialBuildingsSetId: this.data.initialBuildingsSetId
                    })
                        .then(function (data) { return _this._router.navigate(['GamePage', { gameId: data.gameId }]); })
                        .catch(function (data) { return alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')); });
                };
                CreateGameFormComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-create-game-form',
                        templateUrl: 'app/menu/create-game-page/create-game-form/create-game-form.component.html',
                        inputs: ['data']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object, (typeof (_b = typeof remote_service_1.RemoteService !== 'undefined' && remote_service_1.RemoteService) === 'function' && _b) || Object, router_1.Router, (typeof (_c = typeof route_data_service_1.RouteDataService !== 'undefined' && route_data_service_1.RouteDataService) === 'function' && _c) || Object])
                ], CreateGameFormComponent);
                return CreateGameFormComponent;
                var _a, _b, _c;
            }());
            exports_1("CreateGameFormComponent", CreateGameFormComponent);
        }
    }
});
