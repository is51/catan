'use strict';

angular.module('catan')

        .directive('ctGameMap', ['MapService', 'MapDrawService', function(MapService, MapDrawService) {
            return {
                restrict: 'E',
                scope: {
                    game: '='
                },
                link: function(scope, element) {
                    var canvas = angular.element('<div/>').addClass('canvas').appendTo(element);

                    // TODO: $watchCollection is slowly. Probably some updateDate should be created and watched

                    // TODO: Separate drawing of static and dynamic objects. And update only dynamic objects

                    // TODO: Rework updating of game!
                    // Because linkEntities is run every game's update, but it is always the same.
                    // (probably skip updating of static objects of map, or store linked object separately)
                    // (move linkEntities to game model/service)

                    scope.$watchCollection("game", function(game) {
                        MapService.linkEntities(game.map);
                        MapDrawService.drawMap(canvas, game, game.map);
                    });

                }
            };

        }]);