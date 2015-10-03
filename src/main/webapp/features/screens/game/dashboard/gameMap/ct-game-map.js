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
                    var gameUserId1 = scope.game.gameUsers[0].id;
                    var gameUserId2 = scope.game.gameUsers[1].id;
                    var gameUserId3 = scope.game.gameUsers[2].id;
                    scope.game.map.nodes[3].building = {built: "SETTLEMENT", ownerGameUserId: gameUserId1};
                    scope.game.map.nodes[5].building = {built: "CITY", ownerGameUserId: gameUserId2};
                    scope.game.map.edges[3].building = {built: "ROAD", ownerGameUserId: gameUserId1};
                    scope.game.map.edges[5].building = {built: "ROAD", ownerGameUserId: gameUserId3};

                    var canvas = angular.element('<div/>').addClass('canvas').appendTo(element);
                    MapDrawService.drawMap(canvas, scope.game, scope.game.map);
                }
            };

        }]);