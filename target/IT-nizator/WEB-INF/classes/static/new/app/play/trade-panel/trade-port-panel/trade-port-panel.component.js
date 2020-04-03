System.register(['angular2/core', 'app/play/shared/choose-resources/choose-resources.component', 'app/shared/services/auth/auth-user.service'], function(exports_1, context_1) {
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
    var core_1, choose_resources_component_1, auth_user_service_1;
    var TradePortPanelComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (choose_resources_component_1_1) {
                choose_resources_component_1 = choose_resources_component_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            }],
        execute: function() {
            TradePortPanelComponent = (function () {
                function TradePortPanelComponent(_authUser) {
                    this._authUser = _authUser;
                }
                TradePortPanelComponent.prototype.ngOnInit = function () {
                    this.ratio = this.game
                        .getCurrentPlayer(this._authUser.get())
                        .availableActions.getParams("TRADE_PORT");
                };
                TradePortPanelComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-trade-port-panel',
                        templateUrl: 'app/play/trade-panel/trade-port-panel/trade-port-panel.component.html',
                        styleUrls: ['app/play/trade-panel/trade-port-panel/trade-port-panel.component.css'],
                        directives: [choose_resources_component_1.ChooseResourcesComponent],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object])
                ], TradePortPanelComponent);
                return TradePortPanelComponent;
                var _a;
            }());
            exports_1("TradePortPanelComponent", TradePortPanelComponent);
        }
    }
});
