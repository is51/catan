'use strict';

angular.module('catan')
        .directive('ctActionsPanel',['PlayService', 'GameService', 'ModalService', function(PlayService, GameService, ModalService) {
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

                    scope.build = function() {
                        ModalService.toggle("BUILD_PANEL");
                    };
                }
            };
        }]);