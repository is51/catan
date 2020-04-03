System.register(['angular2/core', 'angular2/http', 'rxjs/Rx'], function(exports_1, context_1) {
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
    var core_1, http_1, Rx_1;
    var TEMPLATES_PATH, TEMPLATES_SUFFIX, TEMPLATES, TemplatesService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (Rx_1_1) {
                Rx_1 = Rx_1_1;
            }],
        execute: function() {
            //TODO: defs (filers, gradients) should be loaded separately and only once - on first loading of template
            TEMPLATES_PATH = '/new/resources/templates/'; // Depends on #rootpath
            TEMPLATES_SUFFIX = '.template.xml';
            TEMPLATES = [
                'hex-bg',
                'hex-bg-edge-top-right',
                'hex-bg-edge-top-left',
                'hex-bg-edge-bottom-right',
                'hex-bg-edge-bottom-left',
                'hex-bg-edge-left',
                'hex-bg-edge-right',
                'hex-bg-node-bottom',
                'hex-bg-node-bottom-right',
                'hex-bg-node-bottom-left',
                'hex-bg-node-top',
                'hex-bg-node-top-right',
                'hex-bg-node-top-left',
                'hex-bg-node-right-top',
                'hex-bg-node-right-bottom',
                'hex-bg-node-left-top',
                'hex-bg-node-left-bottom',
                'hex-dice',
                'hex-brick',
                'hex-wood',
                'hex-sheep',
                'hex-wheat',
                'hex-stone',
                'hex-empty',
                'icon-brick',
                'icon-wood',
                'icon-sheep',
                'icon-wheat',
                'icon-stone',
                'icon-any',
                'robber',
                'blank-node',
                'settlement',
                'city',
                'port',
                'port-horizontal',
                'port-vertical',
                'road-vertical',
                'road-horizontal',
                'blank-edge-vertical',
                'blank-edge-horizontal',
                'clouds',
                'map-bottom'
            ];
            TemplatesService = (function () {
                function TemplatesService(_http) {
                    this._http = _http;
                    this._templates = new Map();
                }
                TemplatesService.prototype.load = function () {
                    var _this = this;
                    return new Promise(function (resolve, reject) {
                        var requests = TEMPLATES.map(function (templateName) {
                            return _this._http.get(TEMPLATES_PATH + templateName + TEMPLATES_SUFFIX);
                        });
                        Rx_1.Observable.forkJoin(requests).subscribe(function (responses) {
                            responses.forEach(function (response, i) {
                                _this._templates.set(TEMPLATES[i], response.text());
                            });
                            resolve();
                        }, function () { return reject(); });
                    });
                };
                TemplatesService.prototype.get = function (id, replace) {
                    var template = this._templates.get(id);
                    if (!template) {
                        return null;
                    }
                    if (replace) {
                        for (var i in replace) {
                            template = template.replace(new RegExp('{' + i + '}', 'g'), replace[i]);
                        }
                    }
                    return template;
                };
                TemplatesService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [http_1.Http])
                ], TemplatesService);
                return TemplatesService;
            }());
            exports_1("TemplatesService", TemplatesService);
        }
    }
});
