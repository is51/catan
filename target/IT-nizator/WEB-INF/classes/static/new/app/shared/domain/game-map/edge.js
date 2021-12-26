System.register([], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var Edge, EdgeOrientation, EdgeBuildingType;
    return {
        setters:[],
        execute: function() {
            Edge = (function () {
                function Edge(params) {
                    this.id = params.edgeId;
                    this.orientation = EdgeOrientation[params.orientation];
                    this.hexesIds = params.hexesIds;
                    this.nodesIds = params.nodesIds;
                    this.hexes = {};
                    this.nodes = {};
                    if (params.building) {
                        this.building = {
                            built: EdgeBuildingType[params.building.built],
                            ownerPlayerId: params.building.ownerGameUserId
                        };
                    }
                }
                Edge.prototype.linkRelatedEntities = function (map) {
                    this._linkNodes(map.nodes);
                    this._linkHexes(map.hexes);
                };
                Edge.prototype._linkNodes = function (nodes) {
                    var _this = this;
                    var _loop_1 = function(i) {
                        var newKey = i.slice(0, -2);
                        this_1.nodes[newKey] = nodes.filter(function (node) { return node.id === _this.nodesIds[i]; })[0];
                    };
                    var this_1 = this;
                    for (var i in this.nodesIds) {
                        _loop_1(i);
                    }
                };
                Edge.prototype._linkHexes = function (hexes) {
                    var _this = this;
                    var _loop_2 = function(i) {
                        var newKey = i.slice(0, -2);
                        this_2.hexes[newKey] = hexes.filter(function (hex) { return hex.id === _this.hexesIds[i]; })[0];
                    };
                    var this_2 = this;
                    for (var i in this.hexesIds) {
                        _loop_2(i);
                    }
                };
                Edge.prototype.update = function (params) {
                    var buildingChanged = (!this.building && params.building) ||
                        (this.building && params.building && this.building.built !== EdgeBuildingType[params.building.built]);
                    if (buildingChanged) {
                        this.building = this.building || {};
                        this.building.built = EdgeBuildingType[params.building.built];
                        this.building.ownerPlayerId = params.building.ownerGameUserId;
                        this.triggerUpdate();
                    }
                };
                Edge.prototype.isVertical = function () {
                    return this.orientation === EdgeOrientation.VERTICAL;
                };
                Edge.prototype.getFirstHex = function () {
                    for (var k in this.hexes) {
                        if (this.hexes[k]) {
                            return this.hexes[k];
                        }
                    }
                    return null;
                };
                Edge.prototype.isJoint = function () {
                    var hexesCount = 0;
                    for (var i in this.hexes) {
                        hexesCount++;
                        if (hexesCount > 1) {
                            return true;
                        }
                    }
                    return false;
                };
                //TODO: try to replace with Subscribable
                Edge.prototype.onUpdate = function (onUpdate) {
                    this._onUpdate = onUpdate;
                };
                Edge.prototype.cancelOnUpdate = function () {
                    this._onUpdate = undefined;
                };
                Edge.prototype.triggerUpdate = function () {
                    if (this._onUpdate) {
                        this._onUpdate();
                    }
                };
                return Edge;
            }());
            exports_1("Edge", Edge);
            (function (EdgeOrientation) {
                EdgeOrientation[EdgeOrientation["BOTTOM_RIGHT"] = 0] = "BOTTOM_RIGHT";
                EdgeOrientation[EdgeOrientation["BOTTOM_LEFT"] = 1] = "BOTTOM_LEFT";
                EdgeOrientation[EdgeOrientation["VERTICAL"] = 2] = "VERTICAL";
            })(EdgeOrientation || (EdgeOrientation = {}));
            exports_1("EdgeOrientation", EdgeOrientation);
            (function (EdgeBuildingType) {
                EdgeBuildingType[EdgeBuildingType["ROAD"] = 0] = "ROAD";
            })(EdgeBuildingType || (EdgeBuildingType = {}));
        }
    }
});
