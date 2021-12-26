System.register(['angular2/core', "angular2/src/platform/browser/browser_adapter", '../services/draw-map.service'], function(exports_1, context_1) {
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
    var core_1, browser_adapter_1, draw_map_service_1;
    var HEX_ANIMATED_CLASS, HEX_ANIMATED_DURATION, ROBBER_ANIMATED_CLASS, ROBBER_ANIMATED_DURATION, MapAnimationService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (browser_adapter_1_1) {
                browser_adapter_1 = browser_adapter_1_1;
            },
            function (draw_map_service_1_1) {
                draw_map_service_1 = draw_map_service_1_1;
            }],
        execute: function() {
            HEX_ANIMATED_CLASS = 'hex-animated';
            HEX_ANIMATED_DURATION = 3000;
            ROBBER_ANIMATED_CLASS = 'robber-animated';
            ROBBER_ANIMATED_DURATION = 3000;
            MapAnimationService = (function () {
                function MapAnimationService(_dom, _drawMapService) {
                    this._dom = _dom;
                    this._drawMapService = _drawMapService;
                }
                MapAnimationService.prototype.hexDiceThrown = function (canvas, map, dice) {
                    var _this = this;
                    var hexesToAnimate = map.hexes
                        .filter(function (hex) { return hex.dice === dice && !hex.robbed; })
                        .map(function (hex) { return _this._dom.querySelector(canvas, _this._drawMapService.HEX_SELECTOR + '[hex-id="' + hex.id + '"]'); });
                    hexesToAnimate.forEach(function (element) {
                        _this._dom.addClass(element, HEX_ANIMATED_CLASS);
                    });
                    setTimeout(function () {
                        hexesToAnimate.forEach(function (element) {
                            _this._dom.removeClass(element, HEX_ANIMATED_CLASS);
                        });
                    }, HEX_ANIMATED_DURATION);
                };
                MapAnimationService.prototype.robberThrown = function (canvas) {
                    var _this = this;
                    var element = this._dom.querySelector(canvas, this._drawMapService.ROBBER_SELECTOR);
                    this._dom.addClass(element, ROBBER_ANIMATED_CLASS);
                    setTimeout(function () {
                        _this._dom.removeClass(element, ROBBER_ANIMATED_CLASS);
                    }, ROBBER_ANIMATED_DURATION);
                };
                MapAnimationService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [browser_adapter_1.BrowserDomAdapter, draw_map_service_1.DrawMapService])
                ], MapAnimationService);
                return MapAnimationService;
            }());
            exports_1("MapAnimationService", MapAnimationService);
        }
    }
});
