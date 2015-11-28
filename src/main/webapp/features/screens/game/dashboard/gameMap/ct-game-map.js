'use strict';

angular.module('catan')

        .directive('ctGameMap', ['DrawMapService', 'SelectService', '$timeout', function(DrawMapService, SelectService, $timeout) {

            var HEXES_HIGHLIGHT_DELAY = 3000;
            var HEXES_HIGHLIGHT_CLASS = "highlighted";

            return {
                restrict: 'E',
                scope: {
                    game: '='
                },
                link: function(scope, element) {

                    var canvas = angular.element('<div/>')
                            .addClass('canvas')
                            .appendTo(element);

                    // TODO: $watchCollection is slowly. Probably some updateDate should be created and watched

                    // TODO: Separate drawing of static and dynamic objects. And update only dynamic objects

                    scope.$watchCollection("game", function(game) {
                        DrawMapService.drawMap(canvas, game, game.map);
                    });

                    scope.$watch("game.dice", function(newDice, oldDice) {
                        if (oldDice !== undefined && oldDice.thrown === false && newDice.thrown === true) {
                            highlightHexes(canvas, scope.game.map, newDice.value);
                        }
                    });

                    element.on('click', DrawMapService.NODE_SELECTOR, function(event) {
                        var nodeId = angular.element(event.target).closest(DrawMapService.NODE_SELECTOR).attr('node-id');
                        SelectService.select('node', nodeId);
                    });

                    element.on('click', DrawMapService.EDGE_SELECTOR, function(event) {
                        var edgeId = angular.element(event.target).closest(DrawMapService.EDGE_SELECTOR).attr('edge-id');
                        SelectService.select('edge', edgeId);
                    });

                }
            };

            //TODO: move that code to some helper?
            function highlightHexes(canvas, map, dice) {
                var hexesToHighlight = map.hexes.filter(function(hex) {
                    return (dice === 7) ? hex.robbed : !hex.robbed && hex.dice === dice;
                });

                var elementsToHighlight = angular.element([]);
                hexesToHighlight.forEach(function(hex) {
                    elementsToHighlight = elementsToHighlight.add(DrawMapService.HEX_SELECTOR + "[hex-id="+hex.hexId+"]", canvas);
                });

                elementsToHighlight.addClass(HEXES_HIGHLIGHT_CLASS);

                $timeout(function() {
                    elementsToHighlight.removeClass(HEXES_HIGHLIGHT_CLASS);
                }, HEXES_HIGHLIGHT_DELAY);
            }

        }]);