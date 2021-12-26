System.register(['angular2/core', "angular2/src/platform/browser/browser_adapter"], function(exports_1, context_1) {
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
    var core_1, browser_adapter_1;
    var DomHelper;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (browser_adapter_1_1) {
                browser_adapter_1 = browser_adapter_1_1;
            }],
        execute: function() {
            //TODO: compare with http://blog.wearecolony.com/a-year-without-jquery/
            DomHelper = (function () {
                function DomHelper(_browserDomAdapter) {
                    this._browserDomAdapter = _browserDomAdapter;
                }
                DomHelper.prototype.getClosest = function (root, element, selector) {
                    do {
                        if (element === root) {
                            return null;
                        }
                        if (this.matches(element, selector)) {
                            return element;
                        }
                        element = this._browserDomAdapter.parentElement(element);
                    } while (element);
                    return null;
                };
                DomHelper.prototype.matches = function (element, selector) {
                    var matches = document.querySelectorAll(selector);
                    return Array.prototype.some.call(matches, function (e) { return e === element; });
                };
                DomHelper.prototype.on = function (root, eventName, selector, action) {
                    var _this = this;
                    this._browserDomAdapter.on(root, eventName, function (event) {
                        var element = _this.getClosest(root, event.target, selector);
                        if (element) {
                            action(element, event);
                        }
                    });
                };
                DomHelper = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [browser_adapter_1.BrowserDomAdapter])
                ], DomHelper);
                return DomHelper;
            }());
            exports_1("DomHelper", DomHelper);
        }
    }
});
