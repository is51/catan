'use strict';

angular.module('catan')
        .factory('DrawMapService', ['DrawMapHelper', '$document', function (DrawMapHelper, $document) {

            var HEX_WIDTH = 78;
            var HEX_HEIGHT = 40;

            var EDGE_WIDTH = 3;

            var DrawMapService = {};

            DrawMapService.HEX_SELECTOR = '.hex';
            DrawMapService.NODE_SELECTOR = '.node';
            DrawMapService.EDGE_SELECTOR = '.edge';

            DrawMapService.drawMap = function(canvas, game, map) {

                var that = this;

                canvas.clear();

                var coords;
                var actualSize = { //TODO: try to calculate actual image (map) size somehow more pretty
                    xMin: null,
                    xMax: null,
                    yMin: null,
                    yMax: null
                };

                map.hexes.forEach(function(hex) {
                    coords = DrawMapHelper.getHexCoords(hex, HEX_WIDTH, HEX_HEIGHT);
                    actualSize = updateActualSize(actualSize, coords.x, coords.y, HEX_HEIGHT, HEX_HEIGHT);
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

                setViewBox(canvas, actualSize);
            };

            DrawMapService.drawHex = function(canvas, coords, hex) {
                var group = canvas.g()
                        .attr("transform", "translate(" + coords.x + "," + coords.y + ")")
                        .attr("class", "hex")
                        .attr('hex-id', hex.hexId);

                // Background rectangle
                group.rect(-HEX_WIDTH/2, -HEX_HEIGHT/2, HEX_WIDTH, HEX_HEIGHT, 4, 4)
                        .attr("class", "hex-background")
                        .attr('resource-type', hex.type.toLowerCase())
                        .attr("transform", "scale(0.95, 0.9)");

                // Dice number + robber
                var hexDiceText = ((hex.robbed) ? '(R)' : '') + ((hex.dice) ? hex.dice : '');
                group.text(1, 5, hexDiceText)
                        .attr("class", "dice")
                        .attr("text-anchor", "middle");
            };

            DrawMapService.drawNode = function(canvas, game, coords, node) {
                var group = canvas.g()
                        .attr("class", "node")
                        .attr("transform", "translate(" + coords.x + "," + coords.y + ")")
                        .attr('node-id', node.nodeId);

                if (node.port !== "NONE") {
                    this.drawPort(group, DrawMapHelper.getPortOffset(node), node.port);
                }

                if (node.building) {
                    var colorId = game.getGameUser(node.building.ownerGameUserId).colorId;

                    if (node.building.built === "SETTLEMENT") {
                        this.drawSettlement(group, {x: 0, y: 0}, colorId);
                    }
                    if (node.building.built === "CITY") {
                        this.drawCity(group, {x: 0, y: 0}, colorId);
                    }
                } else {
                    this.drawEmptyNode(group, {x: 0, y: 0});
                }
            };

            DrawMapService.drawEmptyNode = function(canvas, coords) {
                var group = canvas.g()
                        .attr("transform", "translate(" + coords.x + "," + coords.y + ")")
                        .attr("class", "blank-node");

                var NODE_BACKGROUND_RADIUS = 7;
                group.circle(0, 0, NODE_BACKGROUND_RADIUS);
            };

            DrawMapService.drawSettlement = function(canvas, coords, colorId) {
                var group = canvas.g()
                        .attr("transform", "translate(" + coords.x + "," + coords.y + ")")
                        .attr("class", "settlement");

                var SETTLEMENT_RADIUS = 9;
                group.circle(0, 0, SETTLEMENT_RADIUS)
                        .attr("player-color", colorId);

                group.text(0, 4, "s")
                        .attr("text-anchor", "middle");
            };

            DrawMapService.drawCity = function(canvas, coords, colorId) {
                var group = canvas.g()
                        .attr("transform", "translate(" + coords.x + "," + coords.y + ")")
                        .attr("class", "city");

                var CITY_RADIUS = 11;
                group.circle(0, 0, CITY_RADIUS)
                        .attr("player-color", colorId);

                group.text(0, 5, "C")
                        .attr("text-anchor", "middle");
            };

            DrawMapService.drawPort = function(canvas, offset, type) {
                var PORT_DISTANCE = 12;
                var group = canvas.g()
                        .attr("transform", "translate(" + offset.x * PORT_DISTANCE + "," + offset.y * PORT_DISTANCE + ")")
                        .attr("class", "port");

                group.path("M0 -6 L-5 3 L5 3 Z")
                        .attr("resource-type", type.toLowerCase());
            };

            DrawMapService.drawEdge = function(canvas, game, coords, edge) {
                var group = canvas.g()
                        .attr("class", "edge")
                        .attr("transform", "translate(" + coords.x + "," + coords.y + ")")
                        .attr('edge-id', edge.edgeId);

                if (edge.building) {
                    var colorId = game.getGameUser(edge.building.ownerGameUserId).colorId;
                    this.drawRoad(group, {x: 0, y: 0}, edge.orientation, colorId);
                } else {
                    this.drawEmptyEdge(group, {x: 0, y: 0}, edge.orientation);
                }
            };

            DrawMapService.drawEmptyEdge = function (canvas, coords, orientation) {
                var group = canvas.g()
                        .attr("transform", "translate(" + coords.x + "," + coords.y + ")")
                        .attr("class", "blank-edge");

                if (orientation === "VERTICAL") {
                    group.rect(-EDGE_WIDTH, -HEX_HEIGHT/2, 2*EDGE_WIDTH, HEX_HEIGHT);
                } else {
                    group.rect(-HEX_WIDTH/4, -EDGE_WIDTH, HEX_WIDTH/2, 2*EDGE_WIDTH);
                }
            };

            DrawMapService.drawRoad = function (canvas, coords, orientation, colorId) {
                var group = canvas.g()
                        .attr("transform", "translate(" + coords.x + "," + coords.y + ")")
                        .attr("class", "road");

                if (orientation === "VERTICAL") {
                    group.rect(-EDGE_WIDTH, -HEX_HEIGHT/2, 2*EDGE_WIDTH, HEX_HEIGHT)
                            .attr("player-color", colorId);
                } else {
                    group.rect(-HEX_WIDTH/4, -EDGE_WIDTH, HEX_WIDTH/2, 2*EDGE_WIDTH)
                            .attr("player-color", colorId);
                }
            };

            return DrawMapService;

            function setViewBox(canvas, actualSize) {
                var additionalWidth = 2 * HEX_WIDTH;
                var additionalHeight = 2.5 * HEX_HEIGHT;
                var width = actualSize.xMax - actualSize.xMin + additionalWidth;
                var height = actualSize.yMax - actualSize.yMin + additionalHeight;
                var offsetX = actualSize.xMin - HEX_WIDTH;
                var offsetY = actualSize.yMin - HEX_HEIGHT;
                canvas.attr('viewBox', offsetX + ' ' + offsetY + ' ' + width + ' ' + height);
            }

            function updateActualSize(oldActualSize, x, y, w, h) {
                var newActualSize = angular.copy(oldActualSize);
                var xLeft = x - w / 2,
                    xRight = x + w / 2,
                    yTop = y - h / 2,
                    yBottom = y + h / 2;

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
            }
        }]);