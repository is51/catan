'use strict';

angular.module('catan')
        .directive('ctCardYearOfPlentyChooseResourcesPanel', ['ModalWindowService', function(ModalWindowService) {

            var MODAL_WINDOW_ID = "CARD_YEAR_OF_PLENTY";

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/cardYearOfPlentyChooseResourcesPanel/ct-card-year-of-plenty-choose-resources-panel.html",
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