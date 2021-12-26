System.register(['angular2/core', 'app/play/shared/services/select.service', 'app/shared/modal-window/modal-window.service', 'app/shared/modal-window/modal-window.directive', 'app/play/shared/choose-resources/choose-resources.component'], function(exports_1, context_1) {
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
    var core_1, select_service_1, modal_window_service_1, modal_window_directive_1, choose_resources_component_1;
    var PANEL_ID, KickOffResourcesPanelComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (select_service_1_1) {
                select_service_1 = select_service_1_1;
            },
            function (modal_window_service_1_1) {
                modal_window_service_1 = modal_window_service_1_1;
            },
            function (modal_window_directive_1_1) {
                modal_window_directive_1 = modal_window_directive_1_1;
            },
            function (choose_resources_component_1_1) {
                choose_resources_component_1 = choose_resources_component_1_1;
            }],
        execute: function() {
            PANEL_ID = 'KICK_OFF_RESOURCES';
            KickOffResourcesPanelComponent = (function () {
                function KickOffResourcesPanelComponent(_modalWindow, _select) {
                    this._modalWindow = _modalWindow;
                    this._select = _select;
                    this.modalWindowId = PANEL_ID;
                }
                KickOffResourcesPanelComponent.prototype.isModalWindowVisible = function () {
                    return this._modalWindow.isVisible(this.modalWindowId);
                };
                KickOffResourcesPanelComponent.prototype.cancel = function () {
                    this._select.cancelRequestSelection(PANEL_ID);
                };
                KickOffResourcesPanelComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-kick-off-resources-panel',
                        templateUrl: 'app/play/kick-off-resources-panel/kick-off-resources-panel.component.html',
                        styleUrls: [
                            'app/play/kick-off-resources-panel/kick-off-resources-panel.component.css',
                            'app/shared/modal-window/modal-window.directive.css'
                        ],
                        directives: [
                            modal_window_directive_1.ModalWindowDirective,
                            choose_resources_component_1.ChooseResourcesComponent
                        ],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof modal_window_service_1.ModalWindowService !== 'undefined' && modal_window_service_1.ModalWindowService) === 'function' && _a) || Object, (typeof (_b = typeof select_service_1.SelectService !== 'undefined' && select_service_1.SelectService) === 'function' && _b) || Object])
                ], KickOffResourcesPanelComponent);
                return KickOffResourcesPanelComponent;
                var _a, _b;
            }());
            exports_1("KickOffResourcesPanelComponent", KickOffResourcesPanelComponent);
        }
    }
});
