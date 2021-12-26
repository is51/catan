System.register(['angular2/core', 'angular2/router', 'app/shared/services/auth/auth.service', 'app/shared/alert/alert.service'], function(exports_1, context_1) {
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
    var core_1, router_1, auth_service_1, alert_service_1;
    var LoginFormComponent;
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
            function (alert_service_1_1) {
                alert_service_1 = alert_service_1_1;
            }],
        execute: function() {
            LoginFormComponent = (function () {
                function LoginFormComponent(_auth, _router, _alert) {
                    this._auth = _auth;
                    this._router = _router;
                    this._alert = _alert;
                    this.data = {
                        username: '',
                        password: ''
                    };
                }
                LoginFormComponent.prototype.submit = function () {
                    var _this = this;
                    this._auth.login(this.data.username, this.data.password)
                        .then(function () {
                        if (_this.onLogin) {
                            _this.onLogin();
                        }
                        else {
                            _this._router.navigate(['StartPage']);
                        }
                    })
                        .catch(function (data) { return _this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')); });
                };
                LoginFormComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-login-form',
                        templateUrl: 'app/menu/login-page/login-form/login-form.component.html',
                        inputs: ['onLogin']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_service_1.AuthService !== 'undefined' && auth_service_1.AuthService) === 'function' && _a) || Object, router_1.Router, (typeof (_b = typeof alert_service_1.AlertService !== 'undefined' && alert_service_1.AlertService) === 'function' && _b) || Object])
                ], LoginFormComponent);
                return LoginFormComponent;
                var _a, _b;
            }());
            exports_1("LoginFormComponent", LoginFormComponent);
        }
    }
});
