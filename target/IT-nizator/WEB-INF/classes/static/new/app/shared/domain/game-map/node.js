System.register([], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var Node, NodeOrientation, NodePort, NodeBuildingType;
    return {
        setters:[],
        execute: function() {
            Node = (function () {
                function Node(params) {
                    this.id = params.nodeId;
                    this.orientation = NodeOrientation[params.orientation];
                    this.port = NodePort[params.port];
                    this.hexesIds = params.hexesIds;
                    this.edgesIds = params.edgesIds;
                    this.hexes = {};
                    this.edges = {};
                    if (params.building) {
                        this.building = {
                            built: NodeBuildingType[params.building.built],
                            ownerPlayerId: params.building.ownerGameUserId
                        };
                    }
                }
                Node.prototype.linkRelatedEntities = function (map) {
                    this._linkEdges(map.edges);
                    this._linkHexes(map.hexes);
                };
                Node.prototype._linkEdges = function (edges) {
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
                Node.prototype._linkHexes = function (hexes) {
                    var _this = this;
                    var _loop_2 = function(i) {
                        var newKey = i.slice(0, -2);
                        this_2.hexes[newKey] = hexes.filter(function (hex) { return hex.id === _this.hexesIds[i]; })[0];
                    };
                    var this_2 = this;
                    for (var i in this.hexesIds) {
                        _loop_2(i);
                    }
                    this._calculateGridCoords();
                };
                Node.prototype._calculateGridCoords = function () {
                    var hex = this.getFirstHex();
                    var x = hex.x + hex.y / 2;
                    var y = hex.y;
                    var position = this.getPosition(hex);
                    if (position === 'topLeft' || position === 'top' || position === 'topRight') {
                        y = y - 0.5;
                    }
                    else {
                        y = y + 0.5;
                    }
                    if (position === 'topLeft' || position === 'bottomLeft') {
                        x = x - 0.5;
                    }
                    else if (position === 'topRight' || position === 'bottomRight') {
                        x = x + 0.5;
                    }
                    this.gridX = x;
                    this.gridY = y;
                };
                Node.prototype.update = function (params) {
                    var buildingChanged = (!this.building && params.building) ||
                        (this.building && params.building && this.building.built !== NodeBuildingType[params.building.built]);
                    if (buildingChanged) {
                        this.building = this.building || {};
                        this.building.built = NodeBuildingType[params.building.built];
                        this.building.ownerPlayerId = params.building.ownerGameUserId;
                        this.triggerUpdate();
                    }
                };
                Node.prototype.getPortToString = function () {
                    return NodePort[this.port];
                };
                Node.prototype.hasPort = function () {
                    return this.port !== NodePort.NONE;
                };
                Node.prototype.hasPortAny = function () {
                    return this.port === NodePort.ANY;
                };
                Node.prototype.hasSettlement = function () {
                    return this.building.built === NodeBuildingType.SETTLEMENT;
                };
                Node.prototype.hasCity = function () {
                    return this.building.built === NodeBuildingType.CITY;
                };
                /*isNeighborOf(node: Node) {
                    return !!this.getJointEdge(node);
                }*/
                Node.prototype.getJointEdge = function (node) {
                    for (var i in node.edges) {
                        for (var j in this.edges) {
                            if (node.edges[i] === this.edges[j]) {
                                return node.edges[i];
                            }
                        }
                    }
                    return null;
                };
                Node.prototype.getFirstHex = function () {
                    for (var k in this.hexes) {
                        if (this.hexes[k]) {
                            return this.hexes[k];
                        }
                    }
                    return null;
                };
                Node.prototype.getPosition = function (hex) {
                    for (var k in hex.nodes) {
                        if (hex.nodes[k] === this) {
                            return k;
                        }
                    }
                };
                Node.prototype.isJoint = function () {
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
                Node.prototype.onUpdate = function (onUpdate) {
                    this._onUpdate = onUpdate;
                };
                Node.prototype.cancelOnUpdate = function () {
                    this._onUpdate = undefined;
                };
                Node.prototype.triggerUpdate = function () {
                    if (this._onUpdate) {
                        this._onUpdate();
                    }
                };
                return Node;
            }());
            exports_1("Node", Node);
            (function (NodeOrientation) {
                NodeOrientation[NodeOrientation["SINGLE_TOP"] = 0] = "SINGLE_TOP";
                NodeOrientation[NodeOrientation["SINGLE_BOTTOM"] = 1] = "SINGLE_BOTTOM";
            })(NodeOrientation || (NodeOrientation = {}));
            exports_1("NodeOrientation", NodeOrientation);
            (function (NodePort) {
                NodePort[NodePort["BRICK"] = 0] = "BRICK";
                NodePort[NodePort["WOOD"] = 1] = "WOOD";
                NodePort[NodePort["SHEEP"] = 2] = "SHEEP";
                NodePort[NodePort["WHEAT"] = 3] = "WHEAT";
                NodePort[NodePort["STONE"] = 4] = "STONE";
                NodePort[NodePort["ANY"] = 5] = "ANY";
                NodePort[NodePort["NONE"] = 6] = "NONE";
            })(NodePort || (NodePort = {}));
            exports_1("NodePort", NodePort);
            (function (NodeBuildingType) {
                NodeBuildingType[NodeBuildingType["SETTLEMENT"] = 0] = "SETTLEMENT";
                NodeBuildingType[NodeBuildingType["CITY"] = 1] = "CITY";
            })(NodeBuildingType || (NodeBuildingType = {}));
        }
    }
});
