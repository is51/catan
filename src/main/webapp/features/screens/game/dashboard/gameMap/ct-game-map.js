'use strict';

angular.module('catan')

        .directive('ctGameMap', ['DrawMapService', 'SelectMapObjectService', function(DrawMapService, SelectMapObjectService) {
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

                    element.on('click', DrawMapService.NODE_SELECTOR, function(event) {
                        var nodeId = angular.element(event.target).closest(DrawMapService.NODE_SELECTOR).attr('node-id');
                        SelectMapObjectService.select('node', nodeId);
                    });

                    element.on('click', DrawMapService.EDGE_SELECTOR, function(event) {
                        var edgeId = angular.element(event.target).closest(DrawMapService.EDGE_SELECTOR).attr('edge-id');
                        SelectMapObjectService.select('edge', edgeId);
                    });
                }
            };

        }]);