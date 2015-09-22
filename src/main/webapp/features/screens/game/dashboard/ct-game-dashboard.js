'use strict';

angular.module('catan')
        .directive('ctGameDashboard', [function() {
            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/ct-game-dashboard.html"
            };
        }]);