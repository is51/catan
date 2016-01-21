'use strict';

angular.module('catan')
        .directive('ctTradePortPanel', [function() {

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/tradePanel/port/ct-trade-port-panel.html",
                link: function(scope) {



                }
            };
        }]);