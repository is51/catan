System.register(['angular2/core', 'app/play/shared/services/select.service'], function(exports_1, context_1) {
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
    var core_1, select_service_1;
    var ChooseResourcesCancelDirective;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (select_service_1_1) {
                select_service_1 = select_service_1_1;
            }],
        execute: function() {
            ChooseResourcesCancelDirective = (function () {
                function ChooseResourcesCancelDirective(_select) {
                    this._select = _select;
                }
                ChooseResourcesCancelDirective.prototype.onClick = function () {
                    this._select.cancelRequestSelection(this.type);
                };
                ChooseResourcesCancelDirective = __decorate([
                    core_1.Directive({
                        selector: '[ct-choose-resources-cancel]',
                        inputs: ['type'],
                        host: {
                            '(click)': 'onClick($event)',
                        }
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof select_service_1.SelectService !== 'undefined' && select_service_1.SelectService) === 'function' && _a) || Object])
                ], ChooseResourcesCancelDirective);
                return ChooseResourcesCancelDirective;
                var _a;
            }());
            exports_1("ChooseResourcesCancelDirective", ChooseResourcesCancelDirective);
        }
    }
});
