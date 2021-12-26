System.register(['angular2/core', 'angular2/router', 'app/menu/shared/games-list/games-list.component'], function(exports_1, context_1) {
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
    var core_1, router_1, games_list_component_1;
    var ContinueGamePageComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (games_list_component_1_1) {
                games_list_component_1 = games_list_component_1_1;
            }],
        execute: function() {
            ContinueGamePageComponent = (function () {
                function ContinueGamePageComponent() {
                }
                ContinueGamePageComponent = __decorate([
                    core_1.Component({
                        templateUrl: 'app/menu/continue-game-page/continue-game-page.component.html',
                        directives: [
                            games_list_component_1.GamesListComponent,
                            router_1.RouterLink
                        ]
                    }), 
                    __metadata('design:paramtypes', [])
                ], ContinueGamePageComponent);
                return ContinueGamePageComponent;
            }());
            exports_1("ContinueGamePageComponent", ContinueGamePageComponent);
        }
    }
});