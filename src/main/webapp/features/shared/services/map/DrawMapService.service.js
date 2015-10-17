'use strict';

angular.module('catan')
        .factory('DrawMapService', ['DrawMapHelper', function (DrawMapHelper) {

            var HEX_WIDTH = 78;
            var HEX_HEIGHT = 40;

            var DrawMapService = {};

            DrawMapService.drawMap = function(canvas, game, map) {

                canvas.empty();

                var that = this;
                var coords;

                map.hexes.forEach(function(hex) {
                    coords = DrawMapHelper.getHexCoords(hex, HEX_WIDTH, HEX_HEIGHT);
                    that.drawHex(canvas, coords, hex);
                });

                map.edges.forEach(function(edge) {
                    coords = DrawMapHelper.getEdgeCoords(edge, HEX_WIDTH, HEX_HEIGHT);
                    that.drawEdge(canvas, game, coords, edge
                    );
                });

                map.nodes.forEach(function(node) {
                    coords = DrawMapHelper.getNodeCoords(node, HEX_WIDTH, HEX_HEIGHT);
                    that.drawNode(canvas, game, coords, node
                    );
                });
            };

            DrawMapService.drawHex = function(canvas, coords, hex) {
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

            DrawMapService.drawNode = function(canvas, game, coords, node) {
                var elem = angular.element('<div/>')
                        .addClass('node')
                        .css('left', coords.x)
                        .css('top', coords.y)
                        .appendTo(canvas);

                if (node.port !== "NONE") {
                    this.drawPort(elem, DrawMapHelper.getPortOffset(node), node.port);
                }

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
            };

            DrawMapService.drawEmptyNode = function(canvas, coords) {
                var elem = this.createBlankTemplateOfBuilding(canvas, coords);
                elem.root.addClass('none-node');
            };

            DrawMapService.drawSettlement = function(canvas, coords, colorId) {
                var elem = this.createBlankTemplateOfBuilding(canvas, coords);
                elem.root.addClass('settlement');
                elem.inner
                        .addClass('color-' + colorId)
                        .html("S");
            };

            DrawMapService.drawCity = function(canvas, coords, colorId) {
                var elem = this.createBlankTemplateOfBuilding(canvas, coords);
                elem.root.addClass('city');
                elem.inner
                        .addClass('color-' + colorId)
                        .html("C");
            };

            DrawMapService.drawPort = function(canvas, offset, type) {
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

            DrawMapService.drawEdge = function(canvas, game, coords, edge) {
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

            DrawMapService.drawEmptyEdge = function (canvas, coords, orientation) {
                var elem = this.createBlankTemplateOfBuilding(canvas, coords);
                elem.root
                        .addClass('none-edge')
                        .addClass(orientation);
            };

            DrawMapService.drawRoad = function (canvas, coords, orientation, colorId) {
                var elem = this.createBlankTemplateOfBuilding(canvas, coords);
                elem.root
                        .addClass('road')
                        .addClass(orientation);
                elem.inner
                        .addClass('color-' + colorId);
            };

            DrawMapService.createBlankTemplateOfBuilding = function(canvas, coords) {
                var root = angular.element('<div/>')
                        .addClass('building')
                        .css('left', coords.x)
                        .css('top', coords.y)
                        .appendTo(canvas);

                var inner = angular.element('<div/>')
                        .addClass('inner')
                        .appendTo(root);

                return {root: root, inner: inner};
            };

            return DrawMapService;
        }]);