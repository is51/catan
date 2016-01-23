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

                    var tradePortAction = scope.game.getCurrentUser().availableActions.list.filter(function(item) {
                        return item.code === "TRADE_PORT";
                    })[0];

                    scope.ratio = tradePortAction.params;

                }
            };
        }]);