System.register(['angular2/core', 'angular2/router', 'app/shared/services/auth/auth.service', 'app/shared/services/remote/remote.service', 'app/shared/alert/alert.service'], function(exports_1, context_1) {
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
    var core_1, router_1, auth_service_1, remote_service_1, alert_service_1;
    var RegisterFormComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (auth_service_1_1) {
                auth_service_1 = auth_service_1_1;
            },
            function (remote_service_1_1) {
                remote_service_1 = remote_service_1_1;
            },
            function (alert_service_1_1) {
                alert_service_1 = alert_service_1_1;
            }],
        execute: function() {
            RegisterFormComponent = (function () {
                function RegisterFormComponent(_auth, _remote, _router, _alert) {
                    this._auth = _auth;
                    this._remote = _remote;
                    this._router = _router;
                    this._alert = _alert;
                    this.data = {
                        username: '',
                        password: ''
                    };
                }
                RegisterFormComponent.prototype.submit = function () {
                    var _this = this;
                    this._remote.request('auth.register', {
                        username: this.data.username,
                        password: this.data.password
                    })
                        .then(function () {
                        _this._auth.login(_this.data.username, _this.data.password)
                            .then(function () {
                            if (_this.onRegister) {
                                _this.onRegister();
                            }
                            else {
                                _this._router.navigate(['StartPage']);
                            }
                        }, function (data) {
                            _this._alert.message('Login error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                        });
                    }, function (data) {
                        _this._alert.message('Registration error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                    });
                };
                RegisterFormComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-register-form',
                        templateUrl: 'app/menu/register-page/register-form/register-form.component.html',
                        inputs: ['onRegister']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_service_1.AuthService !== 'undefined' && auth_service_1.AuthService) === 'function' && _a) || Object, (typeof (_b = typeof remote_service_1.RemoteService !== 'undefined' && remote_service_1.RemoteService) === 'function' && _b) || Object, router_1.Router, (typeof (_c = typeof alert_service_1.AlertService !== 'undefined' && alert_service_1.AlertService) === 'function' && _c) || Object])
                ], RegisterFormComponent);
                return RegisterFormComponent;
                var _a, _b, _c;
            }());
            exports_1("RegisterFormComponent", RegisterFormComponent);
        }
    }
});
