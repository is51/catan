'use strict';

angular.module('catan')
        .directive('ctModalClose',['ModalService', function(ModalService) {

            return {
                restrict: 'A',
                link: function(scope, element) {

                    var modalId = element.closest("ct-modal").attr("modal-id");

                    if (modalId) {
                        element.on("click", function() {
                            ModalService.hide(modalId);
                            scope.$apply();
                        });
                    }
                }
            };
        }]);