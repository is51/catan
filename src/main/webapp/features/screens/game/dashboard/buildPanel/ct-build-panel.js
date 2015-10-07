'use strict';

angular.module('catan')
        .directive('ctBuildPanel',[function() {

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/buildPanel/ct-build-panel.html",
                link: function(scope) {

                    scope.buildSettlement = function() {
                        alert('Not implemented yet!');
                    };

                    scope.buildCity = function() {
                        alert('Not implemented yet!');
                    };

                    scope.buildRoad = function() {
                        alert('Not implemented yet!');
                    };

                }
            };
        }]);