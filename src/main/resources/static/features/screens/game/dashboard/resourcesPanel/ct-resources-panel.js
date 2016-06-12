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
                    scope.resources = function() {
                        var currentGameUser = scope.game.getCurrentUser();
                        return (currentGameUser) ? currentGameUser.resources : {};
                    }
                }
            };
        }]);