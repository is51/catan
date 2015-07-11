'use strict';

angular.module('catan')
    .directive('ctPlayersList', [function() {
        return {
            restrict: 'E',
            scope: {
                game: '='
            },
            templateUrl: "/features/playersList/ct-players-list.html",
            link: function(scope) {

                scope.game.maxPlayers = 4; // temp, until maxPlayers appears

                scope.$watch('game.gameUsers.length', function() {
                    var vacantPlacesCount = scope.game.maxPlayers - scope.game.gameUsers.length;
                    scope.vacantPlaces = new Array(vacantPlacesCount);
                });

            }
        };
    }]);