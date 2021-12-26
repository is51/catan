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
    var SelectService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            SelectService = (function () {
                function SelectService() {
                    this._requests = new Map();
                }
                SelectService.prototype.requestSelection = function (type) {
                    var _this = this;
                    this.cancelRequestSelection(type);
                    return new Promise(function (resolve, reject) {
                        _this._requests.set(type, { resolve: resolve, reject: reject });
                    });
                };
                SelectService.prototype.select = function (type, objectId) {
                    if (this._requests.has(type)) {
                        this._requests.get(type).resolve(objectId);
                    }
                };
                SelectService.prototype.cancelRequestSelection = function (type) {
                    if (this._requests.has(type)) {
                        this._requests.get(type).reject("CANCELED");
                        this._requests.delete(type);
                    }
                };
                SelectService.prototype.cancelAllRequestSelections = function () {
                    var _this = this;
                    this._requests.forEach(function (callbacks, type) { return _this.cancelRequestSelection(type); });
                };
                SelectService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [])
                ], SelectService);
                return SelectService;
            }());
            exports_1("SelectService", SelectService);
        }
    }
});
