System.register(['angular2/core', 'app/shared/domain/game-map/node', 'app/shared/domain/game-map/point'], function(exports_1, context_1) {
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
    var core_1, node_1, point_1;
    var ISOMETRIC_RATIO, DrawMapHelper;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (node_1_1) {
                node_1 = node_1_1;
            },
            function (point_1_1) {
                point_1 = point_1_1;
            }],
        execute: function() {
            ISOMETRIC_RATIO = Math.tan(Math.PI / 6);
            DrawMapHelper = (function () {
                function DrawMapHelper() {
                }
                DrawMapHelper.prototype.getObjectPosition = function (where, what) {
                    for (var k in where) {
                        if (where[k] === what) {
                            return k;
                        }
                    }
                };
                DrawMapHelper.prototype.getHexCoords = function (hex, hexWidth, hexHeight) {
                    var x = 0;
                    var y = 0;
                    // x affection
                    x = x + (hex.x + hex.y / 2) * hexWidth;
                    y = y - (hex.x + hex.y / 2) * hexWidth * ISOMETRIC_RATIO;
                    // y affection
                    y = y + hex.y * hexHeight * ISOMETRIC_RATIO;
                    x = x + hex.y * hexHeight;
                    return new point_1.Point(x, y);
                };
                DrawMapHelper.prototype.getNodeCoords = function (node, hexWidth, hexHeight) {
                    var hex = node.getFirstHex();
                    var position = this.getObjectPosition(hex.nodes, node);
                    var hexCoords = this.getHexCoords(hex, hexWidth, hexHeight);
                    var x = hexCoords.x;
                    if (position === "topLeft") {
                        x -= hexWidth * 3 / 4;
                    }
                    if (position === "top" || position === "bottomLeft") {
                        x -= hexWidth / 4;
                    }
                    if (position === "topRight" || position === "bottom") {
                        x += hexWidth / 4;
                    }
                    if (position === "bottomRight") {
                        x += hexWidth * 3 / 4;
                    }
                    var y = hexCoords.y;
                    if (position === "topRight") {
                        y -= hexHeight * 3 / 2 * ISOMETRIC_RATIO;
                    }
                    if (position === "top" || position === "bottomRight") {
                        y -= hexHeight / 2 * ISOMETRIC_RATIO;
                    }
                    if (position === "topLeft" || position === "bottom") {
                        y += hexHeight / 2 * ISOMETRIC_RATIO;
                    }
                    if (position === "bottomLeft") {
                        y += hexHeight * 3 / 2 * ISOMETRIC_RATIO;
                    }
                    return new point_1.Point(x, y);
                };
                DrawMapHelper.prototype.getEdgeCoords = function (edge, hexWidth, hexHeight) {
                    var hex = edge.getFirstHex();
                    var position = this.getObjectPosition(hex.edges, edge);
                    var hexCoords = this.getHexCoords(hex, hexWidth, hexHeight);
                    var x = hexCoords.x;
                    if (position === "topLeft" || position === "left") {
                        x -= hexWidth / 2;
                    }
                    if (position === "bottomRight" || position === "right") {
                        x += hexWidth / 2;
                    }
                    var y = hexCoords.y;
                    if (position === "topRight" || position === "right") {
                        y -= hexHeight * ISOMETRIC_RATIO;
                    }
                    if (position === "left" || position === "bottomLeft") {
                        y += hexHeight * ISOMETRIC_RATIO;
                    }
                    return new point_1.Point(x, y);
                };
                DrawMapHelper.prototype.getPortOffset = function (node, portDistance) {
                    var xDec, yDec;
                    if (node.orientation === node_1.NodeOrientation.SINGLE_BOTTOM) {
                        yDec = (node.hexes.bottom) ? -1 : 1;
                        if (node.hexes.topLeft && !node.hexes.topRight) {
                            xDec = 1;
                        }
                        else if (!node.hexes.topLeft && node.hexes.topRight) {
                            xDec = -1;
                        }
                        else {
                            xDec = 0;
                        }
                    }
                    if (node.orientation === node_1.NodeOrientation.SINGLE_TOP) {
                        yDec = (node.hexes.top) ? 1 : -1;
                        if (node.hexes.bottomLeft && !node.hexes.bottomRight) {
                            xDec = 1;
                        }
                        else if (!node.hexes.bottomLeft && node.hexes.bottomRight) {
                            xDec = -1;
                        }
                        else {
                            xDec = 0;
                        }
                    }
                    var x = 0;
                    var y = 0;
                    // x affection
                    x = x + xDec * portDistance;
                    y = y - xDec * portDistance * ISOMETRIC_RATIO;
                    // y affection
                    y = y + yDec * portDistance * ISOMETRIC_RATIO;
                    x = x + yDec * portDistance;
                    return new point_1.Point(x, y);
                };
                DrawMapHelper.prototype.getPortPairs = function (nodes) {
                    var portNodes = nodes
                        .filter(function (node) { return node.hasPort(); })
                        .sort(function (a, b) { return (a.gridY === b.gridY) ? b.gridX - a.gridX : b.gridY - a.gridY; });
                    var pairs = [];
                    var _loop_1 = function() {
                        var pair = {};
                        pair.firstNode = portNodes.shift();
                        portNodes.some(function (node, i) {
                            var jointEdge = node.getJointEdge(pair.firstNode);
                            if (jointEdge) {
                                pair.edge = jointEdge;
                                pair.secondNode = node;
                                portNodes.splice(i, 1);
                                return true;
                            }
                        });
                        pairs.push(pair);
                    };
                    while (portNodes.length) {
                        _loop_1();
                    }
                    return pairs;
                };
                DrawMapHelper = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [])
                ], DrawMapHelper);
                return DrawMapHelper;
            }());
            exports_1("DrawMapHelper", DrawMapHelper);
        }
    }
});
