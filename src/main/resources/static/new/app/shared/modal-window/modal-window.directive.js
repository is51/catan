System.register(['angular2/core', 'app/shared/modal-window/modal-window.service'], function(exports_1, context_1) {
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
    var core_1, modal_window_service_1;
    var ModalWindowDirective;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (modal_window_service_1_1) {
                modal_window_service_1 = modal_window_service_1_1;
            }],
        execute: function() {
            ModalWindowDirective = (function () {
                function ModalWindowDirective(_modalWindowService) {
                    this._modalWindowService = _modalWindowService;
                }
                ModalWindowDirective.prototype.ngOnInit = function () {
                    this._modalWindowService.onShow(this.modalWindowId, this.onShow);
                    this._modalWindowService.onHide(this.modalWindowId, this.onHide);
                    if (this._modalWindowService.isVisible(this.modalWindowId) && this.onShow) {
                        this.onShow();
                    }
                };
                ModalWindowDirective.prototype.isHidden = function () {
                    return this._modalWindowService.isHidden(this.modalWindowId);
                };
                ModalWindowDirective = __decorate([
                    core_1.Directive({
                        selector: 'ct-modal-window',
                        inputs: [
                            'modalWindowId',
                            'onShow',
                            'onHide'
                        ],
                        host: {
                            '[hidden]': 'isHidden()'
                        },
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof modal_window_service_1.ModalWindowService !== 'undefined' && modal_window_service_1.ModalWindowService) === 'function' && _a) || Object])
                ], ModalWindowDirective);
                return ModalWindowDirective;
                var _a;
            }());
            exports_1("ModalWindowDirective", ModalWindowDirective);
        }
    }
});
