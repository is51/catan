'use strict';

angular.module('catan')
        .directive('ctKickOffResourcesPanel', ['ModalWindowService', function(ModalWindowService) {

            var MODAL_WINDOW_ID = "KICK_OFF_RESOURCES";

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/kickOffResourcesPanel/ct-kick-off-resources-panel.html",
                link: function(scope) {
                    scope.modalWindowId = MODAL_WINDOW_ID;

                    scope.$watch(function() {
                        return ModalWindowService.isVisible(scope.modalWindowId)
                    }, function(isVisible) {
                        scope.isModalWindowVisible = isVisible;
                    });
                }
            };

        }]);