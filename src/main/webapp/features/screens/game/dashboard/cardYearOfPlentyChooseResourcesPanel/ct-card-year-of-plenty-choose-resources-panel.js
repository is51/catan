'use strict';

angular.module('catan')
        .directive('ctCardYearOfPlentyChooseResourcesPanel', [function() {

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/cardYearOfPlentyChooseResourcesPanel/ct-card-year-of-plenty-choose-resources-panel.html"
            };

        }]);