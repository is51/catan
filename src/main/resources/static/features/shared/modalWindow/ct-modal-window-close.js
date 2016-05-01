'use strict';

angular.module('catan')
        .directive('ctModalWindowClose',['ModalWindowService', function(ModalWindowService) {

            return {
                restrict: 'A',
                scope: {
                    modalWindowId: "@ctModalWindowClose"
                },
                link: function(scope, element) {
                    element.on("click", function() {
                        var modalWindowId = (scope.modalWindowId !== "") ? scope.modalWindowId : element.closest("ct-modal-window").attr("modal-window-id");
                        ModalWindowService.hide(modalWindowId);
                        scope.$apply();
                    });
                }
            };
        }]);