'use strict';

angular.module('catan')
        .directive('ctGameRoom', [function() {
            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/room/ct-game-room.html"
            };
        }]);