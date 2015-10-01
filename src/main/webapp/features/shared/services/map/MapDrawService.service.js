'use strict';

angular.module('catan')
        .factory('MapDrawService', [function () {

            var MapDrawService = {};

            MapDrawService.draw = function(map, canvas) {

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

            return MapDrawService;

            function getPosition(where, what) {
                for (var k in where) {
                    if (where[k] === what) {
                        return k;
                    }
                }
            }
        }]);