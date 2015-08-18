'use strict';

angular.module('catan')
    .directive('ctPlayersList', [function() {
        return {
            restrict: 'E',
            scope: {
                game: '='
            },
            templateUrl: "/features/shared/directives/playersList/ct-players-list.html",
            link: function(scope) {

                scope.game.get().maxPlayers = 4; // temp, until maxPlayers appears

                scope.$watch('game.get().gameUsers.length', function() {
                    var vacantPlacesCount = scope.game.get().maxPlayers - scope.game.get().gameUsers.length;
                    scope.vacantPlaces = new Array(vacantPlacesCount);
                });

            }
        };
    }]);