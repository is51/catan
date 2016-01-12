'use strict';

angular.module('catan')
        .directive('ctModalWindow',['ModalWindowService', function(ModalWindowService) {

            return {
                restrict: 'E',
                scope: {
                    modalWindowId: "@"
                },
                link: function(scope, element) {

                    // TODO: Provide variable "isVisible" to the scope (just scope.isVisible doesn't work)
                    // or reset somehow ct-choose-resources every time it's shown

                    ModalWindowService.register(scope.modalWindowId);

                    scope.$watch(function() {
                        return ModalWindowService.isVisible(scope.modalWindowId)
                    }, function(isVisible) {
                        if (isVisible) {
                            element.show();
                        } else {
                            element.hide();
                        }
                    });
                }
            };
        }]);