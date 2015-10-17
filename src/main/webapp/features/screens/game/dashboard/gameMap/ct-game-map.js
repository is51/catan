'use strict';

angular.module('catan')

        .directive('ctGameMap', ['DrawMapService', function(DrawMapService) {
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

                }
            };

        }]);