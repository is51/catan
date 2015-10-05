'use strict';

angular.module('catan')
        .factory('MapDrawService', ['MapService', function (MapService) {

            var HEX_WIDTH = 78;
            var HEX_HEIGHT = 40;

            var DrawHelper = {
                getHexCoords: function (hex) {
                    return {
                        x: hex.x * HEX_WIDTH + hex.y * HEX_WIDTH / 2,
                        y: hex.y * HEX_HEIGHT
                    }
                },
                getNodeCoords: function (node) {
                    var hex = MapService.getFirstHexOfNode(node);
                    var position = MapService.getObjectPosition(hex.nodes, node);
                    var hexCoords = this.getHexCoords(hex);

                    var x = hexCoords.x;
                    if (position === "topLeft" || position === "bottomLeft") { x -= Math.round(HEX_WIDTH/2); }
                    if (position === "topRight" || position === "bottomRight") { x += Math.round(HEX_WIDTH/2); }

                    var y = hexCoords.y;
                    if (position === "topRight" || position === "top" || position === "topLeft") { y -= Math.round(HEX_HEIGHT/2); }
                    if (position === "bottomRight" || position === "bottom" || position === "bottomLeft") { y += Math.round(HEX_HEIGHT/2); }

                    return {x: x, y: y}
                },
                getEdgeCoords: function (edge) {
                    var hex = MapService.getFirstHexOfEdge(edge);
                    var position = MapService.getObjectPosition(hex.edges, edge);
                    var hexCoords = this.getHexCoords(hex);

                    var x = hexCoords.x;
                    if (position === "left") { x -= Math.round(HEX_WIDTH/2); }
                    if (position === "topLeft" || position === "bottomLeft") { x -= Math.round(HEX_WIDTH/4); }
                    if (position === "topRight" || position === "bottomRight") { x += Math.round(HEX_WIDTH/4); }
                    if (position === "right") { x += Math.round(HEX_WIDTH/2); }

                    var y = hexCoords.y;
                    if (position === "topLeft" || position === "topRight") { y -= Math.round(HEX_HEIGHT/2); }
                    if (position === "bottomLeft" || position === "bottomRight") { y += Math.round(HEX_HEIGHT/2); }

                    return {x: x, y: y}
                },
                getPortOffset: function(node) {
                    var x = 0,
                        y = 0;

                    if (node.orientation === "SINGLE_BOTTOM") {
                        if (!node.hexes.bottom) {
                            y = 1;
                        }
                        if (node.hexes.bottom) {
                            y = -1;
                        }
                        if (node.hexes.topLeft && !node.hexes.topRight) {
                            x = 1;
                        } else if (!node.hexes.topLeft && node.hexes.topRight) {
                            x = -1;
                        }
                    }

                    if (node.orientation === "SINGLE_TOP") {
                        if (!node.hexes.top) {
                            y = -1;
                        }
                        if (node.hexes.top) {
                            y = 1;
                        }
                        if (node.hexes.bottomLeft && !node.hexes.bottomRight) {
                            x = 1;
                        } else if (!node.hexes.bottomLeft && node.hexes.bottomRight) {
                            x = -1;
                        }
                    }

                    return {x: x, y: y};
                },
                createBuildingByTemplate: function(canvas, coords) {
                    var root = angular.element('<div/>')
                            .addClass('building')
                            .css('left', coords.x)
                            .css('top', coords.y)
                            .appendTo(canvas);

                    var inner = angular.element('<div/>')
                            .addClass('inner')
                            .appendTo(root);

                    return {root: root, inner: inner};
                }
            };

            var MapDrawService = {};

            MapDrawService.drawMap = function(canvas, game, map) {

                var i, l;

                for (i = 0, l = map.hexes.length; i < l; i++) {
                    this.drawHex(
                            canvas,
                            game,
                            DrawHelper.getHexCoords(map.hexes[i]),
                            map.hexes[i]
                    );
                }

                for (i = 0, l = map.edges.length; i < l; i++) {
                    this.drawEdge(
                            canvas,
                            game,
                            DrawHelper.getEdgeCoords(map.edges[i]),
                            map.edges[i]
                    );
                }

                for (i = 0, l = map.nodes.length; i < l; i++) {
                    this.drawNode(
                            canvas,
                            game,
                            DrawHelper.getNodeCoords(map.nodes[i]),
                            map.nodes[i]
                    );
                }
            };

            MapDrawService.drawHex = function(canvas, game, coords, hex) {
                var ROBBED_TEXT = angular.element('<span/>', {'class':'glyphicon glyphicon-fire'});

                var elem = angular.element('<div/>')
                        .addClass('hex')
                        .css('left', coords.x + 'px')
                        .css('top', coords.y + 'px')
                        .appendTo(canvas);

                var elemInner = angular.element('<div/>')
                        .addClass('inner')
                        .addClass(hex.type.toLowerCase())
                        .css('width', HEX_WIDTH + 'px')
                        .css('height', HEX_HEIGHT + 'px')
                        .css('left', (0 - Math.round(HEX_WIDTH/2)) + 'px')
                        .css('top', (0 - Math.round(HEX_HEIGHT/2)) + 'px')
                        .appendTo(elem);

                var elemInnerDice =  angular.element('<div/>')
                        .addClass('dice')
                        .css('width', HEX_WIDTH + 'px')
                        .css('height', HEX_HEIGHT + 'px')
                        .html(hex.dice)
                        .appendTo(elemInner);

                if (hex.robbed) {
                    elemInnerDice.append(ROBBED_TEXT);
                }
            };

            MapDrawService.drawNode = function(canvas, game, coords, node) {
                var elem = angular.element('<div/>')
                        .addClass('node')
                        .css('left', coords.x)
                        .css('top', coords.y)
                        .appendTo(canvas);

                if (node.building) {

                    var colorId = game.getGameUser(node.building.ownerGameUserId).colorId;

                    if (node.building.built === "SETTLEMENT") {
                        this.drawSettlement(elem, {x: 0, y: 0}, colorId);
                    }
                    if (node.building.built === "CITY") {
                        this.drawCity(elem, {x: 0, y: 0}, colorId);
                    }
                } else {
                    this.drawEmptyNode(elem, {x: 0, y: 0});
                }

                if (node.port !== "NONE") {
                    this.drawPort(elem, DrawHelper.getPortOffset(node), node.port);
                }
            };

            MapDrawService.drawEmptyNode = function(canvas, coords) {
                var elem = DrawHelper.createBuildingByTemplate(canvas, coords);
                elem.root.addClass('none-node');
            };

            MapDrawService.drawSettlement = function(canvas, coords, colorId) {
                var elem = DrawHelper.createBuildingByTemplate(canvas, coords);
                elem.root.addClass('settlement');
                elem.inner
                        .addClass('color-' + colorId)
                        .html("S");
            };

            MapDrawService.drawCity = function(canvas, coords, colorId) {
                var elem = DrawHelper.createBuildingByTemplate(canvas, coords);
                elem.root.addClass('city');
                elem.inner
                        .addClass('color-' + colorId)
                        .html("C");
            };

            MapDrawService.drawPort = function(canvas, offset, type) {
                var PORT_TEXT = angular.element('<span/>', {'class':'glyphicon glyphicon-plane'});

                var root = angular.element('<div/>')
                        .addClass('port')
                        .appendTo(canvas);

                if (offset.x !== 0) {
                    root.addClass( (offset.x === 1) ? 'offset-right' : 'offset-left' );
                }

                if (offset.y !== 0) {
                    root.addClass( (offset.y === 1) ? 'offset-bottom' : 'offset-top' );
                }

                var inner = angular.element('<div/>')
                        .addClass('inner')
                        .addClass(type.toLowerCase() + '-color')
                        .html(PORT_TEXT)
                        .appendTo(root);
            };

            MapDrawService.drawEdge = function(canvas, game, coords, edge) {
                var elem = angular.element('<div/>')
                        .addClass('edge')
                        .css('left', coords.x)
                        .css('top', coords.y)
                        .appendTo(canvas);

                var orientation = (edge.orientation === "VERTICAL") ? 'vertical' : 'horizontal';

                if (edge.building) {
                    var colorId = game.getGameUser(edge.building.ownerGameUserId).colorId;
                    this.drawRoad(elem, {x: 0, y: 0}, orientation, colorId);
                } else {
                    this.drawEmptyEdge(elem, {x: 0, y: 0}, orientation);
                }
            };

            MapDrawService.drawEmptyEdge = function (canvas, coords, orientation) {
                var elem = DrawHelper.createBuildingByTemplate(canvas, coords);
                elem.root
                        .addClass('none-edge')
                        .addClass(orientation);
            };

            MapDrawService.drawRoad = function (canvas, coords, orientation, colorId) {
                var elem = DrawHelper.createBuildingByTemplate(canvas, coords);
                elem.root
                        .addClass('road')
                        .addClass(orientation);
                elem.inner
                        .addClass('color-' + colorId);
            };

            return MapDrawService;
        }]);