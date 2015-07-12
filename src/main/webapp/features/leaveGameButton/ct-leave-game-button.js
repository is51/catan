'use strict';

angular.module('catan')
    .directive('ctLeaveGameButton', ['$location', 'Remote', function($location, Remote) {
        return {
            restrict: 'A',
            scope: {
                game: '='
            },
            link: function(scope, element) {

                element.on('click', function() {
                    Remote.game.leave({gameId: scope.game.gameId})
                        .then(function() {
                            alert('Successful leaving');
                            $location.path("/");
                        }, function(response) {
                            alert('Error: ' + ((response.data && response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });

                    return false;
                });

            }
        };
    }]);