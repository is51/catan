'use strict';

angular.module('catan')
        .directive('ctKickOffResourcesPanel', [function() {

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/kickOffResourcesPanel/ct-kick-off-resources-panel.html"
            };

        }]);