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
    var MarkingService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            MarkingService = (function () {
                function MarkingService() {
                    this._markings = new Map();
                    this._onUpdate = new Map();
                }
                MarkingService.prototype.mark = function (type, ids, player) {
                    var _this = this;
                    setTimeout(function () {
                        _this._markings.set(type, { ids: ids, player: player });
                        _this.triggerUpdate(type);
                    });
                };
                MarkingService.prototype.clear = function (type) {
                    this._markings.delete(type);
                    this.triggerUpdate(type);
                };
                MarkingService.prototype.get = function (type) {
                    return this._markings.get(type);
                };
                //TODO: try to replace with Subscribable (it's used in game-page.component)
                MarkingService.prototype.onUpdate = function (type, onUpdate) {
                    this._onUpdate.set(type, onUpdate);
                };
                MarkingService.prototype.cancelOnUpdate = function (type) {
                    this._onUpdate.delete(type);
                };
                MarkingService.prototype.triggerUpdate = function (type) {
                    var onUpdate = this._onUpdate.get(type);
                    if (onUpdate) {
                        onUpdate();
                    }
                };
                MarkingService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [])
                ], MarkingService);
                return MarkingService;
            }());
            exports_1("MarkingService", MarkingService);
        }
    }
});
