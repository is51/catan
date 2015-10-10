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

                    scope.isActive = function(gameUser) {
                        return scope.game.currentMove === gameUser.moveOrder;
                    };

                    function usersSortedByMoveOrderCurrentUserFirst(game) {
                        var currentGameUser = game.getCurrentUser();
                        var gameUsers = game.gameUsers.map(function(user) { return user; });
                        var usersCount = gameUsers.length;

                        return gameUsers.sort(function(a, b) {
                            var aMoveOrder = a.moveOrder + ((a !== currentGameUser && a.moveOrder < currentGameUser.moveOrder) ? usersCount : 0);
                            var bMoveOrder = b.moveOrder + ((b !== currentGameUser && b.moveOrder < currentGameUser.moveOrder) ? usersCount : 0);

                            return aMoveOrder - bMoveOrder;
                        });
                    }
                }
            };
        }]);