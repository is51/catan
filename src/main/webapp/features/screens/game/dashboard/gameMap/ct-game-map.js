'use strict';

angular.module('catan')

        .directive('ctGameMap', ['MapService', 'MapDrawService', function(MapService, MapDrawService) {
            return {
                restrict: 'E',
                scope: {
                    game: '='
                },
                link: function(scope, element) {
                    MapService.linkEntities(scope.game.map);
                    var canvas = angular.element('<div/>').addClass('canvas').appendTo(element);
                    MapDrawService.drawMap(canvas, scope.game, scope.game.map);
                }
            };

        }]);