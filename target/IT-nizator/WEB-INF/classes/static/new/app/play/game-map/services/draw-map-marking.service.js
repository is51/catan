System.register(['angular2/core', "angular2/src/platform/browser/browser_adapter", 'app/play/shared/services/marking.service'], function(exports_1, context_1) {
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
    var core_1, browser_adapter_1, marking_service_1;
    var MARKED_MANY_COUNT, DrawMapMarkingService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (browser_adapter_1_1) {
                browser_adapter_1 = browser_adapter_1_1;
            },
            function (marking_service_1_1) {
                marking_service_1 = marking_service_1_1;
            }],
        execute: function() {
            MARKED_MANY_COUNT = 7;
            DrawMapMarkingService = (function () {
                function DrawMapMarkingService(_marking, _dom) {
                    this._marking = _marking;
                    this._dom = _dom;
                }
                DrawMapMarkingService.prototype.updateByType = function (element, type, typeClass) {
                    var _this = this;
                    var previousMarked = this._dom.querySelectorAll(element, '.' + typeClass + '[marked]');
                    for (var _i = 0, previousMarked_1 = previousMarked; _i < previousMarked_1.length; _i++) {
                        var elem = previousMarked_1[_i];
                        this._dom.removeAttribute(elem, 'marked');
                        this._dom.removeAttribute(elem, 'marked-many');
                        this._dom.removeAttribute(elem, 'player-color');
                    }
                    var marking = this._marking.get(type);
                    if (marking) {
                        marking.ids.forEach(function (id) {
                            var elem = _this._dom.querySelector(element, '.' + typeClass + '[' + typeClass + '-id="' + id + '"]');
                            _this._dom.setAttribute(elem, 'marked', "true");
                            if (marking.ids.length >= MARKED_MANY_COUNT) {
                                _this._dom.setAttribute(elem, 'marked-many', "true");
                            }
                            if (marking.player) {
                                _this._dom.setAttribute(elem, 'player-color', marking.player.colorId);
                            }
                        });
                    }
                };
                DrawMapMarkingService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof marking_service_1.MarkingService !== 'undefined' && marking_service_1.MarkingService) === 'function' && _a) || Object, browser_adapter_1.BrowserDomAdapter])
                ], DrawMapMarkingService);
                return DrawMapMarkingService;
                var _a;
            }());
            exports_1("DrawMapMarkingService", DrawMapMarkingService);
        }
    }
});
