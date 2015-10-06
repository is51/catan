'use strict';

angular.module('catan')
        .directive('ctModal',['ModalService', function(ModalService) {

            return {
                restrict: 'E',
                scope: {
                    modalId: "@"
                },
                link: function(scope, element) {

                    ModalService.register(scope.modalId);

                    scope.$watch(function() {
                        return ModalService.isVisible(scope.modalId)
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