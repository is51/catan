'use strict';

angular.module('catan')
        .directive('ctCardsPanel', ['ModalWindowService', 'PlayService', 'GameService', function(ModalWindowService, PlayService, GameService) {

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/cardsPanel/ct-cards-panel.html",
                link: function(scope) {
                    scope.cards = function() {
                        var currentGameUser = scope.game.getCurrentUser();
                        return (currentGameUser) ? currentGameUser.developmentCards : {};
                    };

                    scope.useCardYearOfPlenty = function() {
                        ModalWindowService.hide("CARDS_PANEL");
                        PlayService.useCardYearOfPlenty(scope.game).then(function() {
                            GameService.refresh(scope.game);
                        }, function(reason) {
                            if (reason !== "CANCELED") {
                                alert('Error: ' + ((reason.data.errorCode) ? reason.data.errorCode : 'unknown'));
                            }
                        });
                    };

                    scope.useCardMonopoly = function() {
                        ModalWindowService.hide("CARDS_PANEL");
                        PlayService.useCardMonopoly(scope.game).then(function(response) {
                            var count = response.data.resourcesCount;
                            if (count === 0) {
                                alert("You received " + count + " resources because players don't have this type of resource");
                            } else {
                                alert("You received " + count + " resources");
                            }

                            GameService.refresh(scope.game);
                        }, function(reason) {
                            if (reason !== "CANCELED") {
                                alert('Error: ' + ((reason.data.errorCode) ? reason.data.errorCode : 'unknown'));
                            }
                        });
                    };

                    scope.useCardRoadBuilding = function() {
                        ModalWindowService.hide("CARDS_PANEL");
                        PlayService.useCardRoadBuilding(scope.game).then(function(response) {
                            var count = response.data.roadsCount;
                            alert("Build " + count + " road" + ((count===1)?"":"s"));
                            GameService.refresh(scope.game);
                        }, function(reason) {
                            alert('Error: ' + ((reason.data.errorCode) ? reason.data.errorCode : 'unknown'));
                        });
                    };

                    scope.useCardKnight = function() {
                        ModalWindowService.hide("CARDS_PANEL");
                        PlayService.useCardKnight(scope.game).then(function() {
                            GameService.refresh(scope.game);
                        }, function(reason) {
                            alert('Error: ' + ((reason.data.errorCode) ? reason.data.errorCode : 'unknown'));
                        });
                    };
                }
            };
        }]);