System.register(['angular2/core', 'angular2/router', 'app/shared/services/auth/auth-user.service', './logout-button.directive'], function(exports_1, context_1) {
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
    var core_1, router_1, auth_user_service_1, logout_button_directive_1;
    var StartPageComponent;
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
            function (logout_button_directive_1_1) {
                logout_button_directive_1 = logout_button_directive_1_1;
            }],
        execute: function() {
            StartPageComponent = (function () {
                function StartPageComponent(authUser) {
                    this.authUser = authUser;
                }
                StartPageComponent = __decorate([
                    core_1.Component({
                        templateUrl: 'app/menu/start-page/start-page.component.html',
                        directives: [
                            router_1.RouterLink,
                            logout_button_directive_1.LogoutButtonDirective
                        ]
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object])
                ], StartPageComponent);
                return StartPageComponent;
                var _a;
            }());
            exports_1("StartPageComponent", StartPageComponent);
        }
    }
});
