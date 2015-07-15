'use strict';

angular.module('catan')
    .directive('ctCancelGameButton', ['$location', 'Remote', function($location, Remote) {
        return {
            restrict: 'A',
            scope: {
                game: '='
            },
            link: function(scope, element) {

                element.on('click', function() {
                    Remote.game.cancel({gameId: scope.game.gameId})
                        .then(function() {
                            alert('Successful canceling');
                            $location.path("/");
                        }, function(response) {
                            alert('Error: ' + ((response.data && response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });

                    return false;
                });

            }
        };
    }]);