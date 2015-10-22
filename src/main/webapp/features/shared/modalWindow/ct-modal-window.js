'use strict';

angular.module('catan')
        .directive('ctModalWindow',['ModalWindowService', function(ModalWindowService) {

            return {
                restrict: 'E',
                scope: {
                    modalWindowId: "@"
                },
                link: function(scope, element) {

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