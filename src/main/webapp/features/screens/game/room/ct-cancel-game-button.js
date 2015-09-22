'use strict';

angular.module('catan')
    .directive('ctCancelGameButton', ['$state', 'Remote', function($state, Remote) {
        return {
            restrict: 'A',
            scope: {
                game: '='
            },
            link: function(scope, element) {

                element.on('click', function() {
                    Remote.game.cancel({gameId: scope.game.getId()})
                        .then(function() {
                            $state.go("start");
                        }, function(response) {
                            alert('Error: ' + ((response.data && response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });

                    return false;
                });

            }
        };
    }]);