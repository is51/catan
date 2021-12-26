System.register(['app/shared/domain/game-map/hex', 'app/shared/domain/game-map/node', 'app/shared/domain/game-map/edge'], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var hex_1, node_1, edge_1;
    var GameMap;
    return {
        setters:[
            function (hex_1_1) {
                hex_1 = hex_1_1;
            },
            function (node_1_1) {
                node_1 = node_1_1;
            },
            function (edge_1_1) {
                edge_1 = edge_1_1;
            }],
        execute: function() {
            GameMap = (function () {
                function GameMap(params) {
                    var _this = this;
                    this.hexes = params.hexes.map(function (hexParams) { return new hex_1.Hex(hexParams); });
                    this.nodes = params.nodes.map(function (nodeParams) { return new node_1.Node(nodeParams); });
                    this.edges = params.edges.map(function (edgeParams) { return new edge_1.Edge(edgeParams); });
                    this.hexes.forEach(function (hex) { return hex.linkRelatedEntities(_this); });
                    this.nodes.forEach(function (node) { return node.linkRelatedEntities(_this); });
                    this.edges.forEach(function (edge) { return edge.linkRelatedEntities(_this); });
                }
                GameMap.prototype.update = function (params) {
                    this.hexes.forEach(function (hex, key) { return hex.update(params.hexes[key]); });
                    this.nodes.forEach(function (node, key) { return node.update(params.nodes[key]); });
                    this.edges.forEach(function (edge, key) { return edge.update(params.edges[key]); });
                };
                GameMap.prototype.getNodeById = function (id) {
                    return this.nodes.filter(function (node) { return node.id === id; })[0];
                };
                return GameMap;
            }());
            exports_1("GameMap", GameMap);
        }
    }
});
