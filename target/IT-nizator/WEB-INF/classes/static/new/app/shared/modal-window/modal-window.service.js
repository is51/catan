System.register(['angular2/core'], function(exports_1, context_1) {
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
    var core_1;
    var ModalWindowService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            ModalWindowService = (function () {
                function ModalWindowService() {
                    this._modalWindows = new Map();
                }
                ModalWindowService.prototype._getOrCreate = function (id) {
                    if (!this._modalWindows.has(id)) {
                        this._modalWindows.set(id, {
                            isVisible: false,
                            onShow: null,
                            onHide: null
                        });
                    }
                    return this._modalWindows.get(id);
                };
                ModalWindowService.prototype.show = function (id) {
                    var modalWindow = this._getOrCreate(id);
                    modalWindow.isVisible = true;
                    if (modalWindow.onShow) {
                        modalWindow.onShow();
                    }
                };
                ModalWindowService.prototype.hide = function (id) {
                    var modalWindow = this._getOrCreate(id);
                    modalWindow.isVisible = false;
                    if (modalWindow.onHide) {
                        modalWindow.onHide();
                    }
                };
                ModalWindowService.prototype.toggle = function (id) {
                    if (this.isVisible(id)) {
                        this.hide(id);
                    }
                    else {
                        this.show(id);
                    }
                };
                ModalWindowService.prototype.isVisible = function (id) {
                    return this._modalWindows.has(id) && this._modalWindows.get(id).isVisible;
                };
                ModalWindowService.prototype.isHidden = function (id) {
                    return !this.isVisible(id);
                };
                ModalWindowService.prototype.onShow = function (id, onShow) {
                    this._getOrCreate(id).onShow = onShow;
                };
                ModalWindowService.prototype.onHide = function (id, onHide) {
                    this._getOrCreate(id).onHide = onHide;
                };
                ModalWindowService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [])
                ], ModalWindowService);
                return ModalWindowService;
            }());
            exports_1("ModalWindowService", ModalWindowService);
        }
    }
});
