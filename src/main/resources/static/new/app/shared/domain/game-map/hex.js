System.register([], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var Hex, HexType;
    return {
        setters:[],
        execute: function() {
            Hex = (function () {
                function Hex(params) {
                    this.id = params.hexId;
                    this.x = params.x;
                    this.y = params.y;
                    this.type = HexType[params.type];
                    this.dice = params.dice;
                    this.robbed = params.robbed;
                    this.edgesIds = params.edgesIds;
                    this.nodesIds = params.nodesIds;
                    this.edges = {};
                    this.nodes = {};
                }
                Hex.prototype.linkRelatedEntities = function (map) {
                    this._linkEdges(map.edges);
                    this._linkNodes(map.nodes);
                };
                Hex.prototype._linkEdges = function (edges) {
                    var _this = this;
                    var _loop_1 = function(i) {
                        var newKey = i.slice(0, -2);
                        this_1.edges[newKey] = edges.filter(function (edge) { return edge.id === _this.edgesIds[i]; })[0];
                    };
                    var this_1 = this;
                    for (var i in this.edgesIds) {
                        _loop_1(i);
                    }
                };
                Hex.prototype._linkNodes = function (nodes) {
                    var _this = this;
                    var _loop_2 = function(i) {
                        var newKey = i.slice(0, -2);
                        this_2.nodes[newKey] = nodes.filter(function (node) { return node.id === _this.nodesIds[i]; })[0];
                    };
                    var this_2 = this;
                    for (var i in this.nodesIds) {
                        _loop_2(i);
                    }
                };
                Hex.prototype.update = function (params) {
                    if (this.robbed !== params.robbed) {
                        this.robbed = params.robbed;
                        this.triggerUpdate();
                    }
                };
                Hex.prototype.getTypeToString = function () {
                    return HexType[this.type];
                };
                //TODO: try to replace with Subscribable
                Hex.prototype.onUpdate = function (onUpdate) {
                    this._onUpdate = onUpdate;
                };
                Hex.prototype.cancelOnUpdate = function () {
                    this._onUpdate = undefined;
                };
                Hex.prototype.triggerUpdate = function () {
                    if (this._onUpdate) {
                        this._onUpdate();
                    }
                };
                return Hex;
            }());
            exports_1("Hex", Hex);
            (function (HexType) {
                HexType[HexType["BRICK"] = 0] = "BRICK";
                HexType[HexType["WOOD"] = 1] = "WOOD";
                HexType[HexType["SHEEP"] = 2] = "SHEEP";
                HexType[HexType["WHEAT"] = 3] = "WHEAT";
                HexType[HexType["STONE"] = 4] = "STONE";
                HexType[HexType["EMPTY"] = 5] = "EMPTY";
            })(HexType || (HexType = {}));
        }
    }
});
