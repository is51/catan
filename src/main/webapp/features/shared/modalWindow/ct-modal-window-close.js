'use strict';

angular.module('catan')
        .directive('ctModalWindowClose',['ModalWindowService', function(ModalWindowService) {

            return {
                restrict: 'A',
                link: function(scope, element) {

                    var modalWindowId = element.closest("ct-modal-window").attr("modal-window-id");

                    if (modalWindowId) {
                        element.on("click", function() {
                            ModalWindowService.hide(modalWindowId);
                            scope.$apply();
                        });
                    }
                }
            };
        }]);