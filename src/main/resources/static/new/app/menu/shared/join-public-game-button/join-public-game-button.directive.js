System.register(['angular2/core', 'angular2/router', 'app/shared/services/remote/remote.service', 'app/shared/services/auth/auth-user.service', 'app/shared/alert/alert.service'], function(exports_1, context_1) {
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
    var core_1, router_1, remote_service_1, auth_user_service_1, alert_service_1;
    var JoinPublicGameButtonDirective;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (remote_service_1_1) {
                remote_service_1 = remote_service_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            },
            function (alert_service_1_1) {
                alert_service_1 = alert_service_1_1;
            }],
        execute: function() {
            JoinPublicGameButtonDirective = (function () {
                function JoinPublicGameButtonDirective(_remote, _authUser, _router, _alert) {
                    this._remote = _remote;
                    this._authUser = _authUser;
                    this._router = _router;
                    this._alert = _alert;
                }
                JoinPublicGameButtonDirective.prototype.onClick = function () {
                    if (this._authUser.isAuthorized()) {
                        this._joinPublicGame();
                    }
                    if (this._authUser.isNotAuthorized()) {
                        /*$state.go('registerGuest', {
                            onRegister: function() {
                                goBack();
                                joinPublicGame();
                            },
                            onBack: goBack
                        });*/
                        this._router.navigate(['RegisterGuestPage']);
                    }
                };
                /*function goBack() {
                    $state.go('joinPublicGame');
                }*/
                JoinPublicGameButtonDirective.prototype._joinPublicGame = function () {
                    var _this = this;
                    this._remote.request('game.joinPublic', { gameId: this.game.getId() })
                        .then(function () { return _this._router.navigate(['GamePage', { gameId: _this.game.getId() }]); })
                        .catch(function (data) { return _this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')); });
                };
                JoinPublicGameButtonDirective = __decorate([
                    core_1.Directive({
                        selector: '[ct-join-public-game-button]',
                        host: {
                            '(click)': 'onClick($event)',
                        },
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof remote_service_1.RemoteService !== 'undefined' && remote_service_1.RemoteService) === 'function' && _a) || Object, (typeof (_b = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _b) || Object, router_1.Router, (typeof (_c = typeof alert_service_1.AlertService !== 'undefined' && alert_service_1.AlertService) === 'function' && _c) || Object])
                ], JoinPublicGameButtonDirective);
                return JoinPublicGameButtonDirective;
                var _a, _b, _c;
            }());
            exports_1("JoinPublicGameButtonDirective", JoinPublicGameButtonDirective);
        }
    }
});
