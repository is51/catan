System.register(['angular2/core', 'app/shared/services/auth/auth-user.service', 'app/shared/modal-window/modal-window.directive', 'app/shared/modal-window/modal-window-close.directive'], function(exports_1, context_1) {
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
    var core_1, auth_user_service_1, modal_window_directive_1, modal_window_close_directive_1;
    var LogPanelComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            },
            function (modal_window_directive_1_1) {
                modal_window_directive_1 = modal_window_directive_1_1;
            },
            function (modal_window_close_directive_1_1) {
                modal_window_close_directive_1 = modal_window_close_directive_1_1;
            }],
        execute: function() {
            LogPanelComponent = (function () {
                function LogPanelComponent(_authUser) {
                    var _this = this;
                    this._authUser = _authUser;
                    this.onShow = function () {
                        _this._updateLog();
                    };
                }
                //TODO: 'ngDoCheck' = bad performance (use subscribe)
                LogPanelComponent.prototype.ngDoCheck = function () {
                    this._updateLog();
                };
                LogPanelComponent.prototype._updateLog = function () {
                    this.log = Object.assign([], this.game.getCurrentPlayer(this._authUser.get()).log);
                    this.log.reverse();
                    this.count = this.log.length;
                };
                LogPanelComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-log-panel',
                        templateUrl: 'app/play/log-panel/log-panel.component.html',
                        styleUrls: [
                            'app/play/log-panel/log-panel.component.css',
                            'app/shared/modal-window/modal-window.directive.css'
                        ],
                        directives: [
                            modal_window_directive_1.ModalWindowDirective,
                            modal_window_close_directive_1.ModalWindowCloseDirective,
                        ],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object])
                ], LogPanelComponent);
                return LogPanelComponent;
                var _a;
            }());
            exports_1("LogPanelComponent", LogPanelComponent);
        }
    }
});
