'use strict';

angular.module('catan')
        .directive('ctTradePanel', ['ModalWindowService', function(ModalWindowService) {

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/tradePanel/ct-trade-panel.html",
                link: function(scope) {

                    scope.isVisibleTradePortPanel = false;
                    scope.isVisibleTradePlayersPanel = false;

                    scope.showTradePort = function () {
                        scope.isVisibleTradePortPanel = true;
                        scope.isVisibleTradePlayersPanel = false;
                    };

                    scope.showTradePlayers = function () {
                        scope.isVisibleTradePortPanel = false;
                        scope.isVisibleTradePlayersPanel = true;
                    };

                    scope.$watch(function() {
                        return ModalWindowService.isVisible("TRADE_PANEL");
                    }, function(isVisible) {
                        if (isVisible) {
                            scope.showTradePort();
                        }
                    });
                }
            };
        }]);