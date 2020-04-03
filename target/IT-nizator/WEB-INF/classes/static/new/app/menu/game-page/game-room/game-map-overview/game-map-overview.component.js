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
    var HEX_WIDTH, HEX_HEIGHT, OFFSET_X, OFFSET_Y, PORT_WIDTH, PORT_HEIGHT, GameMapOverviewComponent, MapHelper;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (browser_adapter_1_1) {
                browser_adapter_1 = browser_adapter_1_1;
            }],
        execute: function() {
            HEX_WIDTH = 50;
            HEX_HEIGHT = 25;
            OFFSET_X = 102;
            OFFSET_Y = 80;
            PORT_WIDTH = Math.round(HEX_HEIGHT / 2.2);
            PORT_HEIGHT = Math.round(HEX_HEIGHT / 2.2);
            GameMapOverviewComponent = (function () {
                function GameMapOverviewComponent(_element, _dom) {
                    this._element = _element;
                    this._dom = _dom;
                }
                GameMapOverviewComponent.prototype.ngOnInit = function () {
                    var canvas = this._dom.createElement("div");
                    this._dom.addClass(canvas, 'canvas');
                    this._dom.appendChild(this._element.nativeElement, canvas);
                    this._drawMap(this.game.map, canvas);
                };
                GameMapOverviewComponent.prototype._drawMap = function (map, canvas) {
                    for (var _i = 0, _a = map.hexes; _i < _a.length; _i++) {
                        var item = _a[_i];
                        var elem = this._dom.createElement("div");
                        this._dom.addClass(elem, 'hex');
                        this._dom.addClass(elem, item.getTypeToString().toLowerCase());
                        this._dom.setStyle(elem, 'width', HEX_WIDTH + 'px');
                        this._dom.setStyle(elem, 'height', HEX_HEIGHT + 'px');
                        this._dom.setStyle(elem, 'left', OFFSET_X + MapHelper.getPositionX(item.x, item.y) + 'px');
                        this._dom.setStyle(elem, 'top', OFFSET_Y + MapHelper.getPositionY(item.x, item.y) + 'px');
                        this._dom.appendChild(canvas, elem);
                        var elemCoords = this._dom.createElement("div");
                        this._dom.addClass(elemCoords, 'coords');
                        this._dom.setStyle(elemCoords, 'width', HEX_WIDTH + 'px');
                        this._dom.setStyle(elemCoords, 'height', HEX_HEIGHT + 'px');
                        this._dom.setStyle(elemCoords, 'font-size', Math.round(HEX_HEIGHT / 3) + 'px');
                        this._dom.appendChild(elemCoords, this._dom.createTextNode(item.x + ',' + item.y));
                        this._dom.appendChild(elem, elemCoords);
                        var elemDice = this._dom.createElement("div");
                        this._dom.addClass(elemDice, 'dice');
                        this._dom.setStyle(elemDice, 'width', HEX_WIDTH + 'px');
                        this._dom.setStyle(elemDice, 'height', HEX_HEIGHT + 'px');
                        this._dom.setStyle(elemDice, 'font-size', Math.round(HEX_HEIGHT / 2) + 'px');
                        this._dom.setStyle(elemDice, 'line-height', HEX_HEIGHT + 'px');
                        var elemNumber = void 0;
                        if (item.robbed) {
                            elemNumber = this._dom.createElement('span');
                            this._dom.addClass(elemNumber, 'glyphicon');
                            this._dom.addClass(elemNumber, 'glyphicon-fire');
                        }
                        else {
                            elemNumber = this._dom.createTextNode(item.dice);
                        }
                        this._dom.appendChild(elemDice, elemNumber);
                        this._dom.appendChild(elem, elemDice);
                    }
                    for (var _b = 0, _c = map.nodes; _b < _c.length; _b++) {
                        var item = _c[_b];
                        var elem = this._dom.createElement("div");
                        this._dom.addClass(elem, 'node');
                        this._dom.addClass(elem, 'node-' + item.id);
                        this._dom.addClass(elem, item.getPortToString().toLowerCase());
                        this._dom.setStyle(elem, 'width', PORT_WIDTH + 'px');
                        this._dom.setStyle(elem, 'height', PORT_HEIGHT + 'px');
                        this._dom.setStyle(elem, 'left', OFFSET_X + MapHelper.getPositionNodeX(item) + 'px');
                        this._dom.setStyle(elem, 'top', OFFSET_Y + MapHelper.getPositionNodeY(item) + 'px');
                        if (item.hasPort()) {
                            var elemPort = this._dom.createElement('span');
                            this._dom.addClass(elemPort, 'glyphicon');
                            this._dom.addClass(elemPort, 'glyphicon-plane');
                            this._dom.appendChild(elem, elemPort);
                        }
                        this._dom.appendChild(canvas, elem);
                    }
                };
                GameMapOverviewComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-game-map-overview',
                        template: '',
                        //TODO: find way use css like that:  (instead of link in index.html)
                        //try just add to all elements attr like "_ngcontent-cgr-10" - same throw all css scope
                        //styleUrls: ['app/menu/game-page/game-room/game-map-overview/game-map-overview.component.css'],
                        providers: [browser_adapter_1.BrowserDomAdapter],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [core_1.ElementRef, browser_adapter_1.BrowserDomAdapter])
                ], GameMapOverviewComponent);
                return GameMapOverviewComponent;
            }());
            exports_1("GameMapOverviewComponent", GameMapOverviewComponent);
            MapHelper = (function () {
                function MapHelper() {
                }
                MapHelper.getPosition = function (where, what) {
                    for (var k in where) {
                        if (where[k] === what) {
                            return k;
                        }
                    }
                };
                MapHelper.getPositionX = function (x, y) {
                    return x * HEX_WIDTH + y * HEX_WIDTH / 2;
                };
                MapHelper.getPositionY = function (x, y) {
                    return y * HEX_HEIGHT;
                };
                MapHelper.getHexOfNode = function (item) {
                    for (var i in item.hexes) {
                        if (item.hexes[i]) {
                            return item.hexes[i];
                        }
                    }
                };
                MapHelper.getPositionNodeX = function (node) {
                    var hex = this.getHexOfNode(node);
                    var position = this.getPosition(hex.nodes, node);
                    var hexX = this.getPositionX(hex.x, hex.y);
                    var x = hexX - Math.round(PORT_WIDTH / 2);
                    if (position === "top" || position === "bottom") {
                        x += Math.round(HEX_WIDTH / 2);
                    }
                    if (position === "topRight" || position === "bottomRight") {
                        x += HEX_WIDTH;
                    }
                    return x;
                };
                MapHelper.getPositionNodeY = function (node) {
                    var hex = this.getHexOfNode(node);
                    var position = this.getPosition(hex.nodes, node);
                    var hexY = this.getPositionY(hex.x, hex.y);
                    var y = hexY - Math.round(PORT_HEIGHT / 2);
                    if (position === "bottomRight" || position === "bottom" || position === "bottomLeft") {
                        y += HEX_HEIGHT;
                    }
                    return y;
                };
                return MapHelper;
            }());
        }
    }
});
