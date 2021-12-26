System.register(['angular2/core', "angular2/src/platform/browser/browser_adapter", 'app/play/shared/services/marking.service', 'app/play/shared/services/select.service', 'app/shared/services/dom/dom.helper', './services/draw-map.service', './services/draw-map-marking.service', './services/map-animation.service', './helpers/draw-map.helper'], function(exports_1, context_1) {
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
    var core_1, browser_adapter_1, marking_service_1, select_service_1, dom_helper_1, draw_map_service_1, draw_map_marking_service_1, map_animation_service_1, draw_map_helper_1;
    var CANVAS_PRESERVE_ASPECT_RATIO, GameMapComponent;
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
            },
            function (select_service_1_1) {
                select_service_1 = select_service_1_1;
            },
            function (dom_helper_1_1) {
                dom_helper_1 = dom_helper_1_1;
            },
            function (draw_map_service_1_1) {
                draw_map_service_1 = draw_map_service_1_1;
            },
            function (draw_map_marking_service_1_1) {
                draw_map_marking_service_1 = draw_map_marking_service_1_1;
            },
            function (map_animation_service_1_1) {
                map_animation_service_1 = map_animation_service_1_1;
            },
            function (draw_map_helper_1_1) {
                draw_map_helper_1 = draw_map_helper_1_1;
            }],
        execute: function() {
            CANVAS_PRESERVE_ASPECT_RATIO = "xMidYMid meet";
            GameMapComponent = (function () {
                function GameMapComponent(_element, _dom, _domHelper, _drawMapService, _drawMapMarkingService, _marking, _select, _animation) {
                    this._element = _element;
                    this._dom = _dom;
                    this._domHelper = _domHelper;
                    this._drawMapService = _drawMapService;
                    this._drawMapMarkingService = _drawMapMarkingService;
                    this._marking = _marking;
                    this._select = _select;
                    this._animation = _animation;
                }
                GameMapComponent.prototype.ngOnInit = function () {
                    this._createCanvas();
                    this._subscribeOnMapElementsClick();
                    this._drawMapService.drawMap(this._canvas, this.game, this.game.map);
                    this._subscribeOnMarkingChanging();
                    this._subscribeOnDiceThrowing();
                };
                GameMapComponent.prototype._createCanvas = function () {
                    this._canvas = this._dom.createElementNS(this._drawMapService.NS, 'svg');
                    this._dom.setAttribute(this._canvas, 'preserveAspectRatio', CANVAS_PRESERVE_ASPECT_RATIO);
                    this._dom.appendChild(this._element.nativeElement, this._canvas);
                };
                GameMapComponent.prototype._subscribeOnMapElementsClick = function () {
                    var _this = this;
                    this._domHelper.on(this._canvas, 'click', this._drawMapService.NODE_SELECTOR, function (element) {
                        if (_this._dom.getAttribute(element, 'marked')) {
                            var id = +_this._dom.getAttribute(element, 'node-id');
                            _this._select.select('node', id);
                        }
                    });
                    this._domHelper.on(this._canvas, 'click', this._drawMapService.EDGE_SELECTOR, function (element) {
                        if (_this._dom.getAttribute(element, 'marked')) {
                            var id = +_this._dom.getAttribute(element, 'edge-id');
                            _this._select.select('edge', id);
                        }
                    });
                    this._domHelper.on(this._canvas, 'click', this._drawMapService.HEX_SELECTOR, function (element) {
                        if (_this._dom.getAttribute(element, 'marked')) {
                            var id = +_this._dom.getAttribute(element, 'hex-id');
                            _this._select.select('hex', id);
                        }
                    });
                };
                GameMapComponent.prototype._subscribeOnMarkingChanging = function () {
                    var _this = this;
                    var types = [
                        ['hexes', 'hex'],
                        ['nodes', 'node'],
                        ['edges', 'edge']
                    ];
                    types.forEach(function (type) {
                        _this._drawMapMarkingService.updateByType(_this._canvas, type[0], type[1]);
                        _this._marking.onUpdate(type[0], function () { return _this._drawMapMarkingService.updateByType(_this._canvas, type[0], type[1]); });
                    });
                };
                GameMapComponent.prototype._subscribeOnDiceThrowing = function () {
                    var _this = this;
                    this.game.dice.onThrow(function (value) {
                        if (value === 7) {
                            _this._animation.robberThrown(_this._canvas);
                        }
                        else {
                            _this._animation.hexDiceThrown(_this._canvas, _this.game.map, value);
                        }
                    });
                };
                GameMapComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-game-map',
                        template: '',
                        //TODO: find way use css like that:  (instead of link in index.html)
                        //try just add to all elements attr like "_ngcontent-cgr-10" - same throw all css scope
                        //styleUrls: ['app/play/game-map/game-map.component.css'],
                        providers: [
                            browser_adapter_1.BrowserDomAdapter,
                            draw_map_service_1.DrawMapService,
                            draw_map_marking_service_1.DrawMapMarkingService,
                            draw_map_helper_1.DrawMapHelper,
                            dom_helper_1.DomHelper,
                            map_animation_service_1.MapAnimationService
                        ],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [core_1.ElementRef, browser_adapter_1.BrowserDomAdapter, (typeof (_a = typeof dom_helper_1.DomHelper !== 'undefined' && dom_helper_1.DomHelper) === 'function' && _a) || Object, draw_map_service_1.DrawMapService, draw_map_marking_service_1.DrawMapMarkingService, (typeof (_b = typeof marking_service_1.MarkingService !== 'undefined' && marking_service_1.MarkingService) === 'function' && _b) || Object, (typeof (_c = typeof select_service_1.SelectService !== 'undefined' && select_service_1.SelectService) === 'function' && _c) || Object, map_animation_service_1.MapAnimationService])
                ], GameMapComponent);
                return GameMapComponent;
                var _a, _b, _c;
            }());
            exports_1("GameMapComponent", GameMapComponent);
        }
    }
});
