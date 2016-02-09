'use strict';

angular.module('catan')
        .directive('ctModalWindow',['ModalWindowService', function(ModalWindowService) {

            return {
                restrict: 'E',
                link: function(scope, element, attrs) {

                    ModalWindowService.register(attrs.modalWindowId);

                    scope.$watch(function() {
                        return ModalWindowService.isVisible(attrs.modalWindowId)
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