'use strict';

angular.module('catan')
        .directive('ctGameResults', [function() {
            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/results/ct-game-results.html",

                link: function(scope) {
                    scope.gameUsers = usersSortedByVictoryPoints(scope.game);

                    scope.winnerName = scope.gameUsers[0].user.username;

                    function usersSortedByVictoryPoints(game) {
                        var gameUsers = game.gameUsers.map(function(user) { return user; });

                        return gameUsers.sort(function(a, b) {
                            return b.displayVictoryPoints - a.displayVictoryPoints;
                        });
                    }
                }
            };
        }]);