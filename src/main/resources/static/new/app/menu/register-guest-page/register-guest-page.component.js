System.register(['angular2/core', 'angular2/router', 'app/shared/services/route-data/route-data.service', './register-guest-form/register-guest-form.component'], function(exports_1, context_1) {
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
    var core_1, router_1, route_data_service_1, register_guest_form_component_1;
    var RegisterGuestPageComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (route_data_service_1_1) {
                route_data_service_1 = route_data_service_1_1;
            },
            function (register_guest_form_component_1_1) {
                register_guest_form_component_1 = register_guest_form_component_1_1;
            }],
        execute: function() {
            RegisterGuestPageComponent = (function () {
                function RegisterGuestPageComponent(_routeData, _router) {
                    this._routeData = _routeData;
                    this._router = _router;
                    this._routeData.fetch();
                    this.formOnRegister = this._routeData.get('onRegister');
                    this.onBack = this._routeData.get('onBack');
                }
                RegisterGuestPageComponent.prototype.goBack = function () {
                    if (this.onBack) {
                        this.onBack();
                    }
                    else {
                        this._router.navigate(['StartPage']);
                    }
                };
                RegisterGuestPageComponent.prototype.login = function () {
                    this._routeData.put({
                        onLogin: this.formOnRegister,
                        onBack: this.onBack
                    });
                    this._router.navigate(['LoginPage']);
                };
                RegisterGuestPageComponent.prototype.registerRegularUser = function () {
                    this._routeData.put({
                        onRegister: this.formOnRegister,
                        onBack: this.onBack
                    });
                    this._router.navigate(['RegisterPage']);
                };
                RegisterGuestPageComponent = __decorate([
                    core_1.Component({
                        templateUrl: 'app/menu/register-guest-page/register-guest-page.component.html',
                        directives: [register_guest_form_component_1.RegisterGuestFormComponent]
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof route_data_service_1.RouteDataService !== 'undefined' && route_data_service_1.RouteDataService) === 'function' && _a) || Object, router_1.Router])
                ], RegisterGuestPageComponent);
                return RegisterGuestPageComponent;
                var _a;
            }());
            exports_1("RegisterGuestPageComponent", RegisterGuestPageComponent);
        }
    }
});
