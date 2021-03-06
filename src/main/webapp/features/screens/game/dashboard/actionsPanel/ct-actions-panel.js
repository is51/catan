'use strict';

angular.module('catan')
        .directive('ctActionsPanel',['PlayService', 'GameService', 'ModalWindowService', function(PlayService, GameService, ModalWindowService) {
            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/actionsPanel/ct-actions-panel.html",
                link: function(scope) {

                    scope.endTurn = function() {
                        PlayService.endTurn(scope.game)
                                .then(function() {
                                    GameService.refresh(scope.game);
                                }, function(response) {
                                    alert('End turn error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                                });
                    };

                    scope.throwDice = function() {
                        PlayService.throwDice(scope.game)
                                .then(function() {
                                    GameService.refresh(scope.game);
                                }, function(response) {
                                    alert('Throw Dice error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                                });
                    };

                    scope.moveRobber = function() {
                        PlayService.moveRobber(scope.game).then(function() {
                            GameService.refresh(scope.game);
                        }, function(reason) {
                            if (reason !== "CANCELED") {
                                alert("Move robber error!");
                            }
                        });
                    };

                    scope.choosePlayerToRob = function() {
                        PlayService.choosePlayerToRob(scope.game).then(function() {
                            GameService.refresh(scope.game);
                        }, function(reason) {
                            if (reason !== "CANCELED") {
                                alert("Choose Player To Rob error!");
                            }
                        });
                    };

                    scope.kickOffResources = function() {
                        PlayService.kickOffResources(scope.game).then(function() {
                            GameService.refresh(scope.game);
                        }, function(reason) {
                            if (reason !== "CANCELED") {
                                alert("Kick Off Resources error!");
                            }
                        });
                    };

                    scope.build = function() {
                        ModalWindowService.show("BUY_PANEL");
                    };

                    scope.showCards = function() {
                        ModalWindowService.show("CARDS_PANEL");
                    };

                    scope.showTradePanel = function() {
                        ModalWindowService.show("TRADE_PANEL");
                    };

                    scope.showTradeReplyPanel = function() {
                        ModalWindowService.show("TRADE_REPLY_PANEL");
                    };
                }
            };
        }]);