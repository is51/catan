'use strict';

angular.module('catan')
        .factory('DrawMapHelper', [function () {

            var DrawMapHelper = {};

            DrawMapHelper.getFirstHexOfNode = function(node) {
                for (var k in node.hexes) {
                    if (node.hexes[k]) {
                        return node.hexes[k];
                    }
                }
                return null;
            };

            DrawMapHelper.getFirstHexOfEdge = function(edge) {
                for (var k in edge.hexes) {
                    if (edge.hexes[k]) {
                        return edge.hexes[k];
                    }
                }
                return null;
            };

            DrawMapHelper.getObjectPosition = function(where, what) {
                for (var k in where) {
                    if (where[k] === what) {
                        return k;
                    }
                }
            };

            DrawMapHelper.getHexCoords = function (hex, hexWidth, hexHeight) {
                return {
                    x: hex.x * hexWidth + hex.y * hexWidth / 2,
                    y: hex.y * hexHeight
                }
            };

            DrawMapHelper.getNodeCoords = function (node, hexWidth, hexHeight) {
                var hex = this.getFirstHexOfNode(node);
                var position = this.getObjectPosition(hex.nodes, node);
                var hexCoords = this.getHexCoords(hex, hexWidth, hexHeight);

                var x = hexCoords.x;
                if (position === "topLeft" || position === "bottomLeft") { x -= Math.round(hexWidth/2); }
                if (position === "topRight" || position === "bottomRight") { x += Math.round(hexWidth/2); }

                var y = hexCoords.y;
                if (position === "topRight" || position === "top" || position === "topLeft") { y -= Math.round(hexHeight/2); }
                if (position === "bottomRight" || position === "bottom" || position === "bottomLeft") { y += Math.round(hexHeight/2); }

                return {x: x, y: y}
            };

            DrawMapHelper.getEdgeCoords = function (edge, hexWidth, hexHeight) {
                var hex = this.getFirstHexOfEdge(edge);
                var position = this.getObjectPosition(hex.edges, edge);
                var hexCoords = this.getHexCoords(hex, hexWidth, hexHeight);

                var x = hexCoords.x;
                if (position === "left") { x -= Math.round(hexWidth/2); }
                if (position === "topLeft" || position === "bottomLeft") { x -= Math.round(hexWidth/4); }
                if (position === "topRight" || position === "bottomRight") { x += Math.round(hexWidth/4); }
                if (position === "right") { x += Math.round(hexWidth/2); }

                var y = hexCoords.y;
                if (position === "topLeft" || position === "topRight") { y -= Math.round(hexHeight/2); }
                if (position === "bottomLeft" || position === "bottomRight") { y += Math.round(hexHeight/2); }

                return {x: x, y: y}
            };

            DrawMapHelper.getPortOffset = function(node) {
                var x, y;

                if (node.orientation === "SINGLE_BOTTOM") {
                    y = (node.hexes.bottom) ? -1 : 1;

                    if (node.hexes.topLeft && !node.hexes.topRight) {
                        x = 1;
                    } else if (!node.hexes.topLeft && node.hexes.topRight) {
                        x = -1;
                    } else {
                        x = 0;
                    }
                }

                if (node.orientation === "SINGLE_TOP") {
                    y = (node.hexes.top) ? 1 : -1;

                    if (node.hexes.bottomLeft && !node.hexes.bottomRight) {
                        x = 1;
                    } else if (!node.hexes.bottomLeft && node.hexes.bottomRight) {
                        x = -1;
                    } else {
                        x = 0;
                    }
                }

                return {x: x, y: y};
            };

            return DrawMapHelper;
        }]);