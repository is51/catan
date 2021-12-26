System.register(['angular2/core', 'angular2/router', 'app/shared/services/auth/auth-user.service', 'app/shared/services/remote/remote.service', 'app/shared/services/route-data/route-data.service', 'app/shared/alert/alert.service'], function(exports_1, context_1) {
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
    var core_1, router_1, auth_user_service_1, remote_service_1, route_data_service_1, alert_service_1;
    var JoinPrivateGameFormComponent;
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
            },
            function (alert_service_1_1) {
                alert_service_1 = alert_service_1_1;
            }],
        execute: function() {
            JoinPrivateGameFormComponent = (function () {
                function JoinPrivateGameFormComponent(_remote, _authUser, _router, _routeData, _alert) {
                    this._remote = _remote;
                    this._authUser = _authUser;
                    this._router = _router;
                    this._routeData = _routeData;
                    this._alert = _alert;
                }
                JoinPrivateGameFormComponent.prototype.ngOnInit = function () {
                    if (!this.data) {
                        this._setDefaultData();
                    }
                };
                JoinPrivateGameFormComponent.prototype._setDefaultData = function () {
                    this.data = {
                        privateCode: ''
                    };
                };
                JoinPrivateGameFormComponent.prototype.submit = function () {
                    var _this = this;
                    if (this._authUser.isAuthorized()) {
                        this._joinPrivateGame();
                    }
                    if (this._authUser.isNotAuthorized()) {
                        this._routeData.put({
                            onRegister: function () {
                                _this._goJoinPrivateGamePage();
                                _this._joinPrivateGame();
                            },
                            onBack: function () { return _this._goJoinPrivateGamePage(); }
                        });
                        this._router.navigate(['RegisterGuestPage']);
                    }
                };
                JoinPrivateGameFormComponent.prototype._joinPrivateGame = function () {
                    var _this = this;
                    this._remote.request('game.joinPrivate', { 'privateCode': this.data.privateCode })
                        .then(function (data) { return _this._router.navigate(['GamePage', { gameId: data.gameId }]); })
                        .catch(function (data) { return _this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')); });
                };
                JoinPrivateGameFormComponent.prototype._goJoinPrivateGamePage = function () {
                    this._routeData.put({ formData: this.data });
                    this._router.navigate(['JoinPrivateGamePage']);
                };
                JoinPrivateGameFormComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-join-private-game-form',
                        templateUrl: 'app/menu/join-private-game-page/join-private-game-form/join-private-game-form.component.html',
                        inputs: ['data']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof remote_service_1.RemoteService !== 'undefined' && remote_service_1.RemoteService) === 'function' && _a) || Object, (typeof (_b = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _b) || Object, router_1.Router, (typeof (_c = typeof route_data_service_1.RouteDataService !== 'undefined' && route_data_service_1.RouteDataService) === 'function' && _c) || Object, (typeof (_d = typeof alert_service_1.AlertService !== 'undefined' && alert_service_1.AlertService) === 'function' && _d) || Object])
                ], JoinPrivateGameFormComponent);
                return JoinPrivateGameFormComponent;
                var _a, _b, _c, _d;
            }());
            exports_1("JoinPrivateGameFormComponent", JoinPrivateGameFormComponent);
        }
    }
});
