'use strict';

angular.module('catan')
        .directive('ctBackButton', ['$state', '$stateParams', function($state, $stateParams) {
            return {
                restrict: 'A',
                scope: {
                    game: '='
                },
                link: function(scope, element) {

                    element.on('click', function() {
                        if ($stateParams.onBack) {
                            $stateParams.onBack();
                        } else {
                            $state.goPrevious();
                        }

                        return false;
                    });

                }
            };

        }]);