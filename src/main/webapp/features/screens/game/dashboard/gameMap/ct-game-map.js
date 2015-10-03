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

                    // temp buildings
                    console.log(scope.game.map);
                    scope.game.map.nodes[3].building = {built: "SETTLEMENT", ownerGameUserId: 28};
                    scope.game.map.nodes[5].building = {built: "CITY", ownerGameUserId: 29};
                    scope.game.map.edges[3].building = {built: "ROAD", ownerGameUserId: 28};
                    scope.game.map.edges[5].building = {built: "ROAD", ownerGameUserId: 30};

                    var canvas = angular.element('<div/>').addClass('canvas').appendTo(element);
                    MapDrawService.drawMap(canvas, scope.game, scope.game.map);
                }
            };

        }]);