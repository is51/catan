'use strict';

angular.module('catan')
        .directive('ctPlayersPanel', [function() {
            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/playersPanel/ct-players-panel.html",
                link: function(scope) {
                    scope.gameUsers = usersSortedByMoveOrderCurrentUserFirst(scope.game);

                    function usersSortedByMoveOrderCurrentUserFirst(game) {
                        var currentGameUser = game.getCurrentUser();
                        var usersCount = game.gameUsers.length;

                        return game.gameUsers.sort(function(a, b) {
                            var aMoveOrder = a.moveOrder + ((a !== currentGameUser && a.moveOrder < currentGameUser.moveOrder) ? usersCount : 0);
                            var bMoveOrder = b.moveOrder + ((b !== currentGameUser && b.moveOrder < currentGameUser.moveOrder) ? usersCount : 0);
                            return aMoveOrder - bMoveOrder;
                        });
                    }
                }
            };
        }]);