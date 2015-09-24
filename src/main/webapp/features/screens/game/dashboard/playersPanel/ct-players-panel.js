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
                        var absMoveOrders = {};

                        for (var i in game.gameUsers) {
                            var iUser = game.gameUsers[i];
                            if (iUser === currentGameUser || iUser.moveOrder > currentGameUser.moveOrder) {
                                absMoveOrders[iUser.user.id] = iUser.moveOrder;
                            } else {
                                absMoveOrders[iUser.user.id] = iUser.moveOrder + game.gameUsers.length;
                            }
                        }

                        return game.gameUsers.sort(function(a, b) {
                            return absMoveOrders[a.user.id] - absMoveOrders[b.user.id];
                        });
                    }
                }
            };
        }]);