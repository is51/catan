'use strict';

angular.module('catan')
        .directive('ctCardMonopolyChooseResourcePanel', [function() {

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/cardMonopolyChooseResourcePanel/ct-card-monopoly-choose-resource-panel.html"
            };

        }]);