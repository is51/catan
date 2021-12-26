System.register(['angular2/core', 'app/shared/alert/alert.service', './alert.component'], function(exports_1, context_1) {
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
    var core_1, alert_service_1, alert_component_1;
    var AlertsComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (alert_service_1_1) {
                alert_service_1 = alert_service_1_1;
            },
            function (alert_component_1_1) {
                alert_component_1 = alert_component_1_1;
            }],
        execute: function() {
            AlertsComponent = (function () {
                function AlertsComponent(_alert, _loader, _element) {
                    this._alert = _alert;
                    this._loader = _loader;
                    this._element = _element;
                }
                AlertsComponent.prototype.ngOnInit = function () {
                    var _this = this;
                    this._alert.onMessage(function (text, resolve) { return _this._createWindow(text, resolve); });
                };
                AlertsComponent.prototype._createWindow = function (text, resolve) {
                    this._loader.loadIntoLocation(alert_component_1.AlertComponent, this._element, 'alerts')
                        .then(function (res) {
                        res.instance.setText(text);
                        res.instance.close = function () {
                            res.dispose();
                            resolve();
                        };
                    });
                };
                AlertsComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-alerts',
                        template: '<div #alerts></div>',
                        directives: [alert_component_1.AlertComponent]
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof alert_service_1.AlertService !== 'undefined' && alert_service_1.AlertService) === 'function' && _a) || Object, core_1.DynamicComponentLoader, core_1.ElementRef])
                ], AlertsComponent);
                return AlertsComponent;
                var _a;
            }());
            exports_1("AlertsComponent", AlertsComponent);
        }
    }
});
