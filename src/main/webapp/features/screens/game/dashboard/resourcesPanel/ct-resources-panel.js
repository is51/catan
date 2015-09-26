'use strict';

angular.module('catan')
        .directive('ctResourcesPanel', [function() {
            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/resourcesPanel/ct-resources-panel.html",
                link: function(scope) {
                    var currentGameUser = scope.game.getCurrentUser();
                    if (currentGameUser) {
                        scope.resources = currentGameUser.resources;
                    }
                }
            };
        }]);