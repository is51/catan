'use strict';

angular.module('catan')
        .directive('ctActionsPanel',['PlayService', 'GameService' , function(PlayService, GameService) {
            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/actionsPanel/ct-actions-panel.html",
                link: function(scope) {
                    //TODD: think about rename method, ti return false as default
                    scope.endTurnEnabled = function() {
                        //TODO: move logic to gameModel
                        return scope.game.currentMove === scope.game.getCurrentUser().moveOrder;
                    };

                    scope.endTurn = function() {
                        PlayService.endTurn(scope.game)
                                .then(function() {
                                    GameService.refresh(scope.game);
                                }, function(response) {
                                    alert('End turn error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                                });
                    };
                }
            };
        }]);