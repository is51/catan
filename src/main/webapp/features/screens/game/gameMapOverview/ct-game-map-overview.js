'use strict';

angular.module('catan')
        .directive('ctGameMapOverview', [function() {
            return {
                restrict: 'E',
                scope: {
                    game: '='
                },
                link: function(scope, element) {

                    var map = generateMap(); //var map = scope.game.map
                    var canvas = angular.element('<div/>').addClass('canvas').appendTo(element);
                    drawMap(map, canvas);
                }
            };

            function generateMap() {

                var HEX_TYPES = ["EMPTY", "BRICK", "WOOD", "SHEEP", "WHEAT", "STONE"];
                var NODE_PORTS = ["NONE", "BRICK", "WOOD", "SHEEP", "WHEAT", "STONE", "ALL"];

                var mapSource = [
                    [null,  null,   {},     {},     {}],
                    [null,  {},     {},     {},     {}],
                    [{},    {},     {},     {},     {}],
                    [{},    {},     {},     {},     null],
                    [{},    {},     {},     null,   null]
                ];

                var map = {
                    hexes: [],
                    nodes: []
                };

                var isAlreadyRobbed = false;

                for (var i = 0, l = mapSource.length; i < l; i++) {
                    for (var j = 0, lj = mapSource[i].length; j < lj; j++) {
                        if (mapSource[i][j]) {
                            var r = Math.round(Math.random() * 180 + 50);
                            var g = Math.round(Math.random() * 180 + 50);
                            var b = Math.round(Math.random() * 180 + 50);

                            var hex = {
                                x: j - 2,
                                y: i - 2,
                                type: HEX_TYPES[Math.round(Math.random()*5)],
                                dice: Math.round(Math.random()*10) + 2,
                                robbed: false,
                                nodes: [{}, {}, {}, {}, {}, {}]
                            };

                            if (hex.type === "EMPTY") {
                                hex.dice = null;
                                hex.robbed = !isAlreadyRobbed;
                                isAlreadyRobbed = true;
                            }

                            if (hex.dice === 7) {
                                hex.dice = Math.round(Math.random()) * 2 + 6;
                            }

                            map.hexes.push(hex);
                        }
                    }
                }

                map.nodes[0] = {port: NODE_PORTS[Math.round(Math.random()*5+1)], hexes: [map.hexes[2]]};
                map.nodes[1] = {port: NODE_PORTS[Math.round(Math.random()*5+1)], hexes: [map.hexes[2], map.hexes[6]]};

                map.hexes[2].nodes[2] = map.nodes[0];
                map.hexes[2].nodes[3] = map.nodes[1];
                map.hexes[6].nodes[1] = map.nodes[1];

                return map;
            }

            function drawMap(map, canvas) {

                var HEX_WIDTH = 50;
                var HEX_HEIGHT = 25;
                var OFFSET_X = 102;
                var OFFSET_Y = 80;
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
                        for (var k = 0, lk = item.hexes.length; k < lk; k++) {
                            if (item.hexes[k]) {
                                return item.hexes[k];
                            }
                        }
                    },
                    getPositionNodeX: function(item) {
                        var hex = this.getHexOfNode(item);
                        var position = hex.nodes.indexOf(item);
                        var hexX = this.getPositionX(hex.x, hex.y);
                        var x = hexX - Math.round(PORT_WIDTH/2);

                        if (position === 1 || position === 4) { x += Math.round(HEX_WIDTH/2); }
                        if (position === 2 || position === 3) { x += HEX_WIDTH; }

                        return x;
                    },
                    getPositionNodeY: function(item) {
                        var hex = this.getHexOfNode(item);
                        var position = hex.nodes.indexOf(item);
                        var hexY = this.getPositionY(hex.x, hex.y);
                        var y = hexY - Math.round(PORT_HEIGHT/2);

                        if (position === 3 || position === 4 || position === 5) { y += HEX_HEIGHT; }

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
                            .addClass('coords')
                            .css('width', HEX_WIDTH + 'px')
                            .css('height', HEX_HEIGHT + 'px')
                            .css('font-size', Math.round(HEX_HEIGHT/3) + 'px')
                            .html(item.x + ',' + item.y)
                            .appendTo(elem);

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
                    angular.element('<div/>')

                            .addClass('node')
                            .addClass(item.port.toLowerCase())

                            .css('width', PORT_WIDTH + 'px')
                            .css('height', PORT_HEIGHT + 'px')
                            .css('left', OFFSET_X + MapService.getPositionNodeX(item) + 'px')
                            .css('top', OFFSET_Y + MapService.getPositionNodeY(item) + 'px')

                            .appendTo(canvas);
                }
            }

        }]);