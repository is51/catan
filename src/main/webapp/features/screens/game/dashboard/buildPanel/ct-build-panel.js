'use strict';

angular.module('catan')
        .directive('ctBuildPanel',[function() {

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/buildPanel/ct-build-panel.html",
                link: function(scope, element) {

                    //TODO: implement actions of building button here (see example in ct-actions-panel)

                }
            };
        }]);