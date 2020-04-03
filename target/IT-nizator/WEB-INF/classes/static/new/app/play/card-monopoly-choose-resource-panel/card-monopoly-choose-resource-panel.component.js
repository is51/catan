System.register(['angular2/core', 'app/shared/modal-window/modal-window.service', 'app/shared/modal-window/modal-window.directive', 'app/shared/modal-window/modal-window-close.directive', 'app/play/shared/choose-resources/choose-resources.component'], function(exports_1, context_1) {
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
    var core_1, modal_window_service_1, modal_window_directive_1, modal_window_close_directive_1, choose_resources_component_1;
    var PANEL_ID, CardMonopolyChooseResourcePanelComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (modal_window_service_1_1) {
                modal_window_service_1 = modal_window_service_1_1;
            },
            function (modal_window_directive_1_1) {
                modal_window_directive_1 = modal_window_directive_1_1;
            },
            function (modal_window_close_directive_1_1) {
                modal_window_close_directive_1 = modal_window_close_directive_1_1;
            },
            function (choose_resources_component_1_1) {
                choose_resources_component_1 = choose_resources_component_1_1;
            }],
        execute: function() {
            PANEL_ID = 'CARD_MONOPOLY';
            CardMonopolyChooseResourcePanelComponent = (function () {
                function CardMonopolyChooseResourcePanelComponent(_modalWindow) {
                    this._modalWindow = _modalWindow;
                    this.modalWindowId = PANEL_ID;
                }
                CardMonopolyChooseResourcePanelComponent.prototype.isModalWindowVisible = function () {
                    return this._modalWindow.isVisible(this.modalWindowId);
                };
                CardMonopolyChooseResourcePanelComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-card-monopoly-choose-resource-panel',
                        templateUrl: 'app/play/card-monopoly-choose-resource-panel/card-monopoly-choose-resource-panel.component.html',
                        styleUrls: [
                            'app/play/card-monopoly-choose-resource-panel/card-monopoly-choose-resource-panel.component.css',
                            'app/shared/modal-window/modal-window.directive.css'
                        ],
                        directives: [
                            modal_window_directive_1.ModalWindowDirective,
                            modal_window_close_directive_1.ModalWindowCloseDirective,
                            choose_resources_component_1.ChooseResourcesComponent
                        ],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof modal_window_service_1.ModalWindowService !== 'undefined' && modal_window_service_1.ModalWindowService) === 'function' && _a) || Object])
                ], CardMonopolyChooseResourcePanelComponent);
                return CardMonopolyChooseResourcePanelComponent;
                var _a;
            }());
            exports_1("CardMonopolyChooseResourcePanelComponent", CardMonopolyChooseResourcePanelComponent);
        }
    }
});
