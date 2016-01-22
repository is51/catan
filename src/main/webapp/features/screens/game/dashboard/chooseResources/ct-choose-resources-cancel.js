'use strict';

angular.module('catan')
        .directive('ctChooseResourcesCancel', ['SelectService', function(SelectService) {

            return {
                restrict: 'A',
                scope: {
                    type: "@ctChooseResourcesCancel"
                },
                link: function(scope, element) {

                    element.on("click", function() {
                        SelectService.cancelRequestSelection(scope.type);
                    });

                }
            };

        }]);