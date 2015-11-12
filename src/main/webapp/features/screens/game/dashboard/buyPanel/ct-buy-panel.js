'use strict';

angular.module('catan')
        .directive('ctBuyPanel', ['ModalWindowService', 'PlayService', 'GameService', function(ModalWindowService, PlayService, GameService) {

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/buyPanel/ct-buy-panel.html",
                link: function(scope) {

                    scope.buildSettlement = function() {
                        ModalWindowService.hide("BUY_PANEL");
                        PlayService.buildSettlement(scope.game).then(function() {
                            GameService.refresh(scope.game);
                        }, function(reason) {
                            if (reason !== "CANCELED") {
                                alert("Build settlement error!");
                            }
                        });
                    };

                    scope.buildCity = function() {
                        ModalWindowService.hide("BUY_PANEL");
                        PlayService.buildCity(scope.game).then(function() {
                            GameService.refresh(scope.game);
                        }, function(reason) {
                            if (reason !== "CANCELED") {
                                alert("Build city error!");
                            }
                        });
                    };

                    scope.buildRoad = function() {
                        ModalWindowService.hide("BUY_PANEL");
                        PlayService.buildRoad(scope.game).then(function() {
                            GameService.refresh(scope.game);
                        }, function(reason) {
                            if (reason !== "CANCELED") {
                                alert("Build road error!");
                            }
                        });
                    };

                    scope.buyCard = function() {
                        ModalWindowService.hide("BUY_PANEL");
                        PlayService.buyCard(scope.game).then(function(response) {
                            alert("Bought card: " + response.data.card);
                            GameService.refresh(scope.game);
                        }, function(response) {
                            alert('Buy Card error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                    };

                }
            };
        }]);