'use strict';

angular.module('catan')
        .directive('ctTradePanel', ['ModalWindowService', 'PlayService', 'GameService', function(ModalWindowService, PlayService, GameService) {

            var PANEL_ID = "TRADE_PANEL";

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

                        PlayService.tradePort(scope.game).then(function() {
                            GameService.refresh(scope.game);
                            ModalWindowService.hide(PANEL_ID);
                        }, function(response) {
                            if (response !== "CANCELED") {
                                alert('Trade Port error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                            }
                            if (!scope.isVisibleTradePlayersPanel) {
                                ModalWindowService.hide(PANEL_ID);
                            }
                        });
                    };

                    scope.showTradePlayers = function () {
                        scope.isVisibleTradePortPanel = false;
                        scope.isVisibleTradePlayersPanel = true;
                    };

                    scope.$watch(function() {
                        return ModalWindowService.isVisible(PANEL_ID);
                    }, function(isVisible) {
                        if (isVisible) {
                            scope.showTradePort();
                        } else {
                            scope.isVisibleTradePortPanel = false;
                            scope.isVisibleTradePlayersPanel = false;
                        }
                    });
                }
            };
        }]);