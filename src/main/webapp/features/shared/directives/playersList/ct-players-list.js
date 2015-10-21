'use strict';

angular.module('catan')
    .directive('ctPlayersList', [function() {
        return {
            restrict: 'E',
            scope: {
                game: '=',
                showReadyStatus: '@'
            },
            templateUrl: "/features/shared/directives/playersList/ct-players-list.html",
            link: function(scope) {

                scope.$watch('game.gameUsers.length', function() {
                    var vacantPlacesCount = scope.game.maxPlayers - scope.game.gameUsers.length;
                    scope.vacantPlaces = new Array(vacantPlacesCount);
                });

            }
        };
    }]);