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
                        .css('width', 0)
                        .css('height', 0)
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
                var SETTLEMENT_TEXT = "S";
                var CITY_TEXT = "C";

                var elem = angular.element('<div/>')
                        .addClass('node')
                    //.addClass(node.port.toLowerCase())
                        .css('width', 0)
                        .css('height', 0)
                        .css('left', coords.x)
                        .css('top', coords.y)
                        .appendTo(canvas);

                var elemInner = angular.element('<div/>')
                        .addClass('inner')
                        .appendTo(elem);

                if (node.building) {
                    elemInner.addClass('color-' + game.getGameUser(node.building.ownerGameUserId).colorId);
                }

                if (node.building && node.building.built === "SETTLEMENT") {
                    elem.addClass('settlement');
                    elemInner.html(SETTLEMENT_TEXT);
                } else if (node.building && node.building.built === "CITY") {
                    elem.addClass('city');
                    elemInner.html(CITY_TEXT);
                } else {
                    elem.addClass('none');
                }
            };

            MapDrawService.drawEdge = function(canvas, game, coords, edge) {
                var elem = angular.element('<div/>')
                        .addClass('edge')
                        .css('width', 0)
                        .css('height', 0)
                        .css('left', coords.x)
                        .css('top', coords.y)
                        .appendTo(canvas);

                var elemInner = angular.element('<div/>')
                        .addClass('inner')
                        .appendTo(elem);

                elem.addClass( (edge.orientation === "VERTICAL") ? 'vertical' : 'horizontal' );

                if (edge.building) {
                    elemInner.addClass('color-' + game.getGameUser(edge.building.ownerGameUserId).colorId);
                }

                if (edge.building && edge.building.built === "ROAD") {
                    elem.addClass('road');
                } else {
                    elem.addClass('none');
                }
            };

            /*MapDrawService.draw = function(map, canvas) {

                var HEX_WIDTH = 68;
                var HEX_HEIGHT = 36;
                var OFFSET_X = 150;
                var OFFSET_Y = 100;
                var PORT_WIDTH = Math.round(HEX_HEIGHT / 3);
                var PORT_HEIGHT = Math.round(HEX_HEIGHT / 3);
                var ROBBED_TEXT = angular.element('<span/>', {'class':'glyphicon glyphicon-fire'});

                var MapService = {
                    getPositionX: function(x, y) {
                        return x * HEX_WIDTH + y * HEX_WIDTH / 2;
                    },
                    getPositionY: function(x, y) {
                        return y * HEX_HEIGHT;
                    },
                    getHexOfNode: function(item) {
                        for (var k in item.hexes) {
                            if (item.hexes[k]) {
                                return item.hexes[k];
                            }
                        }
                    },
                    getPositionNodeX: function(item) {
                        var hex = this.getHexOfNode(item);
                        var position = getPosition(hex.nodes, item);
                        var hexX = this.getPositionX(hex.x, hex.y);
                        var x = hexX - Math.round(PORT_WIDTH/2);

                        if (position === "top" || position === "bottom") { x += Math.round(HEX_WIDTH/2); }
                        if (position === "topRight" || position === "bottomRight") { x += HEX_WIDTH; }

                        return x;
                    },
                    getPositionNodeY: function(item) {
                        var hex = this.getHexOfNode(item);
                        var position = getPosition(hex.nodes, item);
                        var hexY = this.getPositionY(hex.x, hex.y);
                        var y = hexY - Math.round(PORT_HEIGHT/2);

                        if (position === "bottomRight" || position === "bottom" || position === "bottomLeft") { y += HEX_HEIGHT; }

                        return y;
                    }
                };

                for (var item, i = 0, l = map.hexes.length; i < l; i++) {
                    item = map.hexes[i];
                    var elem = angular.element('<div/>')

                            .addClass('hex')
                            .addClass(item.type.toLowerCase())

                            .css('width', HEX_WIDTH + 'px')
                            .css('height', HEX_HEIGHT + 'px')
                            .css('left', OFFSET_X + MapService.getPositionX(item.x, item.y) + 'px')
                            .css('top', OFFSET_Y + MapService.getPositionY(item.x, item.y) + 'px')

                            .appendTo(canvas);

                    angular.element('<div/>')
                            .addClass('dice')
                            .css('width', HEX_WIDTH + 'px')
                            .css('height', HEX_HEIGHT + 'px')
                            .css('font-size', Math.round(HEX_HEIGHT/2) + 'px')
                            .css('line-height', HEX_HEIGHT + 'px')
                            .html((item.robbed)? ROBBED_TEXT : item.dice)
                            .appendTo(elem);
                }

                for (i = 0, l = map.nodes.length; i < l; i++) {
                    item = map.nodes[i];
                    var elem = angular.element('<div/>')

                            .addClass('node')
                            .addClass(item.port.toLowerCase())

                            .css('width', PORT_WIDTH + 'px')
                            .css('height', PORT_HEIGHT + 'px')
                            .css('left', OFFSET_X + MapService.getPositionNodeX(item) + 'px')
                            .css('top', OFFSET_Y + MapService.getPositionNodeY(item) + 'px')

                            .appendTo(canvas);

                    if (item.built === "SETTLEMENT") {
                        elem.addClass('settlement');
                    }

                    if (item.built === "CITY") {
                        elem.addClass('city');
                    }
                }

                function getPosition(where, what) {
                    for (var k in where) {
                        if (where[k] === what) {
                            return k;
                        }
                    }
                }
            };*/

            return MapDrawService;
        }]);