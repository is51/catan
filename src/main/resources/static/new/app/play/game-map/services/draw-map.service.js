System.register(['angular2/core', "angular2/src/platform/browser/browser_adapter", '../helpers/draw-map.helper', 'app/play/shared/services/templates.service', 'app/shared/domain/game-map/edge', 'app/shared/domain/game-map/point'], function(exports_1, context_1) {
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
    var core_1, browser_adapter_1, draw_map_helper_1, templates_service_1, edge_1, point_1;
    var NS, HEX_WIDTH, HEX_HEIGHT, PORT_DISTANCE, PORT_COLORS, DICE_COLORS, BUILDING_COLORS, ROAD_COLORS, DrawMapService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (browser_adapter_1_1) {
                browser_adapter_1 = browser_adapter_1_1;
            },
            function (draw_map_helper_1_1) {
                draw_map_helper_1 = draw_map_helper_1_1;
            },
            function (templates_service_1_1) {
                templates_service_1 = templates_service_1_1;
            },
            function (edge_1_1) {
                edge_1 = edge_1_1;
            },
            function (point_1_1) {
                point_1 = point_1_1;
            }],
        execute: function() {
            NS = 'http://www.w3.org/2000/svg';
            HEX_WIDTH = 50;
            HEX_HEIGHT = 25;
            PORT_DISTANCE = 18;
            PORT_COLORS = {
                brick: '#43a8d9',
                wood: '#d5c867',
                sheep: '#48da83',
                wheat: '#a187aa',
                stone: '#f69f7e',
                any: '#cccccc'
            };
            DICE_COLORS = {
                brick: {
                    colorNumber: '#3699D1',
                    colorMarkerGrad1: '#4BB0DD',
                    colorMarkerGrad2: '#4BB0DD',
                    colorMarkerGrad3: '#3699D1'
                },
                wood: {
                    colorNumber: '#d2c14d',
                    colorMarkerGrad1: '#d8ce80',
                    colorMarkerGrad2: '#d8ce80',
                    colorMarkerGrad3: '#d2c14d'
                },
                sheep: {
                    colorNumber: '#00c553',
                    colorMarkerGrad1: '#45d980',
                    colorMarkerGrad2: '#45d980',
                    colorMarkerGrad3: '#00c553'
                },
                wheat: {
                    colorNumber: '#93729d',
                    colorMarkerGrad1: '#aa93b2',
                    colorMarkerGrad2: '#aa93b2',
                    colorMarkerGrad3: '#93729d'
                },
                stone: {
                    colorNumber: '#f38057',
                    colorMarkerGrad1: '#e69677',
                    colorMarkerGrad2: '#e69677',
                    colorMarkerGrad3: '#f38057'
                },
            };
            BUILDING_COLORS = {
                1: '#f6663d',
                2: '#727df4',
                3: '#ddcc00',
                4: '#00ff55'
            };
            ROAD_COLORS = {
                1: '#f6663d',
                2: '#727df4',
                3: '#ddcc00',
                4: '#00ff55'
            };
            DrawMapService = (function () {
                function DrawMapService(_templates, _helper, _dom) {
                    this._templates = _templates;
                    this._helper = _helper;
                    this._dom = _dom;
                    this.HEX_SELECTOR = '.hex';
                    this.NODE_SELECTOR = '.node';
                    this.EDGE_SELECTOR = '.edge';
                    this.ROBBER_SELECTOR = '.robber';
                    this.NS = NS;
                }
                DrawMapService.prototype.drawMap = function (canvas, game, map) {
                    var _this = this;
                    this._clear(canvas);
                    var actualSize = {
                        xMin: null,
                        xMax: null,
                        yMin: null,
                        yMax: null
                    };
                    this.drawMapBottom(canvas);
                    this.drawPorts(canvas, map);
                    map.hexes.forEach(function (hex) {
                        var coords = _this._helper.getHexCoords(hex, HEX_WIDTH, HEX_HEIGHT);
                        actualSize = _this._updateActualSize(actualSize, coords.x, coords.y, HEX_HEIGHT, HEX_HEIGHT);
                        var hexElement = _this.drawHex(canvas, coords, hex);
                        hex.onUpdate(function () { return _this.updateHex(canvas, hexElement, hex); });
                    });
                    map.edges.forEach(function (edge) {
                        var coords = _this._helper.getEdgeCoords(edge, HEX_WIDTH, HEX_HEIGHT);
                        var edgeElement = _this.drawEdge(canvas, game, coords, edge);
                        edge.onUpdate(function () { return _this.updateEdge(edgeElement, game, edge); });
                    });
                    map.nodes.forEach(function (node) {
                        var coords = _this._helper.getNodeCoords(node, HEX_WIDTH, HEX_HEIGHT);
                        var nodeElement = _this.drawNode(canvas, game, coords, node);
                        node.onUpdate(function () { return _this.updateNode(nodeElement, game, node); });
                    });
                    this.drawRobber(canvas, map);
                    this.drawClouds(canvas);
                    this._setViewBox(canvas, actualSize);
                };
                DrawMapService.prototype._clear = function (element) {
                    this._dom.clearNodes(element);
                };
                DrawMapService.prototype._updateActualSize = function (oldActualSize, x, y, w, h) {
                    var newActualSize = {
                        xMin: oldActualSize.xMin,
                        xMax: oldActualSize.xMax,
                        yMin: oldActualSize.yMin,
                        yMax: oldActualSize.yMax
                    };
                    var xLeft = x - w / 2, xRight = x + w / 2, yTop = y - h / 2, yBottom = y + h / 2;
                    if (oldActualSize.xMin === null || xLeft < oldActualSize.xMin) {
                        newActualSize.xMin = xLeft;
                    }
                    if (oldActualSize.xMax === null || xRight > oldActualSize.xMax) {
                        newActualSize.xMax = xRight;
                    }
                    if (oldActualSize.yMin === null || yTop < oldActualSize.yMin) {
                        newActualSize.yMin = yTop;
                    }
                    if (oldActualSize.yMax === null || yBottom > oldActualSize.yMax) {
                        newActualSize.yMax = yBottom;
                    }
                    return newActualSize;
                };
                DrawMapService.prototype._setViewBox = function (canvas, actualSize) {
                    var additionalWidth = 3.9 * HEX_WIDTH;
                    var additionalHeight = 2.8 * HEX_HEIGHT;
                    var width = actualSize.xMax - actualSize.xMin + additionalWidth;
                    var height = actualSize.yMax - actualSize.yMin + additionalHeight;
                    var offsetX = actualSize.xMin - additionalWidth / 2;
                    var offsetY = actualSize.yMin - additionalHeight / 2.5;
                    this._dom.setAttribute(canvas, 'viewBox', offsetX + ' ' + offsetY + ' ' + width + ' ' + height);
                };
                DrawMapService.prototype.drawMapBottom = function (canvas) {
                    //TODO: Map Bottom is hardcoded for default map
                    var group = this._dom.createElementNS(NS, 'g');
                    this._dom.setAttribute(group, 'class', 'map-bottom');
                    this._dom.appendChild(canvas, group);
                    this._dom.setInnerHTML(group, this._templates.get('map-bottom'));
                };
                DrawMapService.prototype.drawHex = function (canvas, coords, hex) {
                    var group = this._dom.createElementNS(NS, 'g');
                    this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
                    this._dom.setAttribute(group, 'class', 'hex');
                    this._dom.setAttribute(group, 'hex-id', hex.id);
                    this._dom.appendChild(canvas, group);
                    this._dom.setInnerHTML(group, this._getHexHTML(hex));
                    return group;
                };
                DrawMapService.prototype._getHexHTML = function (hex) {
                    var html = this._templates.get('hex-bg');
                    if (!hex.edges.bottomLeft.isJoint()) {
                        html += this._templates.get('hex-bg-edge-bottom-left');
                    }
                    if (!hex.edges.bottomRight.isJoint()) {
                        html += this._templates.get('hex-bg-edge-bottom-right');
                    }
                    if (!hex.edges.left.isJoint()) {
                        html += this._templates.get('hex-bg-edge-left');
                    }
                    if (!hex.edges.right.isJoint()) {
                        html += this._templates.get('hex-bg-edge-right');
                    }
                    if (!hex.edges.topLeft.isJoint()) {
                        html += this._templates.get('hex-bg-edge-top-left');
                    }
                    if (!hex.edges.topRight.isJoint()) {
                        html += this._templates.get('hex-bg-edge-top-right');
                    }
                    if (!hex.nodes.top.isJoint()) {
                        html += this._templates.get('hex-bg-node-top');
                    }
                    if (!hex.nodes.bottom.isJoint()) {
                        html += this._templates.get('hex-bg-node-bottom');
                    }
                    if (!hex.nodes.topRight.isJoint()) {
                        html += this._templates.get('hex-bg-node-top-right');
                        html += this._templates.get('hex-bg-node-right-top');
                    }
                    if (!hex.nodes.topLeft.isJoint()) {
                        html += this._templates.get('hex-bg-node-top-left');
                        html += this._templates.get('hex-bg-node-left-top');
                    }
                    if (!hex.nodes.bottomRight.isJoint()) {
                        html += this._templates.get('hex-bg-node-bottom-right');
                        html += this._templates.get('hex-bg-node-right-bottom');
                    }
                    if (!hex.nodes.bottomLeft.isJoint()) {
                        html += this._templates.get('hex-bg-node-bottom-left');
                        html += this._templates.get('hex-bg-node-left-bottom');
                    }
                    if (!hex.nodes.bottomRight.hexes.bottom && hex.nodes.bottomRight.hexes.topRight) {
                        html += this._templates.get('hex-bg-node-bottom-right');
                    }
                    if (!hex.nodes.topRight.hexes.top && hex.nodes.topRight.hexes.bottomRight) {
                        html += this._templates.get('hex-bg-node-top-right');
                    }
                    var resourceTypeStr = hex.getTypeToString().toLowerCase();
                    html += this._templates.get('hex-' + resourceTypeStr);
                    if (hex.dice) {
                        html += this._templates.get('hex-dice', Object.assign({
                            number: (hex.dice) ? hex.dice : '',
                            resourceType: resourceTypeStr,
                        }, DICE_COLORS[resourceTypeStr]));
                    }
                    return html;
                };
                DrawMapService.prototype.updateHex = function (canvas, element, hex) {
                    // Only hex.robbed can be updated
                    var hexDice = this._dom.querySelector(element, '.dice');
                    if (hex.robbed) {
                        this.updateRobber(canvas, hex);
                    }
                };
                DrawMapService.prototype.drawRobber = function (canvas, map) {
                    var group = this._dom.createElementNS(NS, 'g');
                    this._dom.setAttribute(group, 'class', 'robber');
                    this._dom.appendChild(canvas, group);
                    this._dom.setInnerHTML(group, this._templates.get('robber'));
                    var robbedHex = map.hexes.filter(function (hex) { return hex.robbed; })[0];
                    this.updateRobber(canvas, robbedHex);
                };
                DrawMapService.prototype.updateRobber = function (canvas, robbedHex) {
                    var coords = this._helper.getHexCoords(robbedHex, HEX_WIDTH, HEX_HEIGHT);
                    var robber = this._dom.querySelector(canvas, '.robber');
                    this._dom.setAttribute(robber, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
                };
                DrawMapService.prototype.drawNode = function (canvas, game, coords, node) {
                    var group = this._dom.createElementNS(NS, 'g');
                    this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
                    this._dom.setAttribute(group, 'class', 'node');
                    this._dom.setAttribute(group, 'node-id', node.id);
                    this._dom.appendChild(canvas, group);
                    if (node.building) {
                        var colorId = game.getPlayer(node.building.ownerPlayerId).colorId;
                        if (node.hasSettlement()) {
                            this.drawSettlement(group, new point_1.Point(0, 0), colorId);
                        }
                        if (node.hasCity()) {
                            this.drawCity(group, new point_1.Point(0, 0), colorId);
                        }
                    }
                    else {
                        this.drawEmptyNode(group, new point_1.Point(0, 0));
                    }
                    return group;
                };
                DrawMapService.prototype.updateNode = function (element, game, node) {
                    // Only node.building can be updated
                    this._dom.clearNodes(element);
                    if (node.building) {
                        var colorId = game.getPlayer(node.building.ownerPlayerId).colorId;
                        if (node.hasSettlement()) {
                            this.drawSettlement(element, new point_1.Point(0, 0), colorId);
                        }
                        if (node.hasCity()) {
                            this.drawCity(element, new point_1.Point(0, 0), colorId);
                        }
                    }
                    else {
                        this.drawEmptyNode(element, new point_1.Point(0, 0));
                    }
                };
                DrawMapService.prototype.drawEmptyNode = function (canvas, coords) {
                    var group = this._dom.createElementNS(NS, 'g');
                    this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
                    this._dom.setAttribute(group, 'class', 'blank-node');
                    this._dom.appendChild(canvas, group);
                    this._dom.setInnerHTML(group, this._templates.get('blank-node'));
                };
                DrawMapService.prototype.drawSettlement = function (canvas, coords, colorId) {
                    var group = this._dom.createElementNS(NS, 'g');
                    this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
                    this._dom.setAttribute(group, 'class', 'settlement');
                    this._dom.appendChild(canvas, group);
                    this._dom.setInnerHTML(group, this._templates.get('settlement', { color: BUILDING_COLORS[colorId] }));
                };
                DrawMapService.prototype.drawCity = function (canvas, coords, colorId) {
                    var group = this._dom.createElementNS(NS, 'g');
                    this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
                    this._dom.setAttribute(group, 'class', 'city');
                    this._dom.appendChild(canvas, group);
                    this._dom.setInnerHTML(group, this._templates.get('city', { color: BUILDING_COLORS[colorId] }));
                };
                DrawMapService.prototype.drawEdge = function (canvas, game, coords, edge) {
                    var group = this._dom.createElementNS(NS, 'g');
                    this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
                    this._dom.setAttribute(group, 'class', 'edge');
                    this._dom.setAttribute(group, 'edge-id', edge.id);
                    this._dom.appendChild(canvas, group);
                    if (edge.building) {
                        var colorId = game.getPlayer(edge.building.ownerPlayerId).colorId;
                        this.drawRoad(group, new point_1.Point(0, 0), edge.orientation, colorId);
                    }
                    else {
                        this.drawEmptyEdge(group, new point_1.Point(0, 0), edge.orientation);
                    }
                    return group;
                };
                DrawMapService.prototype.updateEdge = function (element, game, edge) {
                    // Only edge.building can be updated
                    this._dom.clearNodes(element);
                    if (edge.building) {
                        var colorId = game.getPlayer(edge.building.ownerPlayerId).colorId;
                        this.drawRoad(element, new point_1.Point(0, 0), edge.orientation, colorId);
                    }
                    else {
                        this.drawEmptyEdge(element, new point_1.Point(0, 0), edge.orientation);
                    }
                };
                DrawMapService.prototype.drawEmptyEdge = function (canvas, coords, orientation) {
                    var group = this._dom.createElementNS(NS, 'g');
                    this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
                    this._dom.setAttribute(group, 'class', 'blank-edge');
                    this._dom.appendChild(canvas, group);
                    if (orientation === edge_1.EdgeOrientation.VERTICAL) {
                        this._dom.setInnerHTML(group, this._templates.get('blank-edge-vertical'));
                    }
                    else {
                        this._dom.setInnerHTML(group, this._templates.get('blank-edge-horizontal'));
                    }
                };
                DrawMapService.prototype.drawRoad = function (canvas, coords, orientation, colorId) {
                    var group = this._dom.createElementNS(NS, 'g');
                    this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
                    this._dom.setAttribute(group, 'class', 'road');
                    this._dom.appendChild(canvas, group);
                    var rect = this._dom.createElementNS(NS, 'rect');
                    if (orientation === edge_1.EdgeOrientation.VERTICAL) {
                        this._dom.setInnerHTML(group, this._templates.get('road-vertical', { color: ROAD_COLORS[colorId] }));
                    }
                    else {
                        this._dom.setInnerHTML(group, this._templates.get('road-horizontal', { color: ROAD_COLORS[colorId] }));
                    }
                };
                DrawMapService.prototype.drawPorts = function (canvas, map) {
                    var _this = this;
                    var portPairs = this._helper.getPortPairs(map.nodes);
                    portPairs.forEach(function (pair) {
                        var node1Coords = _this._helper.getNodeCoords(pair.firstNode, HEX_WIDTH, HEX_HEIGHT);
                        var node1PortOffset = _this._helper.getPortOffset(pair.firstNode, PORT_DISTANCE);
                        var node1PortCoords = node1Coords.plus(node1PortOffset);
                        var node2Coords = _this._helper.getNodeCoords(pair.secondNode, HEX_WIDTH, HEX_HEIGHT);
                        var node2PortOffset = _this._helper.getPortOffset(pair.secondNode, PORT_DISTANCE);
                        var node2PortCoords = node2Coords.plus(node2PortOffset);
                        var coords = node1PortCoords.average(node2PortCoords);
                        var node1OffsetBack = node1Coords.minus(coords);
                        var node2OffsetBack = node2Coords.minus(coords);
                        var portTemplateName = (pair.edge.isVertical()) ? 'port-vertical' : 'port-horizontal';
                        var resourceIconTemplate = _this._templates.get('icon-' + pair.firstNode.getPortToString().toLowerCase());
                        var group = _this._dom.createElementNS(NS, 'g');
                        _this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
                        _this._dom.appendChild(canvas, group);
                        _this._dom.setInnerHTML(group, _this._templates.get(portTemplateName, {
                            x1: node1OffsetBack.x,
                            x2: node2OffsetBack.x,
                            y1: node1OffsetBack.y,
                            y2: node2OffsetBack.y,
                            icon: resourceIconTemplate,
                            iconLabel: (pair.firstNode.hasPortAny()) ? '3:1' : '2:1'
                        }));
                    });
                };
                DrawMapService.prototype.drawClouds = function (canvas) {
                    //TODO: Clouds are hardcoded for default map
                    var group = this._dom.createElementNS(NS, 'g');
                    this._dom.setAttribute(group, 'class', 'clouds');
                    this._dom.appendChild(canvas, group);
                    this._dom.setInnerHTML(group, this._templates.get('clouds'));
                };
                DrawMapService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof templates_service_1.TemplatesService !== 'undefined' && templates_service_1.TemplatesService) === 'function' && _a) || Object, draw_map_helper_1.DrawMapHelper, browser_adapter_1.BrowserDomAdapter])
                ], DrawMapService);
                return DrawMapService;
                var _a;
            }());
            exports_1("DrawMapService", DrawMapService);
        }
    }
});
