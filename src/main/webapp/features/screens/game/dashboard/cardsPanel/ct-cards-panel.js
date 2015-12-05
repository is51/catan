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

                    scope.useCardRoadBuilding = function() {
                        ModalWindowService.hide("CARDS_PANEL");
                        PlayService.useCardRoadBuilding(scope.game).then(function(response) {
                            alert("Build " + response.data.roadsCount + " roads");
                            GameService.refresh(scope.game);
                        }, function(reason) {
                            alert('Error: ' + ((reason.data.errorCode) ? reason.data.errorCode : 'unknown'));
                        });
                    };
                }
            };
        }]);