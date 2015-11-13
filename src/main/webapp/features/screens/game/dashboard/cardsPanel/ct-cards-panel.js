'use strict';

angular.module('catan')
        .directive('ctCardsPanel', [function() {

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
                }
            };
        }]);