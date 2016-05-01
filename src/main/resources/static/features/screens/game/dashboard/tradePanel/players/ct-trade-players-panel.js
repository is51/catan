'use strict';

angular.module('catan')
        .directive('ctTradePlayersPanel', [function() {

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/tradePanel/players/ct-trade-players-panel.html"
            };
        }]);