'use strict';

angular.module('catan')
        .directive('ctDice',[function() {
            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/dice/ct-dice.html"
            };
        }]);