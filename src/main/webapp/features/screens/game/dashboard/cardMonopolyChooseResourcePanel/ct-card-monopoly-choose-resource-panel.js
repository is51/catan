'use strict';

angular.module('catan')
        .directive('ctCardMonopolyChooseResourcePanel', ['ModalWindowService', function(ModalWindowService) {

            var MODAL_WINDOW_ID = "CARD_MONOPOLY";

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/cardMonopolyChooseResourcePanel/ct-card-monopoly-choose-resource-panel.html",
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